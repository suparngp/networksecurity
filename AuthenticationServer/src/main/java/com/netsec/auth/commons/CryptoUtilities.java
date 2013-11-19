/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.auth.commons;

import com.netsec.auth.messages.GenericMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author suparngupta
 */
public class CryptoUtilities {
    /**
     *
     * Enrypts an object and returns a byte array of the encrypted data.
     * @param o
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptObject(Object o, byte[] key) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(o);
        byte[] raw = out.toByteArray();
        
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, spec);
        byte[] encrypted =  cipher.doFinal(raw);
        return encrypted;
        
    }
    
    /**
     * Decrypts the encrypted byte buffer and returns the Object.
     * @param encryptedBuffer the encrypted byte buffer
     * @param key the secret key
     * @return the decrypted Object
     * @throws Exception
     */
    public static GenericMessage decryptObject(byte[] encryptedBuffer, byte[] key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        byte[] raw = cipher.doFinal(encryptedBuffer);
        ByteArrayInputStream in = new ByteArrayInputStream(raw);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object readObject = ois.readObject();
        return (GenericMessage)readObject;
    }

    /**
     * Creates random session keys
     * @return
     * @throws Exception
     */
    public static String createSessionKey() throws Exception{
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(128);
        byte[] original = gen.generateKey().getEncoded();
        String s = DatatypeConverter.printBase64Binary(original);
        return s;
    }
}
