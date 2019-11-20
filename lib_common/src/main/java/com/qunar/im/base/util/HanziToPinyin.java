package com.qunar.im.base.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by saber on 15-12-16.
 */
public class HanziToPinyin {
    static final int GB_SP_DIFF = 160; // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 }; // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X',
            'Y', 'Z' };

    public static char getFirstLetter(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return (char) (ch - 'a' + 'A');
        }
        if (ch >= 'A' && ch <= 'Z') {
            return ch;
        }
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("Hanzi","error",e);
            return ch;
        }
        if (uniCode[0] > 0) { // 非汉字
            return ch;
        } else {
            return convert(uniCode);
        }
    }

    public static String zh2Abb(String str)
    {
        String result = "";
        for(char ch:str.toCharArray())
        {
            result+=getFirstLetter(ch);
        }
        return result;
    }

    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }
}
