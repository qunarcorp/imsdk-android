package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IBrowsingPresenter;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.MessageUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saber on 16-1-29.
 */
public class BrowsingPresenter implements IBrowsingPresenter {
    private final static String objPattern = "\\[obj type=\"image\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);
    IBrowsingConversationImageView brosingView;
    @Override
    public void setBrosingView(IBrowsingConversationImageView view) {
        brosingView = view;
    }

    @Override
    public void loadImgsOfCurrentConversation() {
        try {
            List<IMMessage> list = null;
            if(!TextUtils.isEmpty(brosingView.getOriginFrom())&&!TextUtils.isEmpty(brosingView.getOriginTo())){
                list = ConnectionUtil.getInstance().searchImageMsg(brosingView.getOriginFrom(),brosingView.getOriginTo(),50);
            }else{
                list = ConnectionUtil.getInstance().searchImageMsg(brosingView.getConversationId(), 50);
            }



            List<IBrowsingConversationImageView.PreImage> imgs = new LinkedList<>();
            for(int i=list.size()-1;i>=0;i--)
            {
                IMMessage msg = list.get(i);

                Matcher m = compiledPattern.matcher(msg.getBody());
                while (m.find()) {
                    String value = m.group(1);
                    String ext = null;
                    if (m.groupCount() >= 2) {
                        ext = m.group(2);
                    }

                    MessageUtils.ImageMsgParams params = new MessageUtils.ImageMsgParams();
                    if (ext != null && ext.contains("width") && ext.contains("height")) {
                        try{
                            String[] str = ext.trim().split("\\s+");
                            if (str.length > 1) {
                                //处理width = 240.000000　问题
                                params.width = Double.valueOf(str[0].substring(str[0].indexOf("width") + 6)).intValue();
                                params.height = Double.valueOf(str[1].substring(str[1].indexOf("height") + 7)).intValue();

                            }
                        }catch (Exception e){

                        }
                    }

                    params.sourceUrl = value;
                    MessageUtils.getDownloadFile(params, QunarIMApp.getContext(), true);
                    IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
                    image.originUrl = params.sourceUrl;
                    image.smallUrl = params.smallUrl;
                    image.width = params.width;
                    image.height = params.height;

                    //判断是不是自己发的本地图片
                    String extenInfo = msg.getExt();
                    if(!TextUtils.isEmpty(extenInfo)){
                        //判断本地图片是否存在
                        List<Map<String, String>> tempList = ChatTextHelper.getObjList(extenInfo);
                        if(tempList != null && tempList.size() == 1){
                            String v = tempList.get(0).get("value");
                            if(v.startsWith("file://")){
                                String localPath = v.substring(7);
                                if(new File(localPath).exists()){//本地文件存在取ext 不存在取网络url
                                    image.localPath = v;
                                }
                            }
                        }
                    }
                    imgs.add(image);
                }
            }
            brosingView.setImageList(imgs);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}