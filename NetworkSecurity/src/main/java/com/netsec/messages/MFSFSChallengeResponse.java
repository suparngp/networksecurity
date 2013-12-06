/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author melyssason
 */
public class MFSFSChallengeResponse extends GenericMessage{
    private String mfsChallenge;
    private String userId;
    private String fileServerName;
    private String fsChallenge;

    @Override
    public String toString() {
        return "MFSFSChallengeResponse{" + "userId=" + userId + ", fileServerName="+fileServerName+
                ", mfsChallenge="+mfsChallenge+", fsChallenge="+fsChallenge+'}';
    }

    
    /**
     * @return the mfsChallenge
     */
    public String getmfsChallenge() {
        return mfsChallenge;
    }

    /**
     * @param mfsChallenge the mfsChallenge to set
     */
    public void setmfsChallenge(String mfsChallenge) {
        this.mfsChallenge = mfsChallenge;
    }
    
    /**
     * @return the fsChallenge
     */
    public String getfsChallenge() {
        return fsChallenge;
    }

    /**
     * @param fsChallenge the fsChallenge to set
     */
    public void setfsChallenge(String fsChallenge) {
        this.fsChallenge = fsChallenge;
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
