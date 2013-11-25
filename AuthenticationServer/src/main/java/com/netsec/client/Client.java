/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.client;

import com.google.gson.Gson;
import com.netsec.commons.ReaderWriter;
import com.netsec.messages.CMFS1;
import com.netsec.messages.ChallengeMessage;
import com.netsec.messages.CloseSocket;
import com.netsec.messages.GenericMessage;
import com.netsec.messages.Intro;
import com.netsec.messages.MessageType;
import com.netsec.messages.TicketsResponse;
import com.netsec.messages.Wrapper;
import com.netsec.messages.Wrapper2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author suparngupta
 */
public class Client {
    private static Socket socket;
    private static Socket mfsSocket;
    
    public Client (){
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
            
            //store the keys in the data file.
            AuthServices.processTickets(tickets);
            System.out.println(tickets);

            //verify the client challenge response
            boolean valid = AuthServices.isClientChallengeSatisfied(tickets);
            System.out.println(valid);
            
            //send socket close request
            dos.write(ReaderWriter.serialize(new CloseSocket("Tickets received successfully. Closing the socket")));
            
            //close the auth socket.
            dis.close();
            dos.close();
            socket.close();
            
            //start a socket with 
            mfsSocket = new Socket("localhost", 1992);
            dis = new DataInputStream(mfsSocket.getInputStream());
            dos = new DataOutputStream(mfsSocket.getOutputStream());
            
            //create the client challenge
            byte[] cmfsChallenge = MFSServices.createMFSChallenge("1234", "accounts");
            byte[] ticket1 = tickets.getTicket1();
            CMFS1 cmfs1 = new CMFS1();
            cmfs1.setChallenge(cmfsChallenge);
            cmfs1.setTicket(ticket1);
            
            //send the client challenge
            dos.write(ReaderWriter.serialize(cmfs1));
            dos.flush();
            
            //read the server challenge
            byte[] serverChallenge = ReaderWriter.readStream(dis);
            byte[] mfsChallengeRes = MFSServices.processMFSChallenge(serverChallenge, tickets.getTicket2(), tickets.getTicket3());
            dos.write(mfsChallengeRes);
            dos.flush();
            
            //read the FS challenge
            byte[] fsChallengeStream = ReaderWriter.readStream(dis);
            
            //unwrap and process
            Wrapper2 fsChallenge = (Wrapper2)ReaderWriter.deserialize(fsChallengeStream);
            byte[] MELYSSA = MFSServices.processFSChallenge(fsChallenge);
            
            
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


