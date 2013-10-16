/*
 * This code can only be used upon consent of the author
 * 
 */
package com.netsec.phaserix.interfaces;

import com.netsec.phaserix.messages.Ticket;

/**
 * Defines a authentication provider. 
 * Any class which provides authentication services must implement this interface
 * @author suparn
 */
public interface AuthenticationProvider {
    
    public Ticket createTicket(Integer userId);
}
