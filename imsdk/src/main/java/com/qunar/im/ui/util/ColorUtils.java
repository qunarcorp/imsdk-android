package com.qunar.im.ui.util;

import android.text.TextUtils;

public class ColorUtils {

    public static String getColorFromHash(String source) {
        if (TextUtils.isEmpty(source))
            return "#000000";
        int hashcode = Math.abs(source.hashCode());

        int red = (hashcode & 0xff0000) >> 16;
        int green = (hashcode & 0xff00) >> 8;
        int blue = (hashcode & 0xff);

        if (0.299 * red + 0.587 * green + 0.114 * blue > 180) {
            red = (int) (red * 0.8);
            green = (int) (green * 0.8);
            blue = (int) (blue * 0.8);
        }
        hashcode = (red << 16) + (green << 8) + blue;

        String code = String.valueOf(hashcode);

        if (code.length() < 6) {
            for (int i = 0; i < 6 - code.length(); i++) {
                code = "0" + code;
            }
            return "#" + code;
        } else
            return "#" + code.substring(0, 6);

    }
}
