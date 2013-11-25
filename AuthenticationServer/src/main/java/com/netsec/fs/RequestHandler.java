/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs;

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
 * @author melyssason
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
            
            //get the MFSFS challenge 
            byte[] cmfs1Stream = ReaderWriter.readStream(dis);
            CMFS1 MFSchallenge = (CMFS1)ReaderWriter.deserialize(cmfs1Stream);
            
            //process and send response
            Wrapper MFSchallengeResponse = FSProvider.processMFSChallenge(MFSchallenge); 
            
            
            dos.write(ReaderWriter.serialize(MFSchallengeResponse));
            dos.flush();
            System.out.println("Process MFS Challenge and send response.");
            
            //get MFStoFS2
            byte[] MFStoFS2stream = ReaderWriter.readStream(dis);
            CMFS1 MFStoFS2 = (CMFS1)ReaderWriter.deserialize(MFStoFS2stream);
            
            Wrapper2 UserChallenge = FSProvider.processMFStoFS2(MFStoFS2);
            
            //send the user challenge (msg #11)
            dos.write(ReaderWriter.serialize(UserChallenge));
            dos.flush();
            System.out.println("Sent FS User Challenge from FS");
            
            //read the user challenge reply (msg #14)
            Wrapper2 userChallengeReply = (Wrapper2)ReaderWriter.deserialize(ReaderWriter.readStream(dis));
            byte [] msg15 = FSProvider.processUserChallengeReply(userChallengeReply);
            
        }
        
        catch(Exception e){
            System.out.println("Error: unable to process client request on File Server");
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
            System.out.println("Error: unable to close the client socket on File Server");
            e.printStackTrace();
        }
    }
}