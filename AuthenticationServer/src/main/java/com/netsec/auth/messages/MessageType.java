/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth.messages;

/**
 *
 * @author suparngupta
 */
public enum MessageType {
    INTRO,
    Error,
    CHALLENGE,
    CLIENT_CHALLENGE,
    TICKETS_RESPONSE,
    CLOSE_SOCKET;
}
