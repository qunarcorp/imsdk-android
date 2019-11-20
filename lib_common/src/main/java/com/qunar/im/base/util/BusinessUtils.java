//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qunar.im.base.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessUtils {
    public static boolean checkPhoneNumber(String number) {
        Pattern p = Pattern.compile("\\d{7}\\d.");
        Matcher m = p.matcher(number);
        return m.matches();
    }


    public static boolean checkEmail(String email) {
        if(!checkChiness(email)) {
            Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
            Matcher m = p.matcher(email);
            return m.matches();
        } else {
            return false;
        }
    }


    public static boolean checkChiness(String str) {
        return str.matches("^[一-龥]+$");
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }


    public static String formatDoublePrice(double value) {
        DecimalFormat df = new DecimalFormat("##########.##");
        return df.format(value);
    }
}
