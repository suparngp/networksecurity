/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

/**
 *
 * @author suparngupta
 */
public class CloseSocket extends GenericMessage{
    private String message;

    @Override
    public String toString() {
        return "CloseSocket{" + "message=" + message + '}';
    }

    public CloseSocket(String message){
        this.setTimestamp(System.currentTimeMillis());
        this.setType(MessageType.CLOSE_SOCKET);
        this.message = message;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
