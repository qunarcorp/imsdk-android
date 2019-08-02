package com.qunar.im.ui.presenter.impl;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.jsonbean.CapabilityResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ISystemPresenter;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.CurrentPreference;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

import static com.qunar.im.base.util.JsonUtils.getGson;

/**
 * Created by xinbo.wang on 2015/3/5.
 */
//用于加载应用当前的基本信息,保存crash Log等
public class SystemPresenter implements ISystemPresenter {
    private static final java.lang.String TAG = "SystemPresenter";

    protected String PATH;
    protected String FILE_NAME;
    protected String FILE_NAME_SUFFIX;
    protected Timer chkTimer;
    protected TimerTask chkTask;

    public SystemPresenter() {
        PATH = Environment.getExternalStorageDirectory().getPath() + "/qtalk/logcat/";
        FILE_NAME = "logcat_";
        FILE_NAME_SUFFIX = ".txt";
    }

    @Override
    public void loadPreference(Context context, boolean isWrite) {
    }
    @Override
    public void changeProcess2Failed() {
    }

    @Override
    public void checkSendingLine() {
        if (chkTimer != null) chkTimer.cancel();
        if (chkTimer != null) chkTask.cancel();
        chkTask = new TimerTask() {
            @Override
            public void run() {
                if (InternDatas.sendingLine.size() == 0) return;
                for (String key : InternDatas.sendingLine.keySet()) {
                    IMMessage msg = InternDatas.sendingLine.get(key);
                    long duration = System.currentTimeMillis() + CommonConfig.divideTime - msg.getTime().getTime();
                    if (duration > 10000) {
                        msg.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        InternDatas.sendingLine.remove(key);
                    }
                }
            }
        };
        chkTimer = new Timer();
        chkTimer.schedule(chkTask, 6000, 6000);
    }

    @Override
    public boolean checkUnique() {
        String uuid;
        boolean result = false;
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isTurnOnPsuh()) {
            uuid = PhoneInfoUtils.getUniqueID();
        } else {
            result = true;
            uuid = "";
        }
//        Protocol.registPush(uuid, QTPushConfiguration.getPlatName());
//        Protocol.uploadSelfImei(uuid);
        return result;
    }

    @Override
    public void getMyCapability() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int cv = 0;
                CapabilityResult result = CurrentPreference.getInstance().getCapabilityResult();
                if (result != null) {
                    saveExtConfig(result);
                    cv = result.version;
                }
                final CapabilityResult finalResult = result;
                Protocol.getAbility(Constants.CLIENT_NAME, CurrentPreference.getInstance().getUserid(), cv,
                        new ProtocolCallback.UnitCallback<String>() {

                            @Override
                            public void onFailure(String errMsg) {
                                if (finalResult != null) {
                                    Constants.HONGBAO_URL = (String) finalResult.
                                            otherconfig.get("redpackageurl");
                                    Constants.MY_HONGBAO = (String) finalResult.
                                            otherconfig.get("myredpackageurl");
                                    Constants.HONGBAO_BALANCE = (String) finalResult.
                                            otherconfig.get("balanceurl");
                                    Constants.AA_PAY_URL = (String) finalResult.
                                            otherconfig.get("aacollectionurl");
                                    Constants.THANKS_URL = (String) finalResult.
                                            otherconfig.get("thanksurl");
                                }
                            }

                            @Override
                            public void onCompleted(String s) {
                                if (!TextUtils.isEmpty(s)) {
                                    try {
                                        CapabilityResult ability = getGson().
                                                fromJson(s, CapabilityResult.class);
                                        saveExtConfig(ability);
                                        CurrentPreference.getInstance().setCapabilityResult(ability);
                                    } catch (Exception e) {
                                        LogUtil.e(TAG,"error",e);
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void saveExtConfig(CapabilityResult capability) {
        if (capability != null) {
            Constants.HONGBAO_URL = (String) capability.otherconfig.get("redpackageurl");
            Constants.MY_HONGBAO = (String) capability.otherconfig.get("myredpackageurl");
            Constants.HONGBAO_BALANCE = (String) capability.otherconfig.get("balanceurl");
            Constants.AA_PAY_URL = (String) capability.
                    otherconfig.get("aacollectionurl");
            Constants.THANKS_URL = (String) capability.
                    otherconfig.get("thanksurl");
            Constants.COMPANY = capability.company;
            if (capability.trdextendmsg != null) {
                synchronized (this) {
                    InternDatas.funcButtonDescs.clear();
                    InternDatas.funcButtonDescs.addAll(capability.trdextendmsg);
                }
            }
            if (capability.ability != null) {
                if (capability.ability.base != null) {
                    for (String str : capability.ability.base) {
                        if (str.equals("group")) {
                            CommonConfig.showQchatGroup = true;
                        }
                    }
                }
                CommonConfig.showHongbao = false;
                if (capability.ability.group != null) {
                    for (String str : capability.ability.group) {
                        if (str.equals("redpackage")) {
                            CommonConfig.showHongbao = true;
                        }
                    }
                }
            }
            EventBus.getDefault().post(new EventBusEvent.ShowGroupEvent(CommonConfig.showQchatGroup));
        }
    }

    @Override
    public void getMyConfig() {

    }

    @Override
    public void checkTemplate() {
    }

}
