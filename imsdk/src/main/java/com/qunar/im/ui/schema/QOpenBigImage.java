package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;

import java.util.Map;

public class QOpenBigImage implements QChatSchemaService {
    public final static QOpenBigImage instance = new QOpenBigImage();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

//        Intent intent = new Intent(context.getApplicationContext(), ImageBrowersingActivity.class);
//        intent.putExtra(Constants.BundleKey.IMAGE_URL, map.get(Constants.BundleKey.IMAGE_URL));
//        intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, map.get(Constants.BundleKey.IMAGE_ON_LOADING));
//        int location[] = new int[2];
////        user_gravatar.getLocationOnScreen(location);
////        intent.putExtra("left", location[0]);
////        intent.putExtra("top", location[1]);
////        intent.putExtra("height", user_gravatar.getHeight());
////        intent.putExtra("width", user_gravatar.getWidth());
//        context.startActivity(intent);

        ImageBrowsUtil.openImageSingle(map.get(Constants.BundleKey.IMAGE_URL),context,false);


        return false;
    }
}
