/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */
package com.netsec.auth;

import com.google.gson.Gson;
import com.netsec.commons.ReaderWriter;
import com.netsec.messages.CloseSocket;
import com.netsec.messages.ErrorMessage;
import com.netsec.messages.GenericMessage;
import com.netsec.messages.Intro;
import com.netsec.messages.MessageType;
import com.netsec.messages.Wrapper;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

/**
 * Request Handler implementation to handle the requests from the client.
 *
 * @author suparngupta
 */
public class RequestHandler extends Thread {

    private DataInputStream dis;
    private DataOutputStream dos;
    private final Socket socket;
    private final Gson gson = new Gson();
    
    public RequestHandler(Socket socket) {
        super("RequestHandler" + socket.getRemoteSocketAddress().toString());
        this.socket = socket;
    }

    private  void cleanResources(){
        try{
            if(!socket.isClosed()){
                socket.close();
            }
        }
        
        catch(IOException e){
            System.out.println("Error: unable to close an open socket.");
            e.printStackTrace();
        }
        
    }
    @Override
    public void run() {
        try {
            System.out.println("Handling the requests for " + Thread.currentThread().getName());

            Properties props = new Properties();
            props.load(new FileInputStream("datastore.props"));
            //System.out.println(props.getProperty("datastore.name"));
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            byte[] message = ReaderWriter.readStream(dis);
            Intro intro = (Intro)ReaderWriter.deserialize(message);
            System.out.println(intro);
            
            System.out.println(intro);
            
            //create a wrapper instance to be used later on in the code.
            Wrapper wrapper = new Wrapper();
            wrapper.setUserId(intro.getUserId());
            
            //creating a challenge
            byte[] encryptedChallenge = AuthProvider.createChallenge(intro.getUserId());
            //wrapper.setEncryptedBuffer(encryptedChallenge);
            dos.write(encryptedChallenge);
            dos.flush();
            
            byte[] wrapped = ReaderWriter.readStream(dis);
            wrapper = (Wrapper)ReaderWriter.deserialize(wrapped);
            System.out.println(wrapper.getUserId());
            byte[] enClientChallenge =wrapper.getEncryptedBuffer();
            
            byte[] tickets = AuthProvider.createTickets(enClientChallenge, wrapper.getUserId());
            dos.write(tickets);
            dos.flush();

            //close socket response
            byte[] closeSocket  = ReaderWriter.readStream(dis);
            CloseSocket s = (CloseSocket)ReaderWriter.deserialize(closeSocket);
            System.out.println(s);
            
            dis.close();
            dos.close();
            socket.close();

        } catch (Exception ioe) {
            System.out.println("Error: Unable to get Streams from the socket");
            ioe.printStackTrace();
            cleanResources();
        }

    }

    private MessageType getMessageType(String message) {
        Gson gson = new Gson();
        return gson.fromJson(message, GenericMessage.class).getType();
    }
    
    private void checkMessage(String message, MessageType type) throws Exception{
        MessageType found = getMessageType(message);
        if(type != found){
            ErrorMessage em = new ErrorMessage("Invalid Message Sequence");
            dos.writeUTF(gson.toJson(em));
            throw new Exception();
        }
    }
    
}
