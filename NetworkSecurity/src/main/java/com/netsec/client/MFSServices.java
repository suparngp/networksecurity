/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.client;

import com.netsec.commons.CryptoUtilities;
import com.netsec.commons.ReaderWriter;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.CMFSChallengeResponse;
import com.netsec.messages.MFSChallengeResponse;
import com.netsec.messages.FSClientChallenge;
import com.netsec.messages.FileData;
import com.netsec.messages.FilePath;
import com.netsec.messages.FileRequestResponse;
import com.netsec.messages.Nonce;
import com.netsec.messages.Wrapper2;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author suparngupta
 */
public class MFSServices {
    private static final Properties props = new Properties();
    private static byte[] userMFSKey;

    static {
        try {
            props.load(new FileInputStream("client.props"));
        } catch (IOException e) {
            System.out.println("Error reading properties");
            e.printStackTrace();
        }

    }
    
    private static byte[] getFSUserKey(String fileServer) {
        return(DatatypeConverter.parseBase64Binary(props.getProperty("user.fs."+fileServer)));
    }
    /**
     * Creates a challenge message to be sent from client to the MFS server
     * @param userId the user ID
     * @param fileServerName the file server name to be accessed
     * @return the encrypted byte buffer of challenge message
     * @throws Exception
     */
    public static byte[] createMFSChallenge(String userId, String fileServerName) throws Exception{
        userMFSKey = DatatypeConverter.parseBase64Binary(props.getProperty("user.mfs"));
        CFSIntro intro = new CFSIntro();
        String challenge = String.valueOf(new Random().nextLong());
        intro.setFileServerName(fileServerName);
        intro.setUserId(userId);
        intro.setChallenge(challenge);
        props.setProperty("mfs.challenge." + fileServerName, challenge);
        props.store(new FileOutputStream("client.props"), challenge);
        AuthServices.printLog("prepare MFS challenge: " + intro.toString());
        return CryptoUtilities.encryptObject(intro, userMFSKey);
    }
    
    /**
     * Process the MFS Challenge
     * @param serverChallenge
     * @param ticket2
     * @param ticket3
     * @return 
     * @throws Exception
     */
    public static byte[] processMFSChallenge(byte[] serverChallenge, byte[] ticket2, byte[] ticket3) throws Exception{
        userMFSKey = DatatypeConverter.parseBase64Binary(props.getProperty("user.mfs"));
        CMFSChallengeResponse cmfscr = (CMFSChallengeResponse)CryptoUtilities
                .decryptObject(serverChallenge, userMFSKey);
        AuthServices.printLog("received msg: "+cmfscr);
        long recChallenge = Long.parseLong(cmfscr.getClientChallenge());
        long sentChallenge = Long.parseLong(props.getProperty("mfs.challenge." + cmfscr.getFileServerName()));
        if(sentChallenge - 1 != recChallenge){
            throw new Exception("Error: Challenge received by MFS is incorrect");
        }
        
        long svrChallengeValue = Long.parseLong(cmfscr.getMfsChallenge());
        MFSChallengeResponse mfscr = new MFSChallengeResponse();
        mfscr.setFileServerName(cmfscr.getFileServerName());
        mfscr.setServerChallenge(String.valueOf(svrChallengeValue - 1));
        mfscr.setTicket2(ticket2);
        mfscr.setTicket3(ticket3);
        mfscr.setUserId(cmfscr.getUserId());
        AuthServices.printLog("prepare msg: "+mfscr.toString());
        Wrapper2 wrapper = new Wrapper2();
        wrapper.setUserId(cmfscr.getUserId());
        wrapper.setEncryptedBuffer(CryptoUtilities.encryptObject(mfscr, userMFSKey));
        wrapper.setFileServerName(cmfscr.getFileServerName());
        AuthServices.printLog("sending msg: "+wrapper.toString());
        return ReaderWriter.serialize(wrapper);
    }
    
 
    
    public static Wrapper2 processFSChallenge(Wrapper2 msg) throws Exception{
        
        //get key and decrypt
        FSClientChallenge fsChallenge = (FSClientChallenge)CryptoUtilities.decryptObject(msg.getEncryptedBuffer(), userMFSKey);
        
        AuthServices.printLog("recieved msg: "+fsChallenge.toString());
        
        //get User FS key and decrypt nonce
        byte[] userFSKey = DatatypeConverter.parseBase64Binary(props.getProperty("user.fs."+msg.getFileServerName()));
        Nonce nonce = (Nonce)CryptoUtilities.decryptObject(fsChallenge.getEncryptedChallenge(), userFSKey);
        
        AuthServices.printLog("decrypt nonce: "+nonce.toString());
        
        //prepare challenge response and User to FS challenge
        FSClientChallenge clientReply = new FSClientChallenge();
        clientReply.setFileServerName(msg.getFileServerName());
        clientReply.setUserId(msg.getUserId());
        
        //Create a new challenge
        String newChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("fs."+fsChallenge.getFileServerName()+".challenge", newChallenge);
        props.store(new FileOutputStream("client.props"), null);
        
        Nonce newNonces = new Nonce();
        newNonces.setNonce(String.valueOf(Long.parseLong(nonce.getNonce())-1));
        newNonces.setNonce2(newChallenge);
        
        AuthServices.printLog("prepare nonces:"+newNonces.toString());
        
        byte[] encryptedFSChallenge = CryptoUtilities.encryptObject(newNonces, userFSKey);
        clientReply.setEncryptedChallenge(encryptedFSChallenge);
        AuthServices.printLog("prepare msg: "+encryptedFSChallenge);
        
        //encrypt, and wrap it up 
        Wrapper2 wrapped = new Wrapper2();
        wrapped.setEncryptedBuffer(CryptoUtilities.encryptObject(clientReply, userMFSKey));
        wrapped.setFileServerName(msg.getFileServerName());
        wrapped.setUserId(msg.getUserId());
        
        AuthServices.printLog("sending msg: "+ wrapped.toString());
        
        return wrapped; 
    }
    
    public static void setBlock(FileOutputStream file, FileData fdata) throws Exception {
        int off = fdata.getBlockNo() * fdata.getBlockLength();
        file.write(fdata.getData(), off, fdata.getDataLength());
    }
    
    public static FileRequestResponse processFSChallengeResponse(Wrapper2 fsChallengeResp) throws Exception {
        /*
        Verify the challenge response from file server.
        */
        byte[] fsUserKey = getFSUserKey(fsChallengeResp.getFileServerName());
        Nonce nonces = (Nonce)CryptoUtilities.decryptObject(fsChallengeResp.getEncryptedBuffer(), fsUserKey);
        AuthServices.printLog("received msg: "+fsChallengeResp.toString());
        AuthServices.printLog("decrypt nonces: "+ nonces.toString());
        
        String correctChallengeString = props.getProperty("fs."+fsChallengeResp.getFileServerName()+".challenge");
        Long correctChallenge = Long.parseLong(correctChallengeString);
        Long recievedChallenge = Long.parseLong(nonces.getNonce());
        
        //System.out.println("Received challenge " + recievedChallenge + "Sent challenge " + correctChallenge);
        if(recievedChallenge != correctChallenge-1){
            throw new Exception("Client could not fulfil the FS challenge");
        }
        
        /*
        Create file request.
        */
        FileRequestResponse fileRequest = new FileRequestResponse();
        fileRequest.setUserId(fsChallengeResp.getUserId());
        fileRequest.setFileServerName(fsChallengeResp.getFileServerName());
        fileRequest.setFileReqResp(Boolean.TRUE);
        FilePath file = new FilePath();
        file.setFilepath(props.getProperty("file.request.location"));
        AuthServices.printLog("requesting file "+file.getFilepath());
        byte[] encryptFileRequest = CryptoUtilities.encryptObject(file, fsUserKey);
        fileRequest.setEncryptedBuffer(encryptFileRequest);
        AuthServices.printLog("sending msg: "+ fileRequest.toString());
        
        return(fileRequest);
    }
    
    public static boolean processFileResponse(FileRequestResponse resp, FileOutputStream fops) throws Exception {
        byte[] userKey = getFSUserKey(resp.getFileServerName());
        FileData fdata = (FileData)CryptoUtilities.decryptObject(resp.getEncryptedBuffer(), userKey);
        setBlock(fops, fdata);
        AuthServices.printLog("Block " +fdata.getBlockNo()+ " of file recieved.");
        return(fdata.isMoreData());
    }
    
    
    public static String getReceivedFilesLocation(){
        return props.getProperty("file.received.location");
    }
    
    public static String getMasterFileServerIp(){
        return props.getProperty("connect.mfs.ip");
    }
    
    public static int getMasterFileServerPort(){
        return Integer.parseInt(props.getProperty("connect.mfs.port"));
    }
}
