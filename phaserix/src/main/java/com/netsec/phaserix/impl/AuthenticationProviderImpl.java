/*
 * This code can only be used upon consent of the authort
 * 
 */
package com.netsec.phaserix.impl;

import com.netsec.phaserix.interfaces.AuthenticationProvider;
import com.netsec.phaserix.messages.Ticket;
import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 *
 * @author suparn
 */
public class AuthenticationProviderImpl implements AuthenticationProvider{

    @Override
    public Ticket createTicket(Integer userId) {
        BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();
        
        //create a token- I am cool:1234:137204839464
        String token = "I am cool:" + userId + ":" + System.currentTimeMillis(); 
        Ticket ticket = new Ticket();
        String hash = bpe.encryptPassword(token);
        ticket.setTicket(hash);
        ticket.setUserId(userId);
        return ticket;
    }
    
}
