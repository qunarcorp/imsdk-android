package com.qunar.im.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 将时间字符串转换为Date类型
     * @param dateStr
     * @return Date
     */
    public static Date toDate(String dateStr) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern("yyyy-MM-dd");
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 按照提供的格式将字符串转换成Date类型
     * @param dateStr
     * @param formaterString
     * @return
     */
    public static Date toDate(String dateStr, String formaterString) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern(formaterString);
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将Date类型时间转换为字符串
     * @param date
     * @return
     */
    public static String toString(Date date) {

        String time;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern("yyyy-MM-dd");
        time = formater.format(date);
        return time;
    }

    /**
     * 按照参数提供的格式将Date类型时间转换为字符串
     * @param date
     * @param formaterString
     * @return
     */
    public static String toString(Date date, String formaterString) {
        String time;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern(formaterString);
        time = formater.format(date);
        return time;
    }

    /**
     * method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
     * @param dateString
     *            需要转换为timestamp的字符串
     * @return dataTime timestamp
     */
    public static java.sql.Timestamp string2Time(String dateString)
            throws ParseException {
        DateFormat dateFormat;
//      dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS",
//              Locale.ENGLISH);// 设定格式
        dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        dateFormat.setLenient(false);
        Date timeDate = dateFormat.parse(dateString);// util类型

        java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
        return dateTime;
    }

    /**
     * method 将字符串类型的日期按照转换为一个timestamp（时间戳记java.sql.Timestamp）
     *
     * @param dateString 需要转换为timestamp的字符串
     * @param formaterString dateString字符串的解析格式
     * @return
     * @throws ParseException
     */
    public static java.sql.Timestamp string2Time(String dateString,
                                                       String formaterString) throws ParseException {
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat(formaterString);// 设定格式
        // dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat.setLenient(false);
        Date timeDate = dateFormat.parse(dateString);// util类型
        java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
        return dateTime;
    }



    /**
     * start
     * 本周开始时间戳 - 以星期一为本周的第一天
     */
    public static long getWeekStartTime() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_WEEK, 2);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMMdd", Locale. getDefault());
//        Calendar cal = Calendar.getInstance();
//        int day_of_week = cal.get(Calendar. DAY_OF_WEEK) - 1;
//        if (day_of_week == 0 ) {
//            day_of_week = 7 ;
//        }
//        cal.add(Calendar.DATE , -day_of_week + 1 );
//        return simpleDateFormat.format(cal.getTime()) + "000000000";
    }

    /**
     * end
     * 本周结束时间戳 - 以星期一为本周的第一天
     */
    public static long getWeekEndTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));
        c.add(Calendar.DATE,1);
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();


//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        Calendar cal = Calendar.getInstance();
//        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
//        if (day_of_week == 0) {
//            day_of_week = 7;
//        }
//        cal.add(Calendar.DATE, -day_of_week + 7);
//        return simpleDateFormat.format(cal.getTime()) + "235959999";

    }

    /**
     * 获取指定日期所在月份开始的时间戳
     * @return
     */
    public static Long getMonthBegin() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获取指定日期所在月份结束的时间戳
     * @return
     */
    public static Long getMonthEnd() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获得今天的结束时间
     * @return
     */
    public static long getToDayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis();

    }

    /**
     * 获得今天的开始时间
     * @return
     */
    public static long getToDayBeginTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 0);
        todayEnd.set(Calendar.MINUTE, 0);
        todayEnd.set(Calendar.SECOND, 0);
        todayEnd.set(Calendar.MILLISECOND, 0);
        return todayEnd.getTimeInMillis();

    }


}
