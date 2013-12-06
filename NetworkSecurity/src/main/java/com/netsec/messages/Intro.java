/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.util.Date;

/**
 *
 * @author suparngupta
 */
public class Intro extends GenericMessage{
    
    private String userId;
    public Intro(){
        super();
        this.setType(MessageType.INTRO);
        this.setTimestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Intro{" + "userId=" + userId + '}';
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
}
