/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs.accounts;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CMFS1;
import com.netsec.messages.MFSFSChallengeResponse;
import com.netsec.messages.Nonce;
import com.netsec.messages.FSClientChallenge;
import com.netsec.messages.CFSIntro; 
import com.netsec.messages.FileData;
import com.netsec.messages.FilePath;
import com.netsec.messages.FileRequestResponse;
import com.netsec.messages.Ticket2;
import com.netsec.messages.Ticket3; 
import com.netsec.messages.Wrapper; 
import com.netsec.messages.Wrapper2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author melyssason
 */
public class AccountsFSProvider {
    private static final Properties props= new Properties();
    private static byte[] fsKey;
    static{
        try{
            props.load(new FileInputStream("accounts.props"));
            fsKey = DatatypeConverter.parseBase64Binary(props.getProperty("fs.auth.key"));
        }
        catch(IOException e){
            System.out.println("Error: Unable to load FS data file");
            e.printStackTrace();
        }
    }
    
    private static byte[] getUserKey(String userID) {
        String FSUserKeyString = props.getProperty("user."+userID+".key");
        byte[] FSUserKey = DatatypeConverter.parseBase64Binary(FSUserKeyString);
        return(FSUserKey);
    }
    
    /**
     * Processes the MFS challenge message
     * @param MFSChallenge the MFS challenge message
     * @return the encrypted challenge response
     * @throws Exception
     */
    public static Wrapper processMFSChallenge(CMFS1 MFSChallenge) throws Exception{
        
       //open ticket 2
        byte[] ticket2Stream = MFSChallenge.getTicket();
        Ticket2 ticket2 = (Ticket2)CryptoUtilities.decryptObject(ticket2Stream, fsKey);
        printLog("decrypt ticket 2: "+ticket2.toString()); 
        
        //get the MFS and FS key
        String MFSFSKeyString = ticket2.getMFSFSKey(); 
        props.setProperty("mfs.key", MFSFSKeyString);
        props.store(new FileOutputStream("fs.props"), null);
        //System.out.println("Stored FS MFS key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        
        //decrypt the challenge
        byte[] challengeStream = MFSChallenge.getChallenge();
        CFSIntro challenge = (CFSIntro)CryptoUtilities.decryptObject(challengeStream, MFSFSKey);
        printLog("decrypt challenge: "+ challenge.toString());
        
        
        //create the challenge response
        MFSFSChallengeResponse response = new MFSFSChallengeResponse();
        response.setFileServerName(ticket2.getServerName());
        response.setUserId(ticket2.getUserId());
        
        //create a new challenge
        long mfsChallenge = Long.parseLong(challenge.getChallenge());
        String fsChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("mfs.challenge", fsChallenge);
        props.store(new FileOutputStream("fs.props"), null);
        
        response.setmfsChallenge(String.valueOf(mfsChallenge - 1));
        response.setfsChallenge(fsChallenge);
        
        printLog("prepare msg: "+response.toString());
        
        //encrypt with MFS - FS key, and wrap the message
        byte[] encrypted = CryptoUtilities.encryptObject(response, MFSFSKey);
        Wrapper wrapped = new Wrapper();
        wrapped.setUserId(ticket2.getServerName());
        wrapped.setEncryptedBuffer(encrypted);
        
        printLog("sending msg: {"+wrapped.getEncryptedBuffer()+"}");
        
        return wrapped; 
    }
    
    /*
    * Sets server name to accounts in response
    */
    public static Wrapper2 processMFStoFS2(CMFS1 mfstofs2) throws Exception{

        //get the MFS and FS key
        String MFSFSKeyString = props.getProperty("mfs.key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        
       //open ticket 3
        byte[] ticket3Stream = mfstofs2.getTicket();
        Ticket3 ticket3 = (Ticket3)CryptoUtilities.decryptObject(ticket3Stream, fsKey);
        printLog("decrypt ticket 3: " + ticket3.toString()); 
      
        //TODO: verify timestamp in ticket 3
        
        //verify challenge
       
        //decrypt the challenge
        byte[] challengeStream = mfstofs2.getChallenge();
        CFSIntro challenge = (CFSIntro)CryptoUtilities.decryptObject(challengeStream, MFSFSKey);
        printLog("decrypt challenge: "+ challenge.toString());
        
        String correctChallengeString = props.getProperty("mfs.challenge");
        Long correctChallenge = Long.parseLong(correctChallengeString);
        Long recievedChallenge = Long.parseLong(challenge.getChallenge());
        
        if(recievedChallenge != correctChallenge-1){
            throw new Exception("Client could not fulfil the FS challenge");
        }
        
        //get the FS and client key
        String UserFSKeyString = ticket3.getUserFSKey();
        props.setProperty("user."+ticket3.getUserId()+".key", UserFSKeyString);
        props.store(new FileOutputStream("fs.props"), null);
        //System.out.println("Stored FS MFS key");
        byte[] UserFSKey = DatatypeConverter.parseBase64Binary(UserFSKeyString);
        
        FSClientChallenge fsClientChallenge = new FSClientChallenge();
        fsClientChallenge.setFileServerName(ticket3.getServerName());
        fsClientChallenge.setUserId(ticket3.getUserId());
        
        //create a challenge and encrypt it using the User FS key
        String userChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("user."+ticket3.getUserId()+".challenge", userChallenge);
        props.store(new FileOutputStream("fs.props"), null);
        
        Nonce n = new Nonce();
        n.setNonce(userChallenge);
        
        printLog("prepare challenge: challenge="+n.getNonce());
        byte[] encryptedUserChallenge = CryptoUtilities.encryptObject(n, UserFSKey);
        
        fsClientChallenge.setEncryptedChallenge(encryptedUserChallenge);
       
        printLog("prepare msg: "+fsClientChallenge.toString()); 
        //encrypt the message and wrap it
        byte[] encrypted = CryptoUtilities.encryptObject(fsClientChallenge, MFSFSKey);
        Wrapper2 wrapped = new Wrapper2();
        wrapped.setEncryptedBuffer(encrypted);
        wrapped.setFileServerName(ticket3.getServerName());
        wrapped.setUserId(ticket3.getUserId());
        
        printLog("sending msg: {"+wrapped.getEncryptedBuffer()+"}");
        
        return wrapped;
    }
    
    public static Wrapper2 processUserChallengeReply(Wrapper2 msg) throws Exception{
        
                
        //get the MFS - FS key and the FS - User key
        String MFSFSKeyString = props.getProperty("mfs.key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        String FSUserKeyString = props.getProperty("user."+msg.getUserId()+".key");
        byte[] FSUserKey = DatatypeConverter.parseBase64Binary(FSUserKeyString);
        
        //open the message, and decrypt the nonces
        FSClientChallenge clientReply = (FSClientChallenge)CryptoUtilities.decryptObject(msg.getEncryptedBuffer(), MFSFSKey);
        Nonce nonces = (Nonce)CryptoUtilities.decryptObject(clientReply.getEncryptedChallenge(), FSUserKey);
        
        printLog("received msg: "+clientReply.toString()); 
        printLog("decrypt nonces: "+nonces.toString());
        
        // verify the challenge I sent
        String correctChallengeString = props.getProperty("user."+msg.getUserId()+".challenge");
        Long correctChallenge = Long.parseLong(correctChallengeString);
        Long recievedChallenge = Long.parseLong(nonces.getNonce());
        
        if(recievedChallenge != correctChallenge-1){
            throw new Exception("Client could not fulfil the FS challenge");
        }
        
        
        Wrapper2 chRespWrapped = new Wrapper2();        
        Nonce respNonces = new Nonce();
        respNonces.setNonce(String.valueOf(Long.parseLong(nonces.getNonce2())-1));
        byte[] encryptedFSChallenge = CryptoUtilities.encryptObject(respNonces, FSUserKey);
        printLog("prepare nonce: "+respNonces.toString());
        chRespWrapped.setEncryptedBuffer(encryptedFSChallenge);
        chRespWrapped.setFileServerName(msg.getFileServerName());
        chRespWrapped.setUserId(msg.getUserId());
        printLog("send msg: "+chRespWrapped.toString());
        
        //byte[] encryptedWrapper = CryptoUtilities.encryptObject(chRespWrapped, MFSFSKey);
        return chRespWrapped;
        
    }
    
    public static FileInputStream getFileInputStream(FileRequestResponse request) throws Exception {
        byte[] userKey = getUserKey(request.getUserId());
        FilePath fpath = (FilePath)CryptoUtilities.decryptObject(request.getEncryptedBuffer(), userKey);
        System.out.println("File Path " + fpath.getFilepath());
        File file = new File (fpath.getFilepath());
        if(!file.exists()){
            throw new Exception("File does not exist on the server");
        }
        return(new FileInputStream(fpath.getFilepath()));
    }
        
    public static FileData getBlock(FileInputStream file, int block) throws Exception {
        int retVal = 0;
        int blockLen = 4096;
        byte[] data = new byte[blockLen];
        int offset = block * blockLen;
        retVal = file.read(data, offset, blockLen);
        FileData fdata = new FileData();
        fdata.setBlockLength(blockLen);
        fdata.setBlockNo(block);
        fdata.setDataLength(retVal);
        fdata.setData(data);
        fdata.setMoreData(true);
        if(retVal < blockLen) {
            fdata.setMoreData(false);
        }
        return(fdata);
    }
    

   
    public static FileRequestResponse processFileRequest(FileRequestResponse request, FileData fdata) throws Exception {
        byte[] userKey = getUserKey(request.getUserId());
        
        FileRequestResponse fileResponse = new FileRequestResponse();
        fileResponse.setUserId(request.getUserId());
        fileResponse.setFileServerName(request.getFileServerName());
        fileResponse.setFileReqResp(Boolean.FALSE);
        byte[] encryptFileRequest = CryptoUtilities.encryptObject(fdata, userKey);
        fileResponse.setEncryptedBuffer(encryptFileRequest);
        return(fileResponse);
    }
   
    public static void printLog(String s)
    {
        System.out.println("ACCOUNTS SERVER:\t"+s+'\n');
    }
    
}
