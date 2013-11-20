/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.messages;

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
    CLOSE_SOCKET,
    TICKET_1,
    TICKET_2,
    TICKET_3,
    CMFS_INTRO,
    CMFS_CHALLENGE_RESPONSE,
    MFS_CHALLENGE_RESPONSE;
}
