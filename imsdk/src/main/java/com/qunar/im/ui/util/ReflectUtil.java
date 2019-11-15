package com.qunar.im.ui.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by froyomu on 2019-08-05
 * <p>
 * Describe:反射获取class 方便解耦（反射的class不要混淆，否则会找不到哦）
 */
public class ReflectUtil {

    /**
     * 获取RN页面跳转Intent
     * @param context
     * @return
     */
    public static Intent getQtalkServiceRNActivityIntent(Context context){
        try{
            Class c = Class.forName("com.qunar.rn_service.activity.QtalkServiceRNActivity");
            Intent intent = new Intent(context,c);
            return intent;
        }catch (ClassNotFoundException e){
            return null;
        }
    }

    /**
     * 获取RN搜索页面跳转Intent
     * @param context
     * @return
     */
    public static Intent getQTalkSearchActivityIntent(Context context){
        try{
            Class c = Class.forName("com.qunar.rn_service.activity.QTalkSearchActivity");
            Intent intent = new Intent(context,c);
            return intent;
        }catch (ClassNotFoundException e){
            return null;
        }
    }

    /**
     * 获取RN 通讯录Fragment
     * @return
     */
    public static Fragment getRNContactsFragment(){
        try{
            Class c = Class.forName("com.qunar.rn_service.fragment.RNContactsFragment");
            return (Fragment) c.newInstance();
        }catch (Exception e){
            return new Fragment();
        }
    }

    /**
     * 获取RN 发现Fragment
     * @return
     */
    public static Fragment getRNFoundFragment(){
        try{
            Class c = Class.forName("com.qunar.rn_service.fragment.RNFoundFragment");
            return (Fragment) c.newInstance();
        }catch (Exception e){
            return new Fragment();
        }
    }

    /**
     * 获取RN 我的Fragment
     * @return
     */
    public static Fragment getRNMineFragment(){
        try{
            Class c = Class.forName("com.qunar.rn_service.fragment.RNMineFragment");
            return (Fragment) c.newInstance();
        }catch (Exception e){
            return new Fragment();
        }
    }
}
