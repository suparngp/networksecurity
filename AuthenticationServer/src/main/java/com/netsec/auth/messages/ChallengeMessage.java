/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth.messages;

/**
 *
 * @author suparngupta
 */
public class ChallengeMessage extends GenericMessage{
    
    private String challenge;
    private String userId;
    public ChallengeMessage(){
        this.setType(MessageType.CHALLENGE);
        this.setTimestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "ChallengeMessage{" + "challenge=" + challenge + ", userId=" + userId + '}';
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
