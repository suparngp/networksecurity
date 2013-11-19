/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

package com.netsec.client;

import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.xml.bind.DatatypeConverter;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;

/**
 *
 * @author suparngupta
 */
public class Keygenerator {
    
    public static void main(String[] args) throws Exception{
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(128);
        byte[] original = gen.generateKey().getEncoded();
        String s = DatatypeConverter.printBase64Binary(original);
        byte[] convertedBack = DatatypeConverter.parseBase64Binary(s);
        
        System.out.println(Arrays.equals(original, convertedBack));
        System.out.println(DatatypeConverter.printBase64Binary(gen.generateKey().getEncoded()));
        System.out.println(Base64.encodeBase64(gen.generateKey().getEncoded()));
        
    }
}
