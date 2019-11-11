package com.qunar.im.rtc.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qunar.im.base.util.Utils;
import com.qunar.im.rtc.activity.RtcActivity;

/**
 * 音视频上层按钮
 * Created by Lex lex on 2017/11/19.
 */

public class ChatHandleService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        chatHead = new ImageView(this);
//        chatHead.setImageResource(R.drawable.icon_calling_back);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

//        chatHead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent localIntent = new Intent();
//                localIntent.setClass(ChatHandleService.this, AVChatActivity.class);
//                localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(localIntent);
//            }
//        });

        setChatHeadTouchListener(params);
    }

    private void setChatHeadTouchListener(final WindowManager.LayoutParams params) {
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if((Math.abs((int) (event.getRawX() - initialTouchX)) <= 5
                        && (Math.abs((int) (event.getRawY() - initialTouchY)) <= 5))){
                            Intent localIntent = new Intent();
                            localIntent.setClass(ChatHandleService.this, RtcActivity.class);
                            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(localIntent);
                        }else{
                            if(params.x < Utils.getScreenWidth(ChatHandleService.this) / 2){
                                params.x = 0;
                                windowManager.updateViewLayout(chatHead, params);
                            }else{
                                params.x = Utils.getScreenWidth(ChatHandleService.this) - params.width;
                                windowManager.updateViewLayout(chatHead, params);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (chatHead != null) {
            windowManager.removeView(chatHead);
        }
    }
}
