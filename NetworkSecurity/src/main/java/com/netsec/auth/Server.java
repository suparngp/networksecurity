/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author suparngupta
 */
public class Server extends Thread{
    
    
    public void run(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.out.println("AUTH SERVER:\tStopping the server");
                stopServer();
            }
        });
        startServer();
    }
    
    
    
    private static ServerSocket socket;
    public static void startServer(){
        
        try{
            socket = new ServerSocket(1991);
            while(true){
                Socket accept = socket.accept();
                new RequestHandler(accept).start();
            }
            
            
        }
        catch(IOException ioe){
            System.out.println("AUTH SERVER:\tError in the server");
            ioe.printStackTrace();
        }
        
    }
    
    public static void stopServer(){
        if(socket != null){
            try{
                socket.close();
                System.out.println("AUTH SERVER:\tSockets closed");
            }
            catch(IOException e){
                System.out.println("AUTH SERVER:\tUnable to close the server");
                e.printStackTrace();
            }
        }
    }
}
