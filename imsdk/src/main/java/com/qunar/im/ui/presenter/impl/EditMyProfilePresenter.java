package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;
import android.util.LruCache;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.RemoteConfig;
import com.qunar.im.base.jsonbean.SetVCardResult;
import com.qunar.im.ui.presenter.IEditMyProfilePresenter;
import com.qunar.im.ui.presenter.views.IMyProfileView;
import com.qunar.im.base.protocol.ConfigAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by saber on 16-2-2.
 */
public class EditMyProfilePresenter implements IEditMyProfilePresenter {
    IMyProfileView myProfileView;

    @Override
    public void setPersonalInfoView(IMyProfileView view) {
        myProfileView = view;
    }

    @Override
    public void updateMood() {
        final String mood = myProfileView.getMood();
        if(!TextUtils.isEmpty(mood)) {
            VCardAPI.setMyUserProfile(mood, new ProtocolCallback.UnitCallback<SetVCardResult>() {
                @Override
                public void onCompleted(SetVCardResult setMoodResult) {
                    if (setMoodResult.ret) {
                        EventBus.getDefault().post(new EventBusEvent.ChangeMood(mood));
                        myProfileView.setMood(mood);
                    }
                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        }
    }

    @Override
    public void loadMood() {
//        VCardAPI.getUserProfile(myProfileView.getJid(), new ProtocolCallback.UnitCallback<GetMoodResult>() {
//            @Override
//            public void onCompleted(GetMoodResult getMoodResult) {
//                if (getMoodResult.ret && !ListUtil.isEmpty(getMoodResult.data)) {
//                    String result = getMoodResult.data.get(0).M;
//                    myProfileView.setMood(result);
//                }
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });
    }

    @Override
    public String getMarkNames() {
        String name = ConnectionUtil.getInstance().selectMarkupNameById(myProfileView.getJid());
        return name == null ? "" : name;
    }

    @Override
    public void updateMarkupName() {
        if(TextUtils.isEmpty(myProfileView.getMarkup())){
            myProfileView.setMarkup(false);
            return;
        }
        final LruCache<String,String> map = IMDatabaseManager.getInstance().selectMarkupNames();
        map.put(myProfileView.getJid(),myProfileView.getMarkup());

        List<RemoteConfig.ConfigItem> configItems = new ArrayList<>();
        RemoteConfig.ConfigItem configItem = new RemoteConfig.ConfigItem();
        final String value = JsonUtils.getGson().toJson(map);
        configItem.key = Constants.SYS.MARKUP_NAME;
        configItem.version = String.valueOf(1);
        configItem.value = value;
        configItems.add(configItem);
        ConfigAPI.setRemoteConfig(configItems, new ProtocolCallback.UnitCallback<RemoteConfig>() {
            @Override
            public void onCompleted(RemoteConfig remoteConfig) {
                if(remoteConfig != null){
                    IMDatabaseManager.getInstance().updateMarkupNames(value);
                    Logger.i("edit map:"+JsonUtils.getGson().toJson(map));
                    CurrentPreference.getInstance().setMarkupNames(map);
                    myProfileView.setMarkup(true);
                }else myProfileView.setMarkup(false);

            }

            @Override
            public void onFailure(String errMsg) {
                myProfileView.setMarkup(false);
            }
        });
    }
}
