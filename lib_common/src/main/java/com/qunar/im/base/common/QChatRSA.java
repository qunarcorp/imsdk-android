package com.qunar.im.base.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.BinaryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by saber on 15-9-2.
 */
public class QChatRSA {
    public static   String pub_key = "";
    private static String encryptMe(byte[] cipherData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(Base64.decode(pub_key,Base64.DEFAULT));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(cipherData);
        return Base64.encodeToString(output, Base64.DEFAULT);
    }

    private static String encryptPassword(String password) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException {
        String base64Key = BinaryUtil.MD5(password).toLowerCase();
        return encryptMe(base64Key.getBytes());
    }

    public static String QunarRSAEncrypt(String pwd) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException {
        String code = encryptPassword(pwd);
   //     code = URLEncoder.encode(code, "utf-8");
        return code;
    }

    public static String QTalkEncodePassword(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return encodePassword(password);
    }

    private static String encodePassword(String raaPwd) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return encryptMe(raaPwd.getBytes()) ;
    }

    /**
     *  读取pubkey
     * @param fileName
     * @return
     */
    public static String readPubkey(Context context, String fileName){
        try {
            InputStream is = context.getAssets().open(fileName + ".pem");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String key = new String(buffer, "GB2312");
            if(TextUtils.isEmpty(key)){
                return QChatRSA.pub_key;
            }
            return key;
        } catch (IOException e) {
            Logger.i("读取pubkey异常：" + e.getMessage());
            return QChatRSA.pub_key;
            //throw new RuntimeException(e);
        }
    }
/**
     *  读取pubkey
     * @param fileName
     * @return
     */
//    public static String readPubkey(Context context, String fileName){
//        try {
//            InputStream is = context.getAssets().open(fileName + ".pem");
//            String key = pub_key;
//            try {
//                RSAPublicKey rsaPublicKey  = (RSAPublicKey) loadPublicKey(is);
//                key = rsaPublicKey.getModulus().toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            String key = new String(buffer, "GB2312");
//            if(TextUtils.isEmpty(key)){
//                return QChatRSA.pub_key;
//            }
//            return key;
//        } catch (IOException e) {
//            return QChatRSA.pub_key;
//            //throw new RuntimeException(e);
//        }
//    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in
     *            公钥输入流
     * @throws Exception
     *             加载公钥时产生的异常
     */
    private static PublicKey loadPublicKey(InputStream in) throws Exception
    {
        try
        {
            return loadPublicKey(readKey(in));
        } catch (IOException e)
        {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null)
        {
            if (readLine.charAt(0) == '-')
            {
                continue;
            } else
            {
                sb.append(readLine);
                sb.append('\r');
            }
        }

        return sb.toString();
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    private static PublicKey loadPublicKey(String publicKeyStr) throws Exception
    {
        try
        {
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e)
        {
            throw new Exception("公钥非法");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥数据为空");
        }
    }
}
