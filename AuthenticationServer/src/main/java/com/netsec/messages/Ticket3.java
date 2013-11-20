/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class Ticket3 extends GenericMessage{
    private String userId;
    private String serverName;

    private long expiration;
    private String userFSKey;
    
    public Ticket3(){
        this.setType(MessageType.TICKET_3);
    }
    @Override
    public String toString() {
        return "Ticket3{" + "userId=" + userId + ", serverName=" + serverName + ", expiration=" + expiration + ", userFSKey=" + userFSKey + '}';
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
}
