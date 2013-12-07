/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs.finance;

import com.netsec.commons.ReaderWriter;
import com.netsec.messages.CMFS1;
import com.netsec.messages.Wrapper;
import com.netsec.messages.Wrapper2; 
import com.netsec.messages.FileData;
import com.netsec.messages.FileRequestResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author melyssason
 */
public class FinanceFSRequestHandler extends Thread{
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    
    public FinanceFSRequestHandler(Socket socket){
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
            FinanceFSProvider.printLog("received msg: "+ MFSchallenge);
            //Process Ticket 2 from MFS
            //send challenge response to MFS (msg 9)
            Wrapper MFSchallengeResponse = FinanceFSProvider.processMFSChallenge(MFSchallenge); 
            dos.write(ReaderWriter.serialize(MFSchallengeResponse));
            dos.flush();
            
            
            byte[] MFStoFS2stream = ReaderWriter.readStream(dis);
            CMFS1 MFStoFS2 = (CMFS1)ReaderWriter.deserialize(MFStoFS2stream);
            FinanceFSProvider.printLog("received msg: "+ MFStoFS2stream); 
            //Process Ticket 3 from MFS and process challenge response from MFS
            //Send challenge to Client. (msg 11)
            Wrapper2 UserChallenge = FinanceFSProvider.processMFStoFS2(MFStoFS2);
            dos.write(ReaderWriter.serialize(UserChallenge));
            dos.flush();
            
            byte[] ClienttoFSStream = ReaderWriter.readStream(dis);
            Wrapper2 userChallengeReply = (Wrapper2)ReaderWriter.deserialize(ClienttoFSStream);
            //Process challenge response from Client
            //Send challenge response to Client
            Wrapper2 fsChallengeToUser = FinanceFSProvider.processUserChallengeReply(userChallengeReply);
            dos.write(ReaderWriter.serialize(fsChallengeToUser));
            dos.flush();
            
            
            byte[] fileRequestStream = ReaderWriter.readStream(dis);
            FileRequestResponse fileRequest = (FileRequestResponse)ReaderWriter.deserialize(fileRequestStream);
            FinanceFSProvider.printLog("received msg: "+ fileRequest.toString());
            
            FileInputStream fips = null;
            /*
            Test Code
            */
            try{
                fips = FinanceFSProvider.getFileInputStream(fileRequest);
            }
            catch(Exception e){
                e.printStackTrace();
                dos.write(e.getMessage().getBytes("UTF-8"));
                throw e;
            }
            
            boolean moreData = true;
            int blockNo = 0;
            while(moreData) {
                FileData fdata = FinanceFSProvider.getBlock(fips, blockNo);
                FileRequestResponse fileResponse = FinanceFSProvider.processFileRequest(fileRequest, fdata);
                dos.write(ReaderWriter.serialize(fileResponse));
                dos.flush();
                moreData = fdata.isMoreData();
                //FinanceFSProvider.setBlock(fops, fdata);
                blockNo++;
            }
            
        }
        
        catch(Exception e){
            System.out.println("Error: unable to process client request on File Server");
            e.printStackTrace();
            shutdown();
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