package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.FeedBackServcie;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BugreportActivity extends SwipeBackActivity implements IMNotificaitonCenter.NotificationCenterDelegate{
    private static final String TAG = "BugreportActivity";
    private static final String QCHAT_SERVICE = "gunjern9357@ejabhost2";
    private final String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    private String username = "";
    private String content = "";
    private String mobile = "";
    private EditText feedback_username;
    private EditText feedback_mobile;
    private EditText feedback_content;
    private Button feedback_send;

    //自定义日志本地存储
    private final String folder = MyDiskCache.CACHE_LOG_DIR;//save path
    private String tagetZipName = folder
            + "/log_android_"
            + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date(System.currentTimeMillis()))
//            + "_" + CurrentPreference.getInstance().getPreferenceUserId()
            + ".zip";

    //    String[] devs = new String[]{"xingchao.song@ejabhost1","xinbo.wang@ejabhost1"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_bugreport);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_mine_feedback);

        feedback_content = (EditText) findViewById(R.id.feedback_content);
        feedback_username = (EditText) findViewById(R.id.feedback_username);
        feedback_mobile = (EditText) findViewById(R.id.feedback_mobile);
        feedback_send = (Button) findViewById(R.id.feedback_send);
        feedback_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = feedback_content.getText().toString();
                username = feedback_username.getText().toString();
                mobile = feedback_mobile.getText().toString();
//                if (TextUtils.isEmpty(CurrentPreference.getInstance().getUserid()) && TextUtils.isEmpty(username)){
//                    Toast.makeText(BugreportActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(BugreportActivity.this, R.string.atom_ui_tip_problem_desc, Toast.LENGTH_SHORT).show();
                    return;
                }
                DispatchHelper.Async("uploadLog", true, new Runnable() {
                    @Override
                    public void run() {
                        FeedBackServcie feedBackServcie = new FeedBackServcie();
                        feedBackServcie.setCallBack(null);
                        feedBackServcie.setNotify(false);
                        feedBackServcie.setVoids(new String[]{content,username,mobile});
                        feedBackServcie.handleLogs();
                    }
                });
                toast("已提交反馈!");
//                finish();
//                FeedBackServcie.runFeedBackServcieService(BugreportActivity.this,new String[]{content,username,mobile});
//                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.FEED_BACK, new String[]{content,username,mobile},true);
            }
        });

        TextView tv_open_dialog = (TextView) this.findViewById(R.id.tv_open_dialog);
        tv_open_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHelpChat("");
            }
        });

        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.FEED_BACK_RESULT);
    }

    @Override
    protected void onDestroy() {
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.FEED_BACK_RESULT);
        super.onDestroy();
    }

    private void startHelpChat(String logFilePath) {
        Random random = new Random();
        int r = random.nextInt(devs.length);
        if (r < 0) {
            r = 0;
        } else if (r >= devs.length) {
            r = devs.length - 1;
        }
        String content = feedback_content.getText().toString();
        Intent intent = new Intent(BugreportActivity.this, PbChatActivity.class);
        String jid = CommonConfig.isQtalk ? devs[r] : QCHAT_SERVICE;
        intent.putExtra("jid", jid);
        intent.putExtra("content", content);
        intent.putExtra("isFromChatRoom", false);
        intent.putExtra("sendLogFile", logFilePath);
        BugreportActivity.this.startActivity(intent);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key){
            case QtalkEvent.FEED_BACK_RESULT:
                boolean result = (boolean) args[0];
                toast(result ? "反馈成功，谢谢您的反馈！" : "oops反馈失败，请重试！");
                break;
        }
    }
}