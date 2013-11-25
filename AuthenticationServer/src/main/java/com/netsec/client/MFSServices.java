/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.client;

import com.netsec.commons.CryptoUtilities;
import com.netsec.commons.ReaderWriter;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.CMFS1;
import com.netsec.messages.CMFSChallengeResponse;
import com.netsec.messages.MFSChallengeResponse;
import com.netsec.messages.Ticket1;
import com.netsec.messages.FSClientChallenge;
import com.netsec.messages.Wrapper;
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
        System.out.println(cmfscr);
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
        Wrapper2 wrapper = new Wrapper2();
        wrapper.setUserId(cmfscr.getUserId());
        wrapper.setEncryptedBuffer(CryptoUtilities.encryptObject(mfscr, userMFSKey));
        wrapper.setFileServerName(cmfscr.getFileServerName());
        return ReaderWriter.serialize(wrapper);
    }
    
    public static Wrapper2 processFSChallenge(Wrapper2 msg) throws Exception{
        
        //get key and decrypt
        FSClientChallenge fsChallenge = (FSClientChallenge)CryptoUtilities.decryptObject(msg.getEncryptedBuffer(), userMFSKey);
        
        System.out.println("Recieved FS Challenge "+fsChallenge.toString());
        
        //get User FS key and decrypt nonce
        byte[] userFSKey = DatatypeConverter.parseBase64Binary(props.getProperty("user.fs."+msg.getFileServerName()));
        Nonce nonce = (Nonce)CryptoUtilities.decryptObject(fsChallenge.getEncryptedChallenge(), userFSKey);
        
        System.out.println(nonce.toString());
        
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
        
        System.out.println("Sending Nonces:"+newNonces.toString());
        
        byte[] encryptedFSChallenge = CryptoUtilities.encryptObject(newNonces, userFSKey);
        clientReply.setEncryptedChallenge(encryptedFSChallenge);
        
        //encrypt, and wrap it up 
        Wrapper2 wrapped = new Wrapper2();
        wrapped.setEncryptedBuffer(CryptoUtilities.encryptObject(clientReply, userMFSKey));
        wrapped.setFileServerName(msg.getFileServerName());
        wrapped.setUserId(msg.getUserId());
        
        return wrapped; 
    }
}
