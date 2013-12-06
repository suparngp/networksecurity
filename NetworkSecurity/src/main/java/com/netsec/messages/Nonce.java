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
    private String nonce2 = null;


    @Override
    public String toString() {
        return "Nonce{" + "nonce=" + nonce + "nonce2="+nonce2+'}';
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
  
        /**
     * @return the nonce2
     */
    public String getNonce2() {
        return nonce2;
    }

    /**
     * @param n the nonce2 to set
     */
    public void setNonce2(String n) {
        this.nonce2 = n;
    }
}