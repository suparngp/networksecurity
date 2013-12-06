/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */
package com.netsec.client;

import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.ChallengeMessage;
import com.netsec.messages.ClientChallenge;
import com.netsec.messages.GenericMessage;
import com.netsec.messages.TicketsResponse;
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
public class AuthServices {

    private static final Properties props = new Properties();
    private static byte[] authKey;

    static {
        try {
            props.load(new FileInputStream("client.props"));
            authKey = DatatypeConverter.parseBase64Binary(props.getProperty("auth.key"));
        } catch (IOException e) {
            System.out.println("Error reading properties");
            e.printStackTrace();
        }

    }

    public static String getAuthServerIpAddress(){
        return props.getProperty("connect.auth.ip");
    }
    
    public static int getAuthServerPort(){
        return Integer.parseInt(props.getProperty("connect.auth.port"));
    }
    
    public static String getUserId(){
        return props.getProperty("user.id");
    }
    
    public static String getFileServerName(){
        return props.getProperty("access.fs");
    }
    public static byte[] createClientChallenge(ChallengeMessage cm, String fileServerName) throws Exception {
        //create a fresh challenge for the server and save it in client's data file
        String freshChallenge = String.valueOf(new Random().nextLong());
        props.setProperty("auth.challenge." + fileServerName, freshChallenge);
        props.store(new FileOutputStream("client.props"), null);

        //create the client challenge object
        ClientChallenge cc = new ClientChallenge();
        cc.setFileServerName(fileServerName);
        cc.setClientChallenge(freshChallenge);
        cc.setServerChallenge(cm.getChallenge());
        cc.setUserId(props.getProperty("userId"));

        //return the encrypted buffer of the client challenge object.
        return encryptAuthObject(cc);
    }

    public static boolean isClientChallengeSatisfied(TicketsResponse res){
        String challenge = props.getProperty("auth.challenge." + res.getFileServerName());
        return challenge.equals(res.getClientChallenge());
    }
    /**
     * Returns an encrypted byte buffer from the object using Authentication
     * server's shared key
     *
     * @param o
     * @return
     * @throws Exception
     */
    public static byte[] encryptAuthObject(Object o) throws Exception {
        return CryptoUtilities.encryptObject(o, authKey);
    }

    /**
     * Returns a decrypted Generic message from the encrypted buffer using
     * Authentication server's shared key.
     *
     * @param encryptedBuffer
     * @return
     * @throws Exception
     */
    public static GenericMessage decryptAuthObject(byte[] encryptedBuffer) throws Exception {
        return CryptoUtilities.decryptObject(encryptedBuffer, authKey);
    }

    /**
     * Processes the tickets and store the file server keys in the data file for future use.
     * @param tickets the tickets response
     * @throws Exception
     */
    public static void processTickets(TicketsResponse tickets) throws Exception{
        String userFSKey = tickets.getUserFSKey();
        String userMFSKey = tickets.getUserMFSKey();
        props.setProperty("user.mfs", userMFSKey);
        props.setProperty("user.fs." + tickets.getFileServerName(), userFSKey);
        props.store(new FileOutputStream("client.props"), null);
    }
    
}
