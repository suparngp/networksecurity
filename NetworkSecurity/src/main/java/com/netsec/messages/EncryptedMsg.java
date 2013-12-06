/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class EncryptedMsg implements Serializable{
    
    private byte[] encryptedBuffer;

    /**
     * @return the encryptedBuffer
     */
    public byte[] getEncryptedBuffer() {
        return encryptedBuffer;
    }

    /**
     * @param encryptedBuffer the encryptedBuffer to set
     */
    public void setEncryptedBuffer(byte[] encryptedBuffer) {
        this.encryptedBuffer = encryptedBuffer;
    }
}
