package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.FavouriteMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.presenter.impl.FavourityMessagePresenter;
import com.qunar.im.base.presenter.views.IFavourityMsgView;
import com.qunar.im.base.presenter.views.ITagView;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtActionBar;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.processor.MessageProcessor;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;


public class MyFavourityMessageInfoActivity extends IMBaseActivity implements ITagView, IFavourityMsgView, View.OnClickListener {
    private TextView tvFromName, tvSetStar, tvSaveTime;
    private ImageView ivStar;
    private LinearLayout llContent;
    private SimpleDraweeView ivGravatar;
    private List<FavouriteMessage> selectMsgs = new ArrayList<>();
    private FavourityMessagePresenter favourityMessagePresenter;
    String tag;
    private static final String tag1 = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_favourite_activity_chat_item_info);
        bindViews();
        favourityMessagePresenter = new FavourityMessagePresenter();
        favourityMessagePresenter.setFavourity(this);
        favourityMessagePresenter.setTagView(this);
        initData();
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        favourityMessagePresenter.showTag();
    }

    private void bindViews() {
        tvFromName = (TextView) findViewById(R.id.tv_from_name);
        tvSetStar = (TextView) findViewById(R.id.tv_set_star);
        tvSaveTime = (TextView) findViewById(R.id.tv_save_time);
        ivStar = (ImageView) findViewById(R.id.iv_star);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        ivGravatar = (SimpleDraweeView) findViewById(R.id.iv_gravatar);
        tvSetStar.setOnClickListener(this);
    }

    public void initData() {
        QtActionBar actionBar = (QtActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
        actionBar.getTitleTextview().setText(R.string.atom_ui_favorite_details);
        Intent intent = getIntent();
        FavouriteMessage fMsg = (FavouriteMessage) intent.getSerializableExtra("msg");
        if(fMsg!=null) {
            ProfileUtils.loadNickName(fMsg.getFromUserId(), tvFromName, false);
            tvSaveTime.setText(getString(R.string.atom_ui_colect)+ DateTimeUtils.getTime(Long.parseLong(fMsg.getTime()), true));
            final IMMessage imMessage = JsonUtils.getGson().fromJson(fMsg.getTextContent(), IMMessage.class);
            updateGravatar(ivGravatar,
                    imMessage.getFromID());
            MessageProcessor processor = ProcessorFactory.getProcessorMap().get(imMessage.getMsgType());
            if (processor == null) {
                processor = ProcessorFactory.getProcessorMap().get(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
            }
            llContent.removeAllViews();
            if (llContent != null) {
                processor.processChatView(llContent, new IMessageItem() {
                    @Override
                    public IMMessage getMessage() {
                        return imMessage;
                    }

                    @Override
                    public int getPosition() {
                        return 0;
                    }

                    @Override
                    public Context getContext() {
                        return MyFavourityMessageInfoActivity.this;
                    }

                    @Override
                    public Handler getHandler() {
                        return QunarIMApp.mainHandler;
                    }

                    @Override
                    public ProgressBar getProgressBar() {
                        return null;
                    }

                    @Override
                    public ImageView getErrImageView() {
                        return null;
                    }

                    @Override
                    public TextView getStatusView() {
                        return null;
                    }

                });
                selectMsgs.clear();
                selectMsgs.add(fMsg);
            }
        }
    }

    public void updateGravatar(final SimpleDraweeView imageView, String fromId) {
        String idOrName = fromId;
        if (!fromId.contains("@conference")) {
            idOrName = QtalkStringUtils.parseBareJid(fromId);
        }
        ProfileUtils.displayGravatarByFullname(idOrName, imageView);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_set_star) {
            if(ivStar.getVisibility() == View.GONE)
            {
                ivStar.setVisibility(View.VISIBLE);
                tag = tag1;
                favourityMessagePresenter.addTag();
            }
            else {
                ivStar.setVisibility(View.GONE);
                tag = tag1;
                favourityMessagePresenter.deleteTag();
            }
        }
    }

    @Override
    public String getSeleteTag() {
        return tag;
    }

    @Override
    public void setTags(List<String> list) {
        if (list==null||list.size() == 0)
        {
            ivStar.setVisibility(View.GONE);
            tvSetStar.setText(R.string.atom_ui_add_star);
        }
        else {
            ivStar.setVisibility(View.VISIBLE);
            tvSetStar.setText(R.string.atom_ui_delete_star);
        }
    }

    @Override
    public List<FavouriteMessage> getSelectedMsgs() {
        return selectMsgs;
    }

    @Override
    public void setFavourityMessages(List<FavouriteMessage> list) {

    }
}
