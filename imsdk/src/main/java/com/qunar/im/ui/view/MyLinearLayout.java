package com.qunar.im.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;

/**
 * Created by froyomu on 2019-08-12
 * <p>
 * Describe:
 */
public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }


    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        try{
            super.dispatchWindowFocusChanged(hasFocus);
        }catch (Exception e){
            Logger.e("dispatchWindowFocusChanged");
            LogInfo logInfo = QLog.build(LogConstans.LogType.CRA,LogConstans.LogSubType.CRASH).eventId("MyLinearLayout").describtion("会话内crash").currentPage("会话页面");
            LogService.getInstance().saveLog(logInfo);
        }
    }
}
