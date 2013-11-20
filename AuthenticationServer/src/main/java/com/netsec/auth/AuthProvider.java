/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */
package com.netsec.auth;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.ChallengeMessage;
import com.netsec.messages.ClientChallenge;
import com.netsec.messages.Ticket1;
import com.netsec.messages.Ticket2;
import com.netsec.messages.Ticket3;
import com.netsec.messages.TicketsResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author suparngupta
 */
public class AuthProvider {

    private static final Properties props = new Properties();
    
    static{
        try{
            props.load(new FileInputStream("datastore.props"));
            
        }
        catch(IOException e){
            System.out.println("Error: Unable to read the auth server data file");
            e.printStackTrace();
        }
    }

    public static byte[] createChallenge(String userId) throws Exception{
        byte[] key = getUserAuthKey(userId);
        
        String challenge = String.valueOf(new Random().nextLong());
        
        //write the challenge in the data file
        props.setProperty("user." + userId + ".challenge", challenge.toString());
        props.store(new FileOutputStream("datastore.props"), null);
        
        //create the challenge message.
        ChallengeMessage challengeMessage = new ChallengeMessage();
        challengeMessage.setChallenge(challenge);
        challengeMessage.setUserId(userId);
        
        //return the encrypted buffer.
        return CryptoUtilities.encryptObject(challengeMessage, key);
    }

    public static byte[] createTickets(byte[] clientChallenge, String userId) throws Exception{
        byte[] key = getUserAuthKey(userId);
        ClientChallenge cc = (ClientChallenge)CryptoUtilities.decryptObject(clientChallenge, key);
        System.out.println(cc);
        String fileServerName = cc.getFileServerName();
        long expiration = System.currentTimeMillis() + 50000;
        TicketsResponse res = new TicketsResponse();
        res.setClientChallenge(cc.getClientChallenge());
        res.setFileServerName(fileServerName);
        res.setServerChallenge(cc.getServerChallenge());
        res.setUserId(userId);
        
        //user-MFS key
        String umfsKey = CryptoUtilities.createSessionKey();
        
        //user-FS key
        String ufsKey = CryptoUtilities.createSessionKey();
        
        //MFS - FS key for this user
        String fsmfsKey = CryptoUtilities.createSessionKey();
        //save in the data file
        props.setProperty("user." + userId + ".mfs", umfsKey);
        props.setProperty("user." + userId + ".fs." + fileServerName, ufsKey);
        props.setProperty("user." + userId + ".mfs.fs", fsmfsKey);
        props.store(new FileOutputStream("datastore.props"), null);
        //set in the response
        res.setUserFSKey(ufsKey);
        res.setUserMFSKey(umfsKey);
        
        //Ticket 1
        Ticket1 ticket1 = new Ticket1();
        ticket1.setExpiration(expiration);
        ticket1.setServerName(fileServerName);
        ticket1.setUserId(userId);
        ticket1.setUserMFSKey(umfsKey);
        ticket1.setMFSFSKey(fsmfsKey);
        
        //encrypt the ticket with MFS shared key
        byte[] mfsSharedKey = getKey("auth.mfs");
        res.setTicket1(CryptoUtilities.encryptObject(ticket1, mfsSharedKey));
        
        //ticket 2
        Ticket2 ticket2 = new Ticket2();
        ticket2.setExpiration(expiration);
        ticket2.setMFSFSKey(fsmfsKey);
        ticket2.setServerName(fileServerName);
        ticket2.setUserId(userId);
        
        //encrypt the ticket 2 with FS shared key (like accounts server)
        byte[] fsSharedKey = getKey("auth.fs." + fileServerName.toLowerCase());
        res.setTicket2(CryptoUtilities.encryptObject(ticket2, fsSharedKey));
        
        //ticket 3
        Ticket3 ticket3 = new Ticket3();
        ticket3.setExpiration(expiration);
        ticket3.setUserFSKey(ufsKey);
        ticket3.setServerName(fileServerName);
        ticket3.setUserId(userId);
        
        //encrypt ticket 3 with FS shared key like acounts file server
        res.setTicket3(CryptoUtilities.encryptObject(ticket3, fsSharedKey));
        
        //System.out.println(res);
        
        //return the encrypted tickets response object. Key = user's shared auth key
        return CryptoUtilities.encryptObject(res, key);
    }

    private static byte[] getUserAuthKey(String userId) throws Exception{
        String sharedKey = props.getProperty("user." + userId + ".key");
        
        if (sharedKey == null || sharedKey.isEmpty()) {
            throw new Exception("User Not Found");
        }
        byte[] key = DatatypeConverter.parseBase64Binary(sharedKey);
        return key;
    }
    
    private static byte[] getKey(String name) throws Exception{
        String sharedKey = props.getProperty(name);
        
        if (sharedKey == null || sharedKey.isEmpty()) {
            throw new Exception("Key Not Found");
        }
        byte[] key = DatatypeConverter.parseBase64Binary(sharedKey);
        return key;
    }
}