package com.qunar.im.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BinaryUtil {

    final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] h2b(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replace(" ", "");
        int length = hexString.length() / 2;
        byte[] d2 = new byte[length];
        int currentPos = 0;
        int i = 0;
        do {
            String a = hexString.substring(i, i + 2);
            d2[currentPos++] = (byte) (Integer.parseInt(a, 16) & 0xFF);
            i += 2;
        } while (currentPos < length);
        return d2;
    }

    public static String MD5(String strSrc) {
        byte[] bt = strSrc.getBytes();
        MessageDigest md = null;
        String strDes = "empty";
        try {
            md = MessageDigest.getInstance("MD5");

            md.update(bt);
            strDes = bytesToHex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return strDes;
    }

}
