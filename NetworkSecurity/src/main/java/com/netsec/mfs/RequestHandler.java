/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */
package com.netsec.mfs;

import com.netsec.commons.ReaderWriter;
import com.netsec.commons.CryptoUtilities;
import com.netsec.messages.CMFS1;
import com.netsec.messages.Wrapper;
import com.netsec.messages.Wrapper2;
import com.netsec.messages.MFSChallengeResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author suparngupta
 */
public class RequestHandler extends Thread {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });

        try {
            //get the stream from the socket
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            //get the cmfs1 message 
            byte[] cmfs1Stream = ReaderWriter.readStream(dis);
            CMFS1 cmfs1 = (CMFS1) ReaderWriter.deserialize(cmfs1Stream);
            
            MFSProvider.printLog("received msg: " + cmfs1.toString());

            //process the client challenge and send the challenge
            byte[] serverChallenge = MFSProvider.processClientChallenge(cmfs1);
            MFSProvider.printLog("sending msg: {" + serverChallenge+"}");
            dos.write(serverChallenge);
            dos.flush();

            //read the challenge response and the ticket2 and ticket3 
            Wrapper2 wrapper = (Wrapper2) ReaderWriter.deserialize(ReaderWriter.readStream(dis));
            byte[] clientRespBuffer = wrapper.getEncryptedBuffer();

            //get the key
            byte[] key = MFSProvider.getCMFSKey(wrapper.getUserId());
            MFSChallengeResponse clientChallengeResp = (MFSChallengeResponse) CryptoUtilities.decryptObject(clientRespBuffer, key);
            MFSProvider.printLog("received msg : " + clientChallengeResp.toString());
            
            byte[] ticket3 = clientChallengeResp.getTicket3();

            CMFS1 mfsfschallenge = MFSProvider.processMFSChallengeResponse(clientChallengeResp, wrapper.getUserId(), wrapper.getFileServerName());

            //open socket to File Server
            Socket fsSocket = new Socket(MFSProvider.getFSIpAddress(wrapper.getFileServerName()), MFSProvider.getFSPort(wrapper.getFileServerName()));
            DataInputStream FSdis = new DataInputStream(fsSocket.getInputStream());
            DataOutputStream FSdos = new DataOutputStream(fsSocket.getOutputStream());

            MFSProvider.printLog("sending msg: "+mfsfschallenge.toString());
            FSdos.write(ReaderWriter.serialize(mfsfschallenge));
            FSdos.flush();

            //Read the challenge response and send the next message (#10)
            Wrapper MFSFSChallengeResponse = (Wrapper) ReaderWriter.deserialize(ReaderWriter.readStream(FSdis));

            byte[] MFStoFS2 = MFSProvider.processMFSFSChallengeResponse(MFSFSChallengeResponse);

            //encrypt the challenge
            CMFS1 MFStoFS2WithTicket3 = new CMFS1();
            MFStoFS2WithTicket3.setChallenge(MFStoFS2);
            MFStoFS2WithTicket3.setTicket(ticket3);

            MFSProvider.printLog("sending msg: "+MFStoFS2WithTicket3.toString());
            FSdos.write(ReaderWriter.serialize(MFStoFS2WithTicket3));
            FSdos.flush();

            //Read in the FS - User Challenge, process and forward to Client
            Wrapper2 UserChallenge = (Wrapper2) ReaderWriter.deserialize(ReaderWriter.readStream(FSdis));
            Wrapper2 UserChallengeForward = MFSProvider.processFSUserChallenge(UserChallenge);

            //send the user challenge to the client
            dos.write(ReaderWriter.serialize(UserChallengeForward));
            dos.flush();

            //get the user challenge reply from client and forward to FS
            Wrapper2 UserChallengeReply = (Wrapper2) ReaderWriter.deserialize(ReaderWriter.readStream(dis));
            Wrapper2 UserChallengeReplyForward = MFSProvider.processFSUserChallengeReply(UserChallengeReply);
            FSdos.write(ReaderWriter.serialize(UserChallengeReplyForward));
            FSdos.flush();

            /*
             To Do(Amruth): As in processFSUserChallengeReply, MFS is not allowed to decrypt and encrypt the challenge
             MFS has to maintain port-fs name and port - user name mapping probably
             Discuss with team again.
             */
            /*
             Forward Challenge Reply from FS to Client
             */
            Wrapper2 FSChallengeReply = (Wrapper2) ReaderWriter.deserialize(ReaderWriter.readStream(FSdis));
            MFSProvider.printLog("recieved msg: "+FSChallengeReply);
            MFSProvider.printLog("forwarding msg: "+FSChallengeReply);
            dos.write(ReaderWriter.serialize(FSChallengeReply));
            dos.flush();

            /*
             Relay file Request to server
             */
            MFSProvider.printLog("relaying file request to file server");
            FSdos.write(ReaderWriter.readStream(dis));
            FSdos.flush();

            /*
             Relay file Response to client
             */
            while (true) {
                MFSProvider.printLog("relaying file response to client");
                dos.write(ReaderWriter.readStream(FSdis));
                dos.flush();
            }
        } catch (Exception e) {
            MFSProvider.printLog("Error: unable to process client request on Master File Server");
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown() {
        try {
            dos.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            MFSProvider.printLog("Error: unable to close the client socket on Master File Server");
            e.printStackTrace();
        }
    }
}
