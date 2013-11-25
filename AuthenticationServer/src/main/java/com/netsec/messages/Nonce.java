/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author melyssason
 */
public class Nonce extends GenericMessage {
    private String nonce;


    @Override
    public String toString() {
        return "Nonce{" + "nonce=" + nonce + '}';
    }
    
    /**
     * @return the nonce
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * @param n the nonce to set
     */
    public void setNonce(String n) {
        this.nonce = n;
    }
  
}