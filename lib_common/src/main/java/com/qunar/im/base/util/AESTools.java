package com.qunar.im.base.util;

import android.util.Base64;

import java.io.StreamCorruptedException;
import java.nio.charset.Charset;

/**
 * Created by may on 2017/10/23.
 */

public class AESTools {

    private static AESTools INSTANCE = new AESTools();

    public AESTools getInstance() {
        return INSTANCE;
    }

    public static String encodeToBase64(String password,String message) {
        byte[] input = INSTANCE.encode(message, password);
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    public static String decodeFromBase64(String password,String base64String) {
        byte[] input = Base64.decode(base64String, Base64.DEFAULT);
        String result;
        try {
            byte[] output = INSTANCE.decode(input, password);
            for (byte b : output) {
                if (
                        b == 0x08 ||
                                b == 0x0b ||
                                b == 0x0c ||
                                b == 0x0e ||
                                b == 0x1f
                        ) {
                    throw new StreamCorruptedException("decrypt failed");
                }
            }
            result = new String(output, Charset.forName("UTF-8"));
            if (result.getBytes("UTF-8").length != output.length)
                throw new StreamCorruptedException("decrypt failed");
        } catch (Exception e) {
            result = null;
        }
        return result;

    }

    public native byte[] encode(String message, String password);

    public native byte[] decode(byte[] input, String password);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("aes256");
    }
}
