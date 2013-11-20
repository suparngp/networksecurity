/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class MFSChallengeResponse extends GenericMessage{
    private String serverChallenge;
    private String userId;
    private String fileServerName;
    private byte[] ticket2;
    private byte[] ticket3;

    /**
     * @return the serverChallenge
     */
    public String getServerChallenge() {
        return serverChallenge;
    }

    /**
     * @param serverChallenge the serverChallenge to set
     */
    public void setServerChallenge(String serverChallenge) {
        this.serverChallenge = serverChallenge;
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

    /**
     * @return the ticket2
     */
    public byte[] getTicket2() {
        return ticket2;
    }

    /**
     * @param ticket2 the ticket2 to set
     */
    public void setTicket2(byte[] ticket2) {
        this.ticket2 = ticket2;
    }

    /**
     * @return the ticket3
     */
    public byte[] getTicket3() {
        return ticket3;
    }

    /**
     * @param ticket3 the ticket3 to set
     */
    public void setTicket3(byte[] ticket3) {
        this.ticket3 = ticket3;
    }
}
