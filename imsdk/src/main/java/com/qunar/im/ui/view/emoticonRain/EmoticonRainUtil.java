package com.qunar.im.ui.view.emoticonRain;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qunar.im.common.CommonConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmoticonRainUtil {

    private static final int MAX_COUNT = 10;
    private static final Map<String,String> filterMap = new HashMap<>();

    static {
        filterMap.put("生日快乐","birthday.png");
        filterMap.put("happy birthday","birthday.png");
        filterMap.put("么么哒","emoticons/0018_2x.png");
    }

    public static String getEmoPath(String s){
        String value = null;
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            String key = entry.getKey();
            if(s.contains(key)){
                value = entry.getValue();
                break;
            }
        }
        return value;
    }

    public static void startRain(EmoticonRainView emoticonRainView,String path) {
        try {
            AssetManager assetManager = CommonConfig.globalContext.getAssets();
            InputStream inputStream = assetManager.open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            List<Bitmap> bitmaps = new ArrayList<>();
            for (int i = 0; i < MAX_COUNT; i++) {
                bitmaps.add(bitmap);
            }
            EmoticonRainView.Conf conf = new EmoticonRainView.Conf.Builder().bitmaps(bitmaps).build();
            emoticonRainView.start(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
