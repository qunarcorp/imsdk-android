package com.qunar.im.utils;

import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.common.CommonConfig;

public class CalendarSynchronousUtil {

    public static void bulkTrip(CalendarTrip calendarTrip){
        for (int i = 0; i < calendarTrip.getData().getTrips().size(); i++) {
            CalendarTrip.DataBean.TripsBean bean = calendarTrip.getData().getTrips().get(i);
            if(bean.getCanceled()){
                //这是行程被删除
                CalendarReminderUtils.deleteCalendarEvent(CommonConfig.globalContext,bean);
            }else{
                //这是行程添加
                CalendarReminderUtils.AnalysisCalendarEvent(CommonConfig.globalContext,bean,15);
            }
        }



    }
}
