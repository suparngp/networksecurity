/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.fs.finance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author melyssason
 */
public class FinanceFileServer extends Thread{
    ServerSocket socket;
    public void run(){
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run(){
                    shutdown();
                }
            });
            
            socket = new ServerSocket(1994);
            
            while(true){
                Socket accept = socket.accept();
                new FinanceFSRequestHandler(accept).start();
            }
            
                    
        }
        
        catch(Exception e){
            System.out.println("Error in File Server");
            e.printStackTrace();
        }
    }
    
    public void shutdown(){
        try{
            socket.close();
        }
        
        catch(IOException e){
            System.out.println("Error: Unable to shut down the File Server");
            e.printStackTrace();
        }
    }
}
