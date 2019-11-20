package com.qunar.im.base.util;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.qunar.im.base.R;
import com.qunar.im.base.common.QunarIMApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by junwen.wu on 2014/5/5.
 */
public class DateTimeUtils {

    static {
        curZh = ConfigFileUtils.isZh();
        if(ConfigFileUtils.isZh())
        {
            week= new String[]{"周日","周一","周二","周三","周四","周五","周六"};
        }
        else {
            week = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        }
    }

    private static String[] week;
    private static boolean curZh;

    public static String getTime(long date, boolean detailAsPossible, boolean defailtAddTime) {
        StringBuilder displayTime = new StringBuilder();
        if (date > 0) {
            Calendar currentDt = Calendar.getInstance();
            currentDt.set(currentDt.get(Calendar.YEAR), currentDt.get(Calendar.MONTH), currentDt.get(Calendar.DATE), 0, 0, 0);
            int caYear = currentDt.get(Calendar.YEAR);
            int caDay = currentDt.get(Calendar.DAY_OF_MONTH);
            int caWeekOfYear = currentDt.get(Calendar.WEEK_OF_YEAR);

            Calendar target = Calendar.getInstance();
            target.setTimeInMillis(date);
            int year = target.get(Calendar.YEAR);
            int month = target.get(Calendar.MONTH);
            int day = target.get(Calendar.DAY_OF_MONTH);
            int hour = target.get(Calendar.HOUR_OF_DAY);
            int minute = target.get(Calendar.MINUTE);
            int weekOfYear = target.get(Calendar.WEEK_OF_YEAR);
            boolean addTime = defailtAddTime;
            if(year<caYear) //上年度显示日期
            {
                java.text.DateFormat dateFormat;
                if(detailAsPossible)
                {
                    dateFormat =DateFormat.getLongDateFormat(QunarIMApp.getContext());
                }
                else {
                    dateFormat = DateFormat.getMediumDateFormat(QunarIMApp.getContext());
                }
                String dateStr = dateFormat.format(target.getTime());
                displayTime.append(dateStr);
                addTime=false;
            }
            else if (year==caYear&&weekOfYear<caWeekOfYear) { //
                if(curZh)
                {
                    displayTime.append(month+1);
                    displayTime.append("月");
                    displayTime.append(day);
                    displayTime.append("日");
                }
                else {
                    displayTime.append(month+1);
                    displayTime.append("-");
                    displayTime.append(day);
                }
                addTime = detailAsPossible;
            }else if(year==caYear&&weekOfYear==caWeekOfYear&&caDay - day>1)//本周，昨天以前
            {
                displayTime.append(week[target.get(Calendar.DAY_OF_WEEK)-1]);
            }
            else if (year==caYear&&weekOfYear==caWeekOfYear&&caDay - day ==1) {   // 昨天
                displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_yesterday));
            } else if (year==caYear&&weekOfYear==caWeekOfYear&&caDay - day ==0) {  //今天
                displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_today));
            }else { //未来
                java.text.DateFormat dateFormat;
                if(detailAsPossible)
                {
                    dateFormat =DateFormat.getLongDateFormat(QunarIMApp.getContext());
                }
                else {
                    dateFormat = DateFormat.getMediumDateFormat(QunarIMApp.getContext());
                }
                String dateStr = dateFormat.format(target.getTime());
                displayTime.append(dateStr);
                addTime=false;
            }
            if(addTime) {
                displayTime.append("  ");
                int hour12 = target.get(Calendar.HOUR);
                if (curZh) {
                    if (hour < 6) {
                        displayTime.append("凌晨");
                    } else if (hour < 12) {
                        displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_am));
                    } else if (hour == 12) {
                        displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_noon));
                        hour12 = hour;
                    } else if (hour < 18) {
                        displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_pm));
                    } else {
                        displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_evening));
                    }
                    displayTime.append(hour12);
                    displayTime.append(":");
                    if(minute<10)
                    {
                        displayTime.append("0");
                    }
                    displayTime.append(minute);
                } else {
                    displayTime.append(hour12);
                    displayTime.append(":");
                    if(minute<10)
                    {
                        displayTime.append("0");
                    }
                    displayTime.append(minute);
                    if (hour < 12) {
                        displayTime.append(" AM");
                    } else {
                        displayTime.append(" PM");
                    }
                }
            }
        }
        return displayTime.toString();
    }

    public static String getTimeForSeesionAndChat(long date, boolean detailAsPossible) {
        StringBuilder displayTime = new StringBuilder();
        if (date > 0) {
            Calendar currentDt = Calendar.getInstance();
            currentDt.set(currentDt.get(Calendar.YEAR), currentDt.get(Calendar.MONTH), currentDt.get(Calendar.DATE), 0, 0, 0);
            int caYear = currentDt.get(Calendar.YEAR);
            int caDay = currentDt.get(Calendar.DAY_OF_MONTH);
            int caWeekOfYear = currentDt.get(Calendar.WEEK_OF_YEAR);

            Calendar target = Calendar.getInstance();
            target.setTimeInMillis(date);
            int year = target.get(Calendar.YEAR);
            int month = target.get(Calendar.MONTH);
            int day = target.get(Calendar.DAY_OF_MONTH);
            int hour = target.get(Calendar.HOUR_OF_DAY);
            int minute = target.get(Calendar.MINUTE);
            int weekOfYear = target.get(Calendar.WEEK_OF_YEAR);
            boolean addTime = true;
            if(year<caYear) //上年度显示日期
            {
                java.text.DateFormat dateFormat;
                if(detailAsPossible)
                {
                    dateFormat =DateFormat.getLongDateFormat(QunarIMApp.getContext());
                }
                else {
                    dateFormat = DateFormat.getMediumDateFormat(QunarIMApp.getContext());
                }
                String dateStr = dateFormat.format(target.getTime());
                displayTime.append(dateStr);
                addTime=false;
            }
            else if (year==caYear&&weekOfYear<caWeekOfYear) { //
                if(curZh)
                {
                    displayTime.append(month+1);
                    displayTime.append("月");
                    displayTime.append(day);
                    displayTime.append("日");
                }
                else {
                    displayTime.append(month+1);
                    displayTime.append("-");
                    displayTime.append(day);
                }
                addTime = detailAsPossible;
            }else if(year==caYear&&weekOfYear==caWeekOfYear&&caDay - day>1)//本周，昨天以前
            {
                addTime = detailAsPossible;
                displayTime.append(week[target.get(Calendar.DAY_OF_WEEK)-1]);
            }
            else if (year==caYear&&weekOfYear==caWeekOfYear&&caDay - day ==1) {   // 昨天
                addTime = detailAsPossible;
                displayTime.append(QunarIMApp.getContext().getString(R.string.atom_ui_day_yesterday));
            } else if (year==caYear&&weekOfYear==caWeekOfYear&&caDay - day ==0) {  //今天
                addTime = true;
//                displayTime.append("今天");
            }else { //未来
                java.text.DateFormat dateFormat;
                if(detailAsPossible)
                {
                    dateFormat =DateFormat.getLongDateFormat(QunarIMApp.getContext());
                }
                else {
                    dateFormat = DateFormat.getMediumDateFormat(QunarIMApp.getContext());
                }
                String dateStr = dateFormat.format(target.getTime());
                displayTime.append(dateStr);
                addTime=false;
            }
            if(addTime) {
                boolean b = year == caYear && weekOfYear == caWeekOfYear && (caDay - day == 0);
                if(!b){//不是今天
                    displayTime.append("  ");
                }
                int hour24 = target.get(Calendar.HOUR_OF_DAY);
                if (curZh) {
//                    if (hour < 6) {
//                        displayTime.append("凌晨");
//                    } else if (hour < 12) {
//                        displayTime.append("上午");
//                    } else if (hour == 12) {
//                        displayTime.append("中午");
//                        hour12 = hour;
//                    } else if (hour < 18) {
//                        displayTime.append("下午");
//                    } else {
//                        displayTime.append("晚上");
//                    }
                    displayTime.append(hour24);
                    displayTime.append(":");
                    if(minute<10)
                    {
                        displayTime.append("0");
                    }
                    displayTime.append(minute);
                } else {
                    displayTime.append(hour24);
                    displayTime.append(":");
                    if(minute<10)
                    {
                        displayTime.append("0");
                    }
                    displayTime.append(minute);
//                    if (hour < 12) {
//                        displayTime.append(" AM");
//                    } else {
//                        displayTime.append(" PM");
//                    }
                }
            }
        }
        return displayTime.toString();
    }

    public static String getTimeForSearch(long date){
        if(isToday(date)){
            return QunarIMApp.getContext().getString(R.string.atom_ui_day_today);
        }else if(isThisWeek(date)){
            return QunarIMApp.getContext().getString(R.string.atom_ui_day_thisweek);
        }else if(isThisMonth(date)){
            return QunarIMApp.getContext().getString(R.string.atom_ui_day_thismonth);
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return year + "-" + month;
        }
    }

    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    public static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }
    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    public static String stringForTime(long timeM) {
        final long totalSeconds = timeM;

        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());

        stringBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static long TimeForString(String time) {
        if(!TextUtils.isEmpty(time) && time.contains(":")) {
            int hours = 0, minutes = 0, seconds = 0;
            String[] times = time.split(":");
            if(times.length == 3) {
                hours = Integer.valueOf(times[0]);
                minutes = Integer.valueOf(times[1]);
                seconds = Integer.valueOf(times[2]);
            } else if(times.length == 2){
                hours = 0;
                minutes = Integer.valueOf(times[0]);
                seconds = Integer.valueOf(times[1]);
            }
            return (long)(hours * 3600 + minutes * 60 + seconds);
        }
        return 0;
    }

}