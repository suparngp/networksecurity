/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author melyssason
 */
public class FSClientChallenge extends GenericMessage {
    private String userId;
    private String fileServerName;
    private byte[] encryptedChallenge; 

    @Override
    public String toString() {
        return "FSClientChallenge{" + "userId=" + userId + ", encryptedChallenge=" + encryptedChallenge +", fileServerName=" + fileServerName + '}';
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
     * @return the encryptedChallenge
     */
    public byte[] getEncryptedChallenge() {
        return encryptedChallenge;
    }
    
    /**
     * @param eChallenge the encryptedChallenge to set
     */
    public void setEncryptedChallenge(byte[] eChallenge) {
        this.encryptedChallenge = eChallenge;
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
