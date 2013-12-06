/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author suparngupta
 */
public class GenericMessage implements Serializable{
    private long timestamp;
    private MessageType type;

    public GenericMessage(){
        timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "GenericMessage{" + "timestamp=" + timestamp + ", type=" + type + '}';
    }
    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MessageType type) {
        this.type = type;
    }
    
}
