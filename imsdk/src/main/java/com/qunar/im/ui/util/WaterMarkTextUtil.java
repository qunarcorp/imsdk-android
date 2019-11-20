package com.qunar.im.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.qunar.im.other.CacheDataType;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;

public class WaterMarkTextUtil {
    /**
     * 字的大小 单位sp
     */
    final int textSize = 13;
    /**
     * 一个字所占的像素
     */
    int oneTextPx;
    /**
     * 当前字符串所占的长度
     */
    int textLength;
    /**
     * 默认后缀文字
     */
    String sText = CurrentPreference.getInstance().getUserid();
    /**
     * 默认后缀文字的宽高
     */
    int sWitchPx;
    int sHigthPx;
    /**
     * 第二段sText文字距离前一段文字的水平偏移量
     */
    int offset = 200;

    /**图片距离右边的距离*/
    int right = 180;

    /**图片距离上边的距离*/
    int top = 160;

    /**文字旋转的角度*/
    float rotate =20f;

    Bitmap bitmap;
    boolean isOpen;

    //设置背景
    public void setWaterMarkTextBg(final View view, final Context gContext) {
        isOpen = DataUtils.getInstance(gContext).getPreferences(CacheDataType.kWaterMark,true);
        if(!isOpen) return;

        if(TextUtils.isEmpty(sText)){
            String user = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
            String domain = QtalkNavicationService.getInstance().getXmppdomain();
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(domain)) {
                return;
            }
            sText = user;
            final String lastid = user + "@" + domain;
            ConnectionUtil.getInstance().getUserCard(lastid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    CommonConfig.mainhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(nick != null){
                                view.setBackground(drawTextToBitmap(gContext, nick.getName()));
                            }
                        }
                    });
                }
            }, false, true);
        }else {
            view.setBackground(drawTextToBitmap(gContext, CurrentPreference.getInstance().getUserName()));
        }

    }

    public void recyleBitmap(){
        if(!isOpen){
            return;
        }
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    /**
     * 生成水印文字图片
     */
    public BitmapDrawable drawTextToBitmap(Context mContext, String gText) {

        try {
            TextPaint mTextPaint1 = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint1.density = mContext.getResources().getDisplayMetrics().density;
            oneTextPx = Utils.sp2px(mContext, textSize);
            mTextPaint1.setTextSize(oneTextPx);
            //计算字长
            textLength = (int) mTextPaint1.measureText(gText);
            int stextLength = (int) mTextPaint1.measureText(sText);
            /**拿到字长之后，计算一下斜着显示文字的时候文字所占的长和高*/
            int witchPx = 0;
            int highPx = 0;
            /**默认一段文字的长和高计算*/
            if (sWitchPx == 0) {
                sWitchPx = measurementWitch(stextLength);
                sHigthPx = measurementHigth(stextLength);
            }
            /**传入的文字的长和高计算*/
            witchPx = measurementWitch(textLength);
            highPx = measurementHigth(textLength);
            /**计算显示完整所需占的画图长宽*/
            int bitmapWitch = witchPx + sWitchPx + 2 * oneTextPx + offset;
            int bitmaphigth = (highPx + oneTextPx) * 2;
            //设置画板的时候 增加一个字的长宽
            bitmap = Bitmap.createBitmap(bitmapWitch + right,
                    bitmaphigth+(2*top), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.atom_ui_chat_gray_bg));
            /**初始化画笔*/
            TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.density = mContext.getResources().getDisplayMetrics().density;
            mTextPaint.setColor(Color.parseColor("#C4C4C4"));
            mTextPaint.setAlpha(90);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setTextSkewX(0);
            mTextPaint.setTextSize(oneTextPx);
            /**
             * ——————————————————————————————————>
             * |    1号绘制区域    |  间  |  2号   |
             * |   gText         |      | appName |
             * |  ①号起点位置     |  隔  |  ②起   |
             * ———————————————————————————————————
             * | 3号      | 间   |   4号绘制区域    |
             * | appName   |      |   gText        |
             * |  ③起     | 隔   |   ④号起        |
             * |———————————————————————————————————
             * V
             */
            /**方式二利用画布平移和旋转绘制*/
            /**先移动到①起点位置*/
            canvas.translate(oneTextPx,highPx+oneTextPx+top);
            /**旋转一定度数 绘制文字*/
            canvas.rotate(-rotate);
            canvas.drawText(gText,0,0,mTextPaint);
            /**恢复原来的度数 再移动画布原点到②号位置*/
            canvas.rotate(rotate);
            canvas.translate(witchPx+offset+oneTextPx,0);
            /**旋转一定度数 绘制文字*/
            canvas.rotate(-rotate);
            canvas.drawText(sText,0,0,mTextPaint);
            /**恢复原来的度数 再移动画布原点到③号位置*/
            canvas.rotate(rotate);
            canvas.translate(-(witchPx+offset+oneTextPx),oneTextPx+highPx+top);
            /**旋转一定度数 绘制文字*/
            canvas.rotate(-rotate);
            canvas.drawText(sText,0,0,mTextPaint);
            /**恢复原来的度数 再移动画布原点到④号位置*/
            canvas.rotate(rotate);
            canvas.translate(sWitchPx+offset+oneTextPx,0);
            /**旋转一定度数 绘制文字*/
            canvas.rotate(-rotate);
            canvas.drawText(gText,0,0,mTextPaint);
            /**保存画布*/
            canvas.save();
            canvas.restore();
            //生成平铺的bitmapDrawable
            BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawable.setDither(true);
            return drawable;
        } catch (Exception e) {

        }
        return null;

    }

    private int measurementWitch(int textLength) {
        //t2 为字长的平方 字长的平方即为三角形的斜边的平方
        int t2 = textLength * textLength;
        //此处设置 三角形的长为高的3倍，那么就是平方之后的9倍，均分为10份 长占9份
        return ceilInt(Math.sqrt(9 * (t2 / 10)));
    }

    private int measurementHigth(int textLength) {
        //t2 为字长的平方 字长的平方即为三角形的斜边的平方
        int t2 = textLength * textLength;
        //此处设置 三角形的长为高的3倍，那么就是平方之后的9倍，均分为10份 长占9份
        return ceilInt(Math.sqrt((t2 / 10)));
    }

    public static int ceilInt(double number) {
        return (int) Math.ceil(number);
    }
}
