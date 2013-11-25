/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.mfs;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.FSClientChallenge;
import com.netsec.messages.CMFS1;
import com.netsec.messages.MFSChallengeResponse;
import com.netsec.messages.CMFSChallengeResponse; 
import com.netsec.messages.MFSFSChallengeResponse;
import com.netsec.messages.CFSIntro; 
import com.netsec.messages.Ticket1;
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
 * @author suparngupta
 */
public class MFSProvider {
    private static final Properties props= new Properties();
    private static byte[] mfsKey;
    static{
        try{
            props.load(new FileInputStream("mfs.props"));
            mfsKey = DatatypeConverter.parseBase64Binary(props.getProperty("mfs.auth.key"));
        }
        catch(IOException e){
            System.out.println("Error: Unable to load MFS data file");
            e.printStackTrace();
        }
    }

    /**
     * Processes the client challenge message
     * @param cmfs1 the client challenge message
     * @return the encrypted MFS challenge
     * @throws Exception
     */
    public static byte[] processClientChallenge(CMFS1 cmfs1) throws Exception{
        
        // decrypt the ticket.
        byte[] ticket1Stream = cmfs1.getTicket();
        Ticket1 ticket1 = (Ticket1)CryptoUtilities.decryptObject(ticket1Stream, mfsKey);
        System.out.println(ticket1);
        
        
        //get the user and MFS 
        String userMFSKeyString = ticket1.getUserMFSKey();
        props.setProperty("user." + ticket1.getUserId() + ".mfs.key", userMFSKeyString);
        props.store(new FileOutputStream("mfs.props"), null);
        byte[] userMFSKey = DatatypeConverter.parseBase64Binary(userMFSKeyString);
        
        //get the MFS and FS key
        String MFSFSKeyString = ticket1.getMFSFSKey(); 
        props.setProperty("fs."+ticket1.getServerName() + ".mfs.key", MFSFSKeyString);
        props.store(new FileOutputStream("mfs.props"), null);
        System.out.println("Stored FS MFS key");
        
        //decrypt the client challenge
        CFSIntro intro = (CFSIntro)CryptoUtilities.decryptObject(cmfs1.getChallenge(), userMFSKey);
        System.out.println(intro);
        
        long clientChallenge = Long.parseLong(intro.getChallenge());
        String serverChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("user." + intro.getUserId() + ".fs." + intro.getFileServerName(), serverChallenge);
        props.store(new FileOutputStream("mfs.props"), null);
        
        CMFSChallengeResponse cmfsRes = new CMFSChallengeResponse();
        cmfsRes.setClientChallenge(String.valueOf(clientChallenge - 1));
        cmfsRes.setFileServerName(intro.getFileServerName());
        cmfsRes.setMfsChallenge(serverChallenge);
        cmfsRes.setUserId(intro.getUserId());
        
        return CryptoUtilities.encryptObject(cmfsRes, userMFSKey);
    }
    
    
    public static CMFS1 processMFSChallengeResponse(MFSChallengeResponse clientChallengeResp, String clientID, String fsID) throws Exception{
        
 
        //TODO: verify the challenge
                
        //Create the FS challenge
        
        //create a fresh challenge for the server and save it in the MFS 
        String freshChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("fs.challenge." + fsID, freshChallenge);
        props.store(new FileOutputStream("mfs.props"), null);

        //Create the Challenge object and message object        
        CMFS1 fsChallengeMessage = new CMFS1();
        fsChallengeMessage.setTicket(clientChallengeResp.getTicket2());
       
        CFSIntro fsChallenge = new CFSIntro();
        fsChallenge.setChallenge(freshChallenge);
        fsChallenge.setFileServerName(fsID);
        fsChallenge.setUserId(clientID);
        
        System.out.print("MFS-FS challenge: ");
        System.out.println(fsChallenge.toString());
        
        //encrypt the challenge message using the MFS-FS key
        byte[] MFSFSKey = DatatypeConverter.parseBase64Binary(props.getProperty("fs."+fsID + ".mfs.key"));
        byte[] challengeEncrypted = CryptoUtilities.encryptObject(fsChallenge, MFSFSKey);
        
        
        fsChallengeMessage.setChallenge(challengeEncrypted);
        
        return fsChallengeMessage; 
    }
    
    public static byte[] getCMFSKey(String userId) throws Exception{
        
        String sharedKey = props.getProperty("user." + userId + ".mfs.key");
        
        if (sharedKey == null || sharedKey.isEmpty()) {
            throw new Exception("User Not Found");
        }
        byte[] key = DatatypeConverter.parseBase64Binary(sharedKey);
        return key;
    }
    
   public static byte[] getFSKey(String fsID) throws Exception{
        
        String sharedKey = props.getProperty("fs."+ fsID + ".mfs.key");
        
        if (sharedKey == null || sharedKey.isEmpty()) {
            throw new Exception("FS Not Found");
        }
        byte[] key = DatatypeConverter.parseBase64Binary(sharedKey);
        return key;
    }
    
    public static byte[] processMFSFSChallengeResponse(Wrapper response) throws Exception{
        
        //retrieve the MFS - FS key
        byte[] key = getFSKey(response.getUserId());
        
        MFSFSChallengeResponse fsChallengeResp = (MFSFSChallengeResponse)CryptoUtilities.decryptObject(response.getEncryptedBuffer(), key); 
        System.out.print("MFS recieved MFS-FS challenge Response: ");
        System.out.println(fsChallengeResp.toString());
        
        //verify the challenge
        String correctChallenge = props.getProperty("fs.challenge."+response.getUserId());
        if(Long.parseLong(fsChallengeResp.getmfsChallenge())+1 != Long.parseLong(correctChallenge)){
            throw new Exception("FS could not fulfil the challenge");
        }
          
       //Create third message
        CFSIntro mfstofs2 = new CFSIntro();
        mfstofs2.setFileServerName(fsChallengeResp.getFileServerName());
        mfstofs2.setUserId(fsChallengeResp.getUserId());
        
        
        Long fsCh = Long.parseLong(fsChallengeResp.getfsChallenge());
        fsCh--; 
        
        mfstofs2.setChallenge(String.valueOf(fsCh));
        
        //encrypt the challenge
        byte[] challengeEncrypted = CryptoUtilities.encryptObject(mfstofs2, key);
        
        return challengeEncrypted; 
    }
    
    public static Wrapper2 processFSUserChallenge(Wrapper2 userChallenge) throws Exception
    {      
        //retrieve the MFS - FS key and MFS - User key
        byte[] MFSFSkey = getFSKey(userChallenge.getFileServerName());
        byte[] CMFSkey = getCMFSKey(userChallenge.getUserId());
        
        //decrypt
        FSClientChallenge cc = (FSClientChallenge)CryptoUtilities.decryptObject(userChallenge.getEncryptedBuffer(), MFSFSkey);
        System.out.println("FSClientChallenge recieved at MFS="+cc.toString());
        
        //Encrypt with FS - User Key
        byte[] encryptedChallenge = CryptoUtilities.encryptObject(cc, CMFSkey);
        
        //wrap
        Wrapper2 reWrapped = new Wrapper2();
        reWrapped.setEncryptedBuffer(encryptedChallenge);
        reWrapped.setFileServerName(userChallenge.getFileServerName());
        reWrapped.setUserId(userChallenge.getUserId());
        
        return reWrapped;  
    }
    
    public static Wrapper2 processFSUserChallengeReply(Wrapper2 userChallenge) throws Exception
    {     
        // note, this is subtly different from processFSUserChallenge in that it uses the keys in reverse. 
        
        //retrieve the MFS - FS key and MFS - User key
        byte[] MFSFSkey = getFSKey(userChallenge.getFileServerName());
        byte[] CMFSkey = getCMFSKey(userChallenge.getUserId());
        
        //decrypt
        FSClientChallenge cc = (FSClientChallenge)CryptoUtilities.decryptObject(userChallenge.getEncryptedBuffer(), CMFSkey);
        System.out.println("FSClientChallengeReply recieved at MFS="+cc.toString());
        
        //Encrypt with MFS - FS Key
        byte[] encryptedChallenge = CryptoUtilities.encryptObject(cc, MFSFSkey);
        
        //wrap
        Wrapper2 reWrapped = new Wrapper2();
        reWrapped.setEncryptedBuffer(encryptedChallenge);
        reWrapped.setFileServerName(userChallenge.getFileServerName());
        reWrapped.setUserId(userChallenge.getUserId());
        
        return reWrapped;  
    }
    
}
