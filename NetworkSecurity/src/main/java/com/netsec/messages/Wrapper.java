/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class Wrapper implements Serializable{
    private String userId;
    private byte[] encryptedBuffer;

    @Override
    public String toString() {
        return "Wrapper{" + "userId=" + userId + ", encryptedBuffer=" + encryptedBuffer + '}';
    }
    
    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

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
