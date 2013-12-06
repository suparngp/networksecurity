/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class CFSIntro extends GenericMessage{
    private String userId;
    private String challenge;
    private String fileServerName;

    @Override
    public String toString() {
        return "CFSIntro{" + "userId=" + userId + ", challenge=" + challenge + ", fileServerName=" + fileServerName + '}';
    }

    public CFSIntro(){
        this.setType(MessageType.INTRO);
        this.setTimestamp(System.currentTimeMillis());
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
     * @return the challenge
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * @param challenge the challenge to set
     */
    public void setChallenge(String challenge) {
        this.challenge = challenge;
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
