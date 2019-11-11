package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.google.gson.reflect.TypeToken;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IRobotSessionPresenter;
import com.qunar.im.ui.presenter.ISaveConvMap;
import com.qunar.im.ui.presenter.impl.RobotSessionPresenter;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.events.WeiDaoOrderEvent;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2016/5/19.
 */
public class WebMsgActivity extends QunarWebActvity implements IChatView {
    String robotId;
    IRobotSessionPresenter robotSessionPresenter;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(RobotChatActivity.ROBOT_ID_EXTRA)) {
                robotId = QtalkStringUtils.parseLocalpart(extras_.getString(RobotChatActivity.ROBOT_ID_EXTRA));
            }
        }
        robotSessionPresenter = new RobotSessionPresenter();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        robotSessionPresenter.setView(this);
        robotSessionPresenter.propose();
    }
    /*
     var data={
              "time": 1462943381886,
              "from": {
                "user":"guide",
                "userImage":"http://img1.qunarzz.com/vc/1a/8e/20/4fb4e7e47eed9f6e3912de6df0.jpg_92.jpg"
              },
              "htmlcontent":"<div class=\"m-msg\"><div class=\"title\"><div class=\"flex\">#名古屋#问题</div><div class=\"mark\"></div></div><div class=\"content\">%s请问我要从名古屋往返东京，对于交通方式有什么好的建议？</div><a href=\"javascript:void(0);\" class=\"btn\">立即抢单</a></div>"
            };
     */

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void loadUrl()
    {
        mWebView.addJavascriptInterface(this,"ClientApi");
        mWebView.loadUrl(mUrl);

    }

    @JavascriptInterface
    public void openNewLink(String url)
    {
        Intent intent = new Intent(this,WebMsgActivity.class);
        intent.setData(Uri.parse(url));
        intent.putExtra(RobotChatActivity.ROBOT_ID_EXTRA,robotId);
        startActivity(intent);
    }

    @JavascriptInterface
    public void openNewSession(String sessionId)
    {
        if(robotSessionPresenter instanceof ISaveConvMap)
        {
            ((ISaveConvMap)robotSessionPresenter).saveConvMap(sessionId);
            Intent intent =new Intent(this, PbChatActivity.class);
            intent.putExtra("jid",QtalkStringUtils.userId2Jid(sessionId));
            intent.putExtra("isFromChatRoom",
                    false);
            startActivity(intent);
        }
    }
    @JavascriptInterface
    public void closeBrowser(Object code)
    {
        finish();
    }

    @JavascriptInterface
    public void updateCardState(String cardId,Object status)
    {
        String s = "0";
        if(status!=null) s = status.toString();
        WeiDaoOrderEvent event = new WeiDaoOrderEvent(cardId,s);
        EventBus.getDefault().post(event);
    }

    public void onEventMainThread(WeiDaoOrderEvent event)
    {
        mWebView.loadUrl("javascript:updateMessage('"+event.dealId+"','"+event.status+"')");
    }

    @Override
    public String getInputMsg() {
        return "";
    }

    @Override
    public void setInputMsg(String text) {

    }

    public void onEvent(final EventBusEvent.HasNewMessageEvent event) {
        final IMMessage message = event.mMessage;
        if (message != null && message.getConversationID().equals(robotId)) {
            robotSessionPresenter.receiveMsg(message);
        }
    }

    @Override
    public void setNewMsg2DialogueRegion(IMMessage imMessage) {
        if(imMessage !=null&&imMessage.getMsgType()== ProtoMessageOuterClass.MessageType.MessageTypeMicroTourGuide_VALUE) {
            String ext = imMessage.getExt();
            Map<String, Object> data = JsonUtils.getGson().fromJson(ext, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            data.put("time", imMessage.getTime().getTime());
            String html = JsonUtils.getGson().toJson(data);
            mWebView.loadUrl("javascript:pushMessage('" + html + "')");
        }
    }

    @Override
    public void showUnReadCount(int count) {

    }

    @Override
    public String getAutoReply() {
        return null;
    }

    @Override
    public String getOf() {
        return null;
    }

    @Override
    public String getOt() {
        return null;
    }

    @Override
    public String getRealJid() {
        return null;
    }

    @Override
    public String getFromId() {
        return QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid());
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getToId() {
        return robotId;
    }

    @Override
    public String getChatType() {
        return null;
    }

    @Override
    public String getUserId() {
        return CurrentPreference.getInstance().getUserid();
    }

    @Override
    public String getUploadImg() {
        return "";
    }

    @Override
    public String getTransferId() {
        return "";
    }

    @Override
    public void initActionBar() {

    }

    @Override
    public List<IMMessage> getSelMessages() {
        return null;
    }

    @Override
    public void refreshDataset() {

    }

    @Override
    public void setCurrentStatus(String s) {

    }

    @Override
    public void setHistoryMessage(List<IMMessage> list,int unread) {
        for(IMMessage msg:list)
        {
            if(msg !=null&&msg.getMsgType()==ProtoMessageOuterClass.MessageType.MessageTypeMicroTourGuide_VALUE) {
                String ext = msg.getExt();
                Map<String, Object> data = JsonUtils.getGson().fromJson(ext, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                data.put("time", msg.getTime().getTime());
                String html = JsonUtils.getGson().toJson(data);
                mWebView.loadUrl("javascript:pushMessage(" + html + ")");
            }
        }
    }

    @Override
    public void addHistoryMessage(List<IMMessage> list) {

    }

    @Override
    public void addHistoryMessageLast(List<IMMessage> historyMessage) {

    }

    @Override
    public void showNoticePopupWindow(NoticeBean noticeBean) {

    }

    @Override
    public Map<String, String> getAtList() {
        return null;
    }

    @Override
    public void clearAndAddMsgs(List<IMMessage> historyMessage, int unread) {

    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public int getListSize() {
        return 0;
    }

    @Override
    public void setTitleState(String stats) {

    }

    @Override
    public void revokeItem(IMMessage imMessage) {

    }

    @Override
    public void deleteItem(IMMessage imMessage) {

    }

    @Override
    public boolean isFromChatRoom() {
        return false;
    }

    @Override
    public void replaceItem(IMMessage imMessage) {

    }

    @Override
    public void sendEditPic(String path) {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void parseEncryptSignal(IMMessage message) {

    }

    @Override
    public void updateUploadProgress(IMMessage message, int progress, boolean isDone) {

    }

    @Override
    public void isEmotionAdd(boolean flag) {

    }

    @Override
    public void popNotice(NoticeBean noticeBean) {

    }

    @Override
    public void clearMessage() {

    }

    @Override
    public boolean isMessageExit(String msgId) {
        return false;
    }

    @Override
    public int getUnreadMsgCount() {
        return 0;
    }

    @Override
    public void sendRobotMsg(String msg) {

    }


    @Override
    public String getBackupInfo() {
        return null;
    }

    @Override
    public IMMessage getListFirstItem() {
        return null;
    }

    @Override
    public IMMessage getListLastItem() {
        return null;
    }

    @Override
    public void onRefreshComplete() {

    }

    @Override
    public boolean getSearching() {
        return false;
    }

    @Override
    public void showToast(String str) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public String getRefenceString() {
        return null;
    }

    @Override
    public void payRedEnvelopChioce(String type,String rid) {

    }

    @Override
    public void payOrder(String orderInfo) {

    }

    @Override
    public void payAuth(String authInfo) {

    }
}

