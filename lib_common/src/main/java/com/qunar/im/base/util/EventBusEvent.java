package com.qunar.im.base.util;

import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.base.module.IMMessage;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by xingchao.song on 9/16/2015.
 */
public class EventBusEvent {
    public static class HasNewMessageEvent {
        public IMMessage mMessage;
        public HasNewMessageEvent(IMMessage message){
                this.mMessage = message;
            }
    }

    public static class HasNewOpsMsgEvent{

    }

    public static class ReadThirdMsgEvent{

    }

    public static class CancelFollowRobot{
        public String robotId;
        public CancelFollowRobot(String robotId)
        {
            this.robotId = robotId;
        }
    }

    public static class RefreshMessageStatusEvent{
        public String jid;
        public RefreshMessageStatusEvent(String id)
        {
            jid = id;
        }
    }

    public static class GravtarGot{
        public GravtarGot(String url,String jid){
            murl = url;
            this.jid = jid;
        }
        public String jid;
        public String murl;
    }

    public static class LoginComplete
    {
        public boolean loginStatus;
        public LoginComplete(boolean loginStatus)
        {
            this.loginStatus = loginStatus;
        }
    }

    public static class ReloginEvent
    {
        public boolean conflict;
        public ReloginEvent(boolean conflict){this.conflict = conflict;}
    }

    public static class GravantarChanged
    {
    }

    public static class ReceivedHistory
    {
        public long latestHistoryTime;
        public String id;
        public ReceivedHistory(long l,String id){
            this.latestHistoryTime = l;
            this.id = id;
        }
    }

    public static class FriendsChange{
        public boolean result;
        public FriendsChange(boolean result){
            this.result = result;
        }
    }

    public static class ShowGroupEvent
    {
        public boolean isShow;
        public ShowGroupEvent(boolean isShow)
        {
            this.isShow = isShow;
        }
    }

    public static class HandleOrderOperation
    {
        public IMMessage message;
        public HandleOrderOperation(IMMessage m)
        {
            message = m;
        }
    }

    public static class GravanterSelected{
        public File selectedFile;
        public GravanterSelected(File file)
        {
            this.selectedFile = file;
        }
    }

    public static class SendTransMsg{
        public Serializable msg;
        public String transId;
        public SendTransMsg(Serializable serializable,String str)
        {
            msg = serializable;
            transId = str;
        }
    }

    public static class SendShareMsg{
        public String msg;
        public String shareId;
        public SendShareMsg(String extMsg,String str)
        {
            msg = extMsg;
            shareId = str;
        }
    }

    public static class RefreshChatroom
    {
        public String roomId;
        public String roomName;
        public RefreshChatroom(String id,String name)
        {
            roomId = id;
            roomName = name;
        }
    }

    public static class TypingEvent{
        public String jid;
        public TypingEvent(String id)
        {
            this.jid = id;
        }
    }

    public static class KinckoffChatroom
    {
        public String roomId;
        public KinckoffChatroom(String id)
        {
            roomId = id;
        }
    }

    public static class restartChat {}

    public static class CleanHistory{
        public String jid;
        public CleanHistory(String id)
        {
            jid = id;
        }
    }

    public static class DownEmojComplete
    {
        public String pkgId;
        public String name;
        public DownEmojComplete(String id,String n)
        {
            pkgId = id;
            name = n;
        }
    }

    public static class ChangeMood{
        public String mood;
        public ChangeMood(String mood){
            this.mood = mood;
        }
    }

    public static class NewPictureEdit{
        public NewPictureEdit(String picturePath){
            mPicturePath = picturePath;
        }
        public String mPicturePath;
    }
    public static class ShareLocationMessage{
        public ShareLocationMessage(IMMessage message){
            this.message = message;
        }

        public IMMessage getMessage() {
            return message;
        }
        private IMMessage message;
    }
    public static class ThirdNotify{

    }

    public static class VerifyFriend{
        public Map<String,String> mode;
        public VerifyFriend(Map<String,String> map){
            this.mode = map;
        }
    }

    public static class UpdateVoiceMessage{
        public IMMessage message;
        public UpdateVoiceMessage(IMMessage msg)
        {
            this.message = msg;
        }
    }
    public static class UpdateFileMessage{
        public IMMessage message;
        public UpdateFileMessage(IMMessage message){this.message = message;}
    }

    public static class WebRtcMessage{
        public IMMessage message;
        public WebRtcMessage(IMMessage message){this.message = message;}
    }
    public static class PasswordBox{
        public DailyMindSub dailyMindSub;
        public DailyMindMain dailyMindMain;
        public List<DailyMindMain> dailyMindMains;
        public List<DailyMindSub> dailyMindSubs;
        public PasswordBox(DailyMindSub dailyMindSub){this.dailyMindSub = dailyMindSub;}
        public PasswordBox(DailyMindMain dailyMindMain){this.dailyMindMain = dailyMindMain;}
        public PasswordBox(List<DailyMindMain> dailyMindMains, List<DailyMindSub> dailyMindSubs){
            this.dailyMindMains = dailyMindMains;
            this.dailyMindSubs = dailyMindSubs;
        }
    }
}
