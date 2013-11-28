/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.netsec.messages;

/**
 *
 * @author AMGP
 */
public class FileData extends GenericMessage {
    int blockNo;
    int blockLength;
    int dataLength;

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
    boolean moreData;
    byte[] data;

    public int getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(int blockNo) {
        this.blockNo = blockNo;
    }

    public boolean isMoreData() {
        return moreData;
    }

    public void setMoreData(boolean moreData) {
        this.moreData = moreData;
    }

    public int getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
