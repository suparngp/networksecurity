/*
 * This code can only be used upon consent of the authort
 * 
 */
package com.netsec.phaserix.messages;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author suparn
 */
@XmlRootElement
public class Ticket {
    private Integer userId;
    private String ticket;

    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * @param ticket the ticket to set
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
