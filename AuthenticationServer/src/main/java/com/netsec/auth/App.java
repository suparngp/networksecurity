package com.netsec.auth;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting the authentication server" );
        new Server().start();
        System.out.println("Authentication server has been started");
    }
}
