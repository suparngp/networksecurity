/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.mfs;

import com.netsec.commons.ReaderWriter;
import com.netsec.commons.CryptoUtilities; 
import com.netsec.messages.CMFS1;
import com.netsec.messages.Wrapper;
import com.netsec.messages.Wrapper2; 
import com.netsec.messages.CMFSChallengeResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author suparngupta
 */
public class RequestHandler extends Thread{
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    
    public RequestHandler(Socket socket){
        this.socket = socket;
    }
    
    @Override
    public void run(){
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                shutdown();
            }
        });
        
        try{
            //get the stream from the socket
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            
            //get the cmfs1 message 
            byte[] cmfs1Stream = ReaderWriter.readStream(dis);
            CMFS1 cmfs1 = (CMFS1)ReaderWriter.deserialize(cmfs1Stream);
            
            //process the client challenge and send the challenge
            byte[] serverChallenge = MFSProvider.processClientChallenge(cmfs1);
            dos.write(serverChallenge);
            dos.flush();
            System.out.println("Process Client Challenge and send response.");
            
            //read the challenge response and the ticket2 and ticket3 
            Wrapper2 wrapper = (Wrapper2)ReaderWriter.deserialize(ReaderWriter.readStream(dis));
            System.out.println(wrapper.getUserId());
            byte[] clientRespBuffer = wrapper.getEncryptedBuffer(); 
            CMFS1 mfsfschallenge = MFSProvider.processMFSChallengeResponse(clientRespBuffer, wrapper.getUserId(), wrapper.getFileServerName());
            
            //open socket to File Server
            Socket fsSocket = new Socket("localhost", 1993);
            DataInputStream FSdis = new DataInputStream(fsSocket.getInputStream());
            DataOutputStream FSdos = new DataOutputStream(fsSocket.getOutputStream());
            
            FSdos.write(ReaderWriter.serialize(mfsfschallenge));
            FSdos.flush();
        }
        
        catch(Exception e){
            System.out.println("Error: unable to process client request on Master File Server");
            e.printStackTrace();
        }
    }
    
    public void shutdown(){
        try{
            dos.close();
            dis.close();
            socket.close();
        }
        
        catch(IOException e){
            System.out.println("Error: unable to close the client socket on Master File Server");
            e.printStackTrace();
        }
    }
}
