/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.client;

import com.google.gson.Gson;
import com.netsec.auth.commons.ReaderWriter;
import com.netsec.auth.messages.ChallengeMessage;
import com.netsec.auth.messages.ClientChallenge;
import com.netsec.auth.messages.CloseSocket;
import com.netsec.auth.messages.GenericMessage;
import com.netsec.auth.messages.Intro;
import com.netsec.auth.messages.MessageType;
import com.netsec.auth.messages.TicketsResponse;
import com.netsec.auth.messages.Wrapper;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author suparngupta
 */
public class Client {
    private static Socket socket;
    private AuthServices authServices;
    
    public Client (){
        authServices = new AuthServices();
    }
    public static void main(String[] args) throws Exception {
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                shutdown();
            }
        });
        
        try{
            socket = new Socket("localhost", 1991);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            
            //wrapper to be used later in the code.
            Wrapper wrapper = new Wrapper();
            wrapper.setUserId("1234");
            
            //introduce to the server as user 
            Intro intro = new Intro();
            intro.setUserId("1234");
            dos.write(ReaderWriter.serialize(intro));
            dos.flush();

            //receive the encrypted challenge
            byte[] encryptedChallenge = ReaderWriter.readStream(dis);
            ChallengeMessage challenge = (ChallengeMessage)AuthServices.decryptAuthObject(encryptedChallenge);
            System.out.println(challenge);
            
            //send the client challenge
            byte[] clientChallenge = AuthServices.createClientChallenge(challenge, "accounts");
            wrapper.setEncryptedBuffer(clientChallenge);
            dos.write(ReaderWriter.serialize(wrapper));
            dos.flush();
            
            //receive the tickets
            byte[] encryptedTickets = ReaderWriter.readStream(dis);
            TicketsResponse tickets = (TicketsResponse)AuthServices.decryptAuthObject(encryptedTickets);
            System.out.println(tickets);

            //verify the client challenge response
            boolean valid = AuthServices.isClientChallengeSatisfied(tickets);
            System.out.println(valid);
            
            //send socket close request
            dos.write(ReaderWriter.serialize(new CloseSocket("Tickets received successfully. Closing the socket")));
            dis.close();
            dos.close();
        }
        catch(Exception e){
            System.out.println("Error: Unable to connect to the authentication server");
            e.printStackTrace();
        }
    }

    /**
     * Shutdown hook to cleanly close the sockets.
     */
    public static void shutdown(){
        try{
            socket.close();
        }
        catch(IOException ioe){
            System.out.println("Unable to close the client socket");
            ioe.printStackTrace();
        }
    }
    
    public static MessageType processReply(String reply){
        
        Gson gson = new Gson();
        
        GenericMessage gm = gson.fromJson(reply, GenericMessage.class);
        if(gm.getType() == MessageType.Error){
            System.out.println("Server returned an error. Closing the connection");
            System.exit(0);
        }
        
        return gm.getType();
    }
}
