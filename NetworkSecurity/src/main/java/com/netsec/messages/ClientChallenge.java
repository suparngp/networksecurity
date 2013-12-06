/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class ClientChallenge extends GenericMessage {
    private String userId;
    private String serverChallenge;
    private String clientChallenge;
    private String fileServerName;

    @Override
    public String toString() {
        return "ClientChallenge{" + "userId=" + userId + ", serverChallenge=" + serverChallenge + ", clientChallenge=" + clientChallenge + ", fileServerName=" + fileServerName + '}';
    }
    
    public ClientChallenge(){
        this.setType(MessageType.CLIENT_CHALLENGE);
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
