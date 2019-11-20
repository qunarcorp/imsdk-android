package com.qunar.im.base.util;

/**
 * Created by zhaokai on 16-2-22.
 */
public final class DegreeUtils {
    public static double radToDegree(double rad){
        return 180.0 / Math.PI * rad;
    }

    public static double degreeToRad(double degree){
        return Math.PI / 180.0 * degree;
    }
}