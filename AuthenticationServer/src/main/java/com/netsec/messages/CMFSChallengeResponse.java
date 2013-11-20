/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class CMFSChallengeResponse extends GenericMessage{
    private String clientChallenge;
    private String mfsChallenge;
    private String userId;
    private String fileServerName;

    @Override
    public String toString() {
        return "CMFSChallengeResponse{" + "clientChallenge=" + clientChallenge + ", mfsChallenge=" + mfsChallenge + ", userId=" + userId + ", fileServerName=" + fileServerName + '}';
    }

    public CMFSChallengeResponse(){
        this.setType(MessageType.CMFS_CHALLENGE_RESPONSE);
        this.setTimestamp(System.currentTimeMillis());
    }
    /**
     * @return the clientChallenge
     */
    public String getClientChallenge() {
        return clientChallenge;
    }

    /**
     * @param clientChallenge the clientChallenge to set
     */
    public void setClientChallenge(String clientChallenge) {
        this.clientChallenge = clientChallenge;
    }

    /**
     * @return the mfsChallenge
     */
    public String getMfsChallenge() {
        return mfsChallenge;
    }

    /**
     * @param mfsChallenge the mfsChallenge to set
     */
    public void setMfsChallenge(String mfsChallenge) {
        this.mfsChallenge = mfsChallenge;
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
