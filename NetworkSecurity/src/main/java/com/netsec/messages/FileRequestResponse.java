/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.netsec.messages;

import java.io.Serializable;

/**
 *
 * @author AMGP
 */
public class FileRequestResponse implements Serializable {
    String userId;
    String fileServerName;
    Boolean fileReqResp; //true for request and false for response
    private byte[] encryptedBuffer;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileServerName() {
        return fileServerName;
    }

    public void setFileServerName(String fileServerName) {
        this.fileServerName = fileServerName;
    }

    public Boolean isFileReqResp() {
        return fileReqResp;
    }

    public void setFileReqResp(Boolean fileReqResp) {
        this.fileReqResp = fileReqResp;
    }

    public byte[] getEncryptedBuffer() {
        return encryptedBuffer;
    }

    public void setEncryptedBuffer(byte[] encryptedBuffer) {
        this.encryptedBuffer = encryptedBuffer;
    }
}
