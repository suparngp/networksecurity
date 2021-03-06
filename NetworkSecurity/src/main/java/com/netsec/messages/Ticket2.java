/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class Ticket2 extends GenericMessage{
    private String userId;
    private String serverName;
    private long expiration;

    public Ticket2(){
        this.setType(MessageType.TICKET_2);
    }
    @Override
    public String toString() {
        return "Ticket2{" + "userId=" + userId + ", serverName=" + serverName + ", expiration=" + expiration + ", MFSFSKey=" + MFSFSKey + '}';
    }
    private String MFSFSKey;

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
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the expiration
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * @param expiration the expiration to set
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * @return the MFSFSKey
     */
    public String getMFSFSKey() {
        return MFSFSKey;
    }

    /**
     * @param MFSFSKey the MFSFSKey to set
     */
    public void setMFSFSKey(String MFSFSKey) {
        this.MFSFSKey = MFSFSKey;
    }
    
}
