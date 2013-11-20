/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.mfs;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.CMFS1;
import com.netsec.messages.CMFSChallengeResponse;
import com.netsec.messages.Ticket1;
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
    
    
    public byte[] processCMFSChallengeResponse(byte[] encryptedBuffer){
        return null;
    }
}
