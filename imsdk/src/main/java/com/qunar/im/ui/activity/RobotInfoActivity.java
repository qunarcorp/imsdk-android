package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.presenter.IRobotInfoPresenter;
import com.qunar.im.ui.presenter.impl.RobotInfoPresenter;
import com.qunar.im.ui.presenter.views.IRobotInfoView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-9-7.
 */
public class RobotInfoActivity extends IMBaseActivity implements IRobotInfoView, View.OnClickListener {
    public final static String ROBOT_ID_EXTRA = "robotId";
    public final static String MSG_TYPE_EXTRA = "msgType";
    public final static String CONTENT_EXTRA = "content";
    public final static String IS_HIDEN_EXTRA = "isHiden";

    TextView introduction, pnid, name, fromsource, tel, robot_qr_code;
    Button enter;
    SimpleDraweeView header;
    Button unFollow;

    String robotId, content, msgType;
    String jid;
    boolean isHiden;

    boolean isFollow;

    IRobotInfoPresenter robotInfoPresenter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_robot_info);
        bindViews();
        injectExtras_();
        robotInfoPresenter = new RobotInfoPresenter();
        robotInfoPresenter.setRobotInfoView(this);
        initView();
    }

    private void injectExtras_() {

        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(ROBOT_ID_EXTRA)) {
                jid = extras_.getString(ROBOT_ID_EXTRA);
                robotId = QtalkStringUtils.parseLocalpart(jid);
            }
            if (extras_.containsKey(MSG_TYPE_EXTRA)) {
                msgType = extras_.getString(MSG_TYPE_EXTRA);
            }
            if (extras_.containsKey(CONTENT_EXTRA)) {
                content = extras_.getString(CONTENT_EXTRA);
            }
            if (extras_.containsKey(IS_HIDEN_EXTRA)) {
                isHiden = extras_.getBoolean(IS_HIDEN_EXTRA);
            }
        }
    }

    private void bindViews() {
        header = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.header);
        name = (TextView) findViewById(R.id.name);
        pnid = (TextView) findViewById(R.id.pnid);
        introduction = (TextView) findViewById(R.id.introduction);
        fromsource = (TextView) findViewById(R.id.fromsource);
        tel = (TextView) findViewById(R.id.tel);
        robot_qr_code = (TextView) findViewById(R.id.robot_qr_code);
        enter = (Button) findViewById(R.id.enter);
        unFollow = (Button) findViewById(R.id.unFollow);
        unFollow.setOnClickListener(this);
    }

    void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_contact_tab_public_number);
        robotInfoPresenter.loadRobotInfo();
        if (isHiden) {
            enter.setVisibility(View.GONE);
        }
    }


    void unFollow() {
        robotInfoPresenter.unfollowRobot();
    }

    @Override
    public String getRobotId() {
        return robotId;
    }

    @Override
    public void setFollowRobotResult(final boolean b) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (b) {
                    startRobotChatActivity();
                } else {
                    Toast.makeText(RobotInfoActivity.this, "添加机器人失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void setUnfollowRobotResult(boolean b) {
        if (b) {
            EventBus.getDefault().post(new EventBusEvent.CancelFollowRobot(robotId));
            this.finish();
        }
    }

    @Override
    public void setInfo(final Map<String, String> infoMap) {
        if (infoMap == null || infoMap.isEmpty())
            return;
        final String id = infoMap.get("id");
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                pnid.setText(id);
                name.setText(infoMap.get("name"));
                introduction.setText(infoMap.get("description"));
            }
        });
        String url = QtalkStringUtils.getGravatar(infoMap.get("gravatarUrl"), true);
        FacebookImageUtil.loadWithCache(url, header, ImageRequest.CacheChoice.SMALL, false);
        if (!TextUtils.isEmpty(id)) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    robot_qr_code.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent qrIntent = new Intent(RobotInfoActivity.this, QRActivity.class);
                            qrIntent.putExtra("qrString",
                                    Constants.Config.QR_SCHEMA + "://robot?id=" + id + "&type=robot");
                            startActivity(qrIntent);
                        }
                    });

                    enter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFollow) {
                                startRobotChatActivity();
                            } else {
                                robotInfoPresenter.followRobot();
                            }
                        }
                    });
                }
            });

        }
    }

    private void startRobotChatActivity() {
        Intent intent = new Intent(this, RobotChatActivity.class);
        intent.putExtra("robotId", robotId);
        intent.putExtra("content", content);
        intent.putExtra("msgType", msgType);
        startActivity(intent);
        this.finish();
    }

    @Override
    public String getUserId() {
        return CurrentPreference.getInstance().getUserid();
    }

    @Override
    public void setFollowStatus(final boolean followStatus) {
        isFollow = followStatus;
        if (isFollow) {
            unFollow.setVisibility(View.VISIBLE);
            enter.setText("进入公众号");
            if (content != null) {
                startRobotChatActivity();
            }
        } else {
            enter.setText("关注");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.unFollow) {
            unFollow();
        }
    }
}
