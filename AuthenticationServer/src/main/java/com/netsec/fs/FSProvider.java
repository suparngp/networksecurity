/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CFSIntro;
import com.netsec.messages.CMFS1;
import com.netsec.messages.MFSChallengeResponse;
import com.netsec.messages.CMFSChallengeResponse; 
import com.netsec.messages.CFSIntro; 
import com.netsec.messages.Ticket2;
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
    public static byte[] processMFSChallenge(CMFS1 MFSChallenge) throws Exception{
        
       //open ticket 2
        byte[] ticket2Stream = MFSChallenge.getTicket();
        Ticket2 ticket2 = (Ticket2)CryptoUtilities.decryptObject(ticket2Stream, fsKey);
        System.out.println(ticket2);
        
        return null; 
    }
}
