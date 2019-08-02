package com.qunar.im.ui.util;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.dispatch.DispatchHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class SerializableUtils {

    private static final String TAG = SerializableUtils.class.getSimpleName();

    private static class LazyHolder{
        private static final SerializableUtils INSTANCE = new SerializableUtils();
    }

    public static SerializableUtils getInstance(){
        return LazyHolder.INSTANCE;
    }

    public void saveAsSerializable(final List<RecentConversation> recentConversations) {
        DispatchHelper.Async("saveAsSerializable", false, new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    FileOutputStream fos = null;
                    ObjectOutputStream oos = null;
                    try {
                        fos = CommonConfig.globalContext.getApplicationContext().openFileOutput(TAG, Context.MODE_PRIVATE);
                        if(fos == null) return;
                        oos = new ObjectOutputStream(fos);
                        if(oos == null) return;
                        oos.writeObject(recentConversations);
                    } catch (Exception e) {
                        Logger.i("saveAsSerializableException" + e.getLocalizedMessage());
                    }finally {
                        try{
                            if(oos != null)
                                oos.close();
                            if(fos != null)
                                fos.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    public List<RecentConversation> readData() {
        List<RecentConversation> obj = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = CommonConfig.globalContext.getApplicationContext().openFileInput(TAG);
            if(fis == null) return null;
            ois = new ObjectInputStream(fis);
            if(ois == null) return null;
            obj = (List<RecentConversation>)ois.readObject();
        } catch (Exception e) {

        }finally {
            try{
                if(fis != null)
                    fis.close();
                if(ois != null)
                    ois.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return obj;

    }
}
