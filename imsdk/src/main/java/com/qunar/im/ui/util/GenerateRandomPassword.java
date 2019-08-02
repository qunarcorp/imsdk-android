package com.qunar.im.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 随记生成密码
 * Created by lihaibin.li on 2017/8/25.
 */

public class GenerateRandomPassword {
    /**
     * 特殊符号
     */
    private static final char[] SPECIAL_CHARS = {'`', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', '\\', '|', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?'};
    private static final String[] LETTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    /**
     * 产生n位随机数
     *
     * @return
     */
    private static String generateRandomNumber(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return String.valueOf((long) (Math.random() * 9 * Math.pow(10, n - 1)) + (long) Math.pow(10, n - 1));
    }

    private static String generateRandomSpecialChars(int n) {
        String str = "";
        int char_length = SPECIAL_CHARS.length - 1;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int index = random.nextInt(char_length);
            str += SPECIAL_CHARS[index];
        }
        return str;
    }

    private static String generateRandomLetters(int n, int upcaseCount) {
        String str = "";
        int str_length = LETTERS.length - 1;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int index = random.nextInt(str_length);
            str += i < upcaseCount ? LETTERS[index].toUpperCase() : LETTERS[index];
        }
        return str;
    }

    private static String shuffleForSortingString(String s) {
        char[] c = s.toCharArray();
        List<Character> lst = new ArrayList<Character>();
        for (int i = 0; i < c.length; i++) {
            lst.add(c[i]);
        }

        System.out.println(lst);

        Collections.shuffle(lst);

        System.out.println(lst);

        String resultStr = "";
        for (int i = 0; i < lst.size(); i++) {
            resultStr += lst.get(i);
        }

        return resultStr;
    }

    public static String creatGenerateRandomPassword(int number, int lettes, int specialChar,int upcaseCount) {
        String s = generateRandomNumber(number) + generateRandomLetters(lettes,upcaseCount) + generateRandomSpecialChars(specialChar);
        return shuffleForSortingString(s);
    }
}
