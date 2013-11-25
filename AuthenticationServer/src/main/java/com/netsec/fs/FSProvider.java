/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.CMFS1;
import com.netsec.messages.MFSFSChallengeResponse;
import com.netsec.messages.CMFSChallengeResponse; 
import com.netsec.messages.Nonce;
import com.netsec.messages.FSClientChallenge;
import com.netsec.messages.CFSIntro; 
import com.netsec.messages.Ticket2;
import com.netsec.messages.Ticket3; 
import com.netsec.messages.Wrapper; 
import com.netsec.messages.Wrapper2;
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
public class FSProvider {
    private static final Properties props= new Properties();
    private static byte[] fsKey;
    static{
        try{
            props.load(new FileInputStream("fs.props"));
            fsKey = DatatypeConverter.parseBase64Binary(props.getProperty("fs.auth.key"));
        }
        catch(IOException e){
            System.out.println("Error: Unable to load FS data file");
            e.printStackTrace();
        }
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
        System.out.println("FS Recieved Ticket 2:"); 
        System.out.println(ticket2);
        
        //get the MFS and FS key
        String MFSFSKeyString = ticket2.getMFSFSKey(); 
        props.setProperty("mfs.key", MFSFSKeyString);
        props.store(new FileOutputStream("fs.props"), null);
        System.out.println("Stored FS MFS key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        
        //decrypt the challenge
        byte[] challengeStream = MFSChallenge.getChallenge();
        CFSIntro challenge = (CFSIntro)CryptoUtilities.decryptObject(challengeStream, MFSFSKey);
        System.out.println("FS Recieved challenge");
        System.out.println(challenge);
        
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
        
        //encrypt with MFS - FS key, and wrap the message
        byte[] encrypted = CryptoUtilities.encryptObject(response, MFSFSKey);
        Wrapper wrapped = new Wrapper();
        wrapped.setUserId(ticket2.getServerName());
        wrapped.setEncryptedBuffer(encrypted);
       
        return wrapped; 
    }
    
    public static Wrapper2 processMFStoFS2(CMFS1 mfstofs2) throws Exception{

        //get the MFS and FS key
        String MFSFSKeyString = props.getProperty("mfs.key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        
       //open ticket 3
        byte[] ticket3Stream = mfstofs2.getTicket();
        Ticket3 ticket3 = (Ticket3)CryptoUtilities.decryptObject(ticket3Stream, fsKey);
        System.out.println("FS Recieved Ticket 3:"); 
        System.out.println(ticket3);
        
        //TODO: verify timestamp in ticket 3
        
        //TODO: verify challenge
        
        //get the FS and client key
        String UserFSKeyString = ticket3.getUserFSKey();
        props.setProperty("user."+ticket3.getUserId()+".key", UserFSKeyString);
        props.store(new FileOutputStream("fs.props"), null);
        System.out.println("Stored FS MFS key");
        byte[] UserFSKey = DatatypeConverter.parseBase64Binary(UserFSKeyString);
        
        FSClientChallenge fsClientChallenge = new FSClientChallenge();
        fsClientChallenge.setFileServerName(ticket3.getServerName());
        fsClientChallenge.setUserId(ticket3.getUserId());
        
        //create a challenge and encrypt it using the User FS key
        String userChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("user."+ticket3.getUserId()+".challenge", userChallenge);
        props.store(new FileOutputStream("fs.props"), null);
        
        System.out.println("nonce="+userChallenge);
        
        Nonce n = new Nonce();
        n.setNonce(userChallenge);
        
        byte[] encryptedUserChallenge = CryptoUtilities.encryptObject(n, UserFSKey);
        
        fsClientChallenge.setEncryptedChallenge(encryptedUserChallenge);
       
        //encrypt the message and wrap it
        byte[] encrypted = CryptoUtilities.encryptObject(fsClientChallenge, MFSFSKey);
        Wrapper2 wrapped = new Wrapper2();
        wrapped.setEncryptedBuffer(encrypted);
        wrapped.setFileServerName(ticket3.getServerName());
        wrapped.setUserId(ticket3.getUserId());
        
        return wrapped;
    }
    
    public static byte[] processUserChallengeReply(Wrapper2 msg) throws Exception{
        
                
        //get the MFS - FS key and the FS - User key
        String MFSFSKeyString = props.getProperty("mfs.key");
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(MFSFSKeyString);
        String FSUserKeyString = props.getProperty("user."+msg.getUserId()+".key");
        byte[] FSUserKey = DatatypeConverter.parseBase64Binary(FSUserKeyString);
        
        //open the message, and decrypt the nonces
        FSClientChallenge clientReply = (FSClientChallenge)CryptoUtilities.decryptObject(msg.getEncryptedBuffer(), MFSFSKey);
        Nonce nonces = (Nonce)CryptoUtilities.decryptObject(clientReply.getEncryptedChallenge(), FSUserKey);
        
        System.out.println("FS recieved nonces:"+nonces.toString());
        
        // verify the challenge I sent
        String correctChallengeString = props.getProperty("user."+msg.getUserId()+".challenge");
        Long correctChallenge = Long.parseLong(correctChallengeString);
        Long recievedChallenge = Long.parseLong(nonces.getNonce());
        
        if(recievedChallenge != correctChallenge-1){
            throw new Exception("Client could not fulfil the FS challenge");
        }
        
        //AMRUTH: start here :) I verfied the challenge in msg#14, you can start building msg #15 here
        
        return null;
        
    }
    
}
