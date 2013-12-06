/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class CMFS1 implements Serializable{
    private byte[] ticket;
    private byte[] challenge;

    @Override
    public String toString() {
        return "CMFS1{" + "ticket=" + ticket + ", challenge="+challenge+'}';
    }

    /**
     * @return the challenge
     */
    public byte[] getChallenge() {
        return challenge;
    }

    /**
     * @param challenge the challenge to set
     */
    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    /**
     * @return the ticket
     */
    public byte[] getTicket() {
        return ticket;
    }

    /**
     * @param ticket the ticket to set
     */
    public void setTicket(byte[] ticket) {
        this.ticket = ticket;
    }
}
