/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class Ticket1 implements Serializable{
    private String userId;
    private String serverName;

    @Override
    public String toString() {
        return "Ticket1{" + "userId=" + userId + ", serverName=" + serverName + ", expiration=" + expiration + ", userMFSKey=" + userMFSKey + ", MFSFSKey=" + MFSFSKey + '}';
    }
    private long expiration;
    private String userMFSKey;
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
