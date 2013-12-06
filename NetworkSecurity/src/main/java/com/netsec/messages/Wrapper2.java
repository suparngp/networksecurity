/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class Wrapper2 implements Serializable{
    private String userId;
    private String fileServerName;
    private byte[] encryptedBuffer;

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

    /**
     * @return the fileServerName
     */
    public String getFileServerName() {
        return fileServerName;
    }

    /**
     * @param fileServerName the fileServerName to set
     */
    public void setFileServerName(String fileServerName) {
        this.fileServerName = fileServerName;
    }
}
