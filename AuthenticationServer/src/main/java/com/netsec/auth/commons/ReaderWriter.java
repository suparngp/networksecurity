/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author suparngupta
 */
public class ReaderWriter {
    
    public static byte[] serialize(Object o) throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(o);
        return os.toByteArray();
    }
    
    public static Object deserialize(byte[] buffer) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
    
    public static byte[] readStream(DataInputStream is) throws Exception{
        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        int next = is.read();
        int size = is.available();
        byte[] temp = new byte[size];
        is.read(temp);
        bis.write(next);
        bis.write(temp);
        return bis.toByteArray();
        
    } 
}
