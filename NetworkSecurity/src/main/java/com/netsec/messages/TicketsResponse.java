/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class TicketsResponse extends GenericMessage{
    private String serverChallenge;
    private String clientChallenge;
    private String fileServerName;
    private String userId;
    private String userMFSKey;
    private String userFSKey;
    private byte[] ticket1;
    private byte[] ticket2;
    private byte[] ticket3;

    public TicketsResponse(){
        this.setType(MessageType.TICKETS_RESPONSE);
        
    }

    @Override
    public String toString() {
        return "TicketsResponse{" + "serverChallenge=" + serverChallenge + ", clientChallenge=" + clientChallenge + ", fileServerName=" + fileServerName + ", userId=" + userId + ", userMFSKey=" + userMFSKey + ", userFSKey=" + userFSKey + ", ticket1=" + ticket1.length + ", ticket2=" + ticket2.length + ", ticket3=" + ticket3.length + '}';
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
     * @return the userMFSKey
     */
    public String getUserMFSKey() {
        return userMFSKey;
    }

    /**
     * @param userMFSKey the userMFSKey to set
     */
    public void setUserMFSKey(String userMFSKey) {
        this.userMFSKey = userMFSKey;
    }

    /**
     * @return the userFSKey
     */
    public String getUserFSKey() {
        return userFSKey;
    }

    /**
     * @param userFSKey the userFSKey to set
     */
    public void setUserFSKey(String userFSKey) {
        this.userFSKey = userFSKey;
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
     * @return the ticket1
     */
    public byte[] getTicket1() {
        return ticket1;
    }

    /**
     * @param ticket1 the ticket1 to set
     */
    public void setTicket1(byte[] ticket1) {
        this.ticket1 = ticket1;
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
