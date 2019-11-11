package com.qunar.im.ui.presenter.impl;

import android.os.Environment;
import android.text.TextUtils;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.HongbaoContent;
import com.qunar.im.base.jsonbean.QunarLocation;
import com.qunar.im.base.jsonbean.RequestRobotInfo;
import com.qunar.im.base.jsonbean.RobotInfoResult;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotBody;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotItemResult;
import com.qunar.im.base.jsonbean.ShareLocation;
import com.qunar.im.base.jsonbean.ThirdRequestMsgJson;
import com.qunar.im.base.jsonbean.ThirdResponseMsgJson;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.base.module.PublishPlatformNews;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.IRobotSessionPresenter;
import com.qunar.im.ui.presenter.IRushOrderPresenter;
import com.qunar.im.ui.presenter.ISaveConvMap;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.model.IPublishPlatformDataModel;
import com.qunar.im.ui.presenter.model.IPublishPlatformNewsDataModel;
import com.qunar.im.ui.presenter.model.impl.PublishPlatformDataModel;
import com.qunar.im.ui.presenter.model.impl.PublishPlatformNewsDataModel;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.ui.presenter.views.IRobotChatView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.RobotAPI;
import com.qunar.im.base.structs.MachineType;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.PbImageMessageQueue;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by saber on 15-9-14.
 */
public class RobotSessionPresenter implements IRobotSessionPresenter, ICloudRecordPresenter, IRushOrderPresenter, ISaveConvMap, IMNotificaitonCenter.NotificationCenterDelegate {
    IChatView chatView;
    IRobotChatView robotChatView;
    IPublishPlatformNewsDataModel publishPlatformNewsDataModel;
    IPublishPlatformDataModel publishPlatformDataModel;

    PublishPlatform publishPlatform;

    private String currentCookie;

    private int curMsgNum = 0;
    private int numPerPage = 10;

    private Map<String, IMMessage> rushQueue = new HashMap<>();
    private ConnectionUtil connectionUtil;

    public RobotSessionPresenter() {
        publishPlatformNewsDataModel = new PublishPlatformNewsDataModel();
        publishPlatformDataModel = new PublishPlatformDataModel();
    }
    @Override
    public void setIRobotChatView(IRobotChatView robotChatView) {
        this.robotChatView = robotChatView;
    }

    @Override
    public void sendActionMsg(String body) {
        IMMessage message = generateIMMessage();
        message.setBody(body);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeTopic_VALUE);
        connectionUtil.sendSubscriptionMessage(message);
    }

    @Override
    public void setView(IChatView view) {
        chatView = view;
        connectionUtil = ConnectionUtil.getInstance();
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
    }

    @Override
    public void removeEventForSearch() {

    }

    @Override
    public void addEventForSearch() {

    }


    @Override
    public void removeEvent() {
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
    }

    @Override
    public void clearAndReloadMessages() {

    }

    @Override
    public void showMoreOldMsg(boolean isFromGroup) {
        List<PublishPlatformNews> publishPlatformNewses = publishPlatformNewsDataModel.getMsgWithLimit(QtalkStringUtils.userId2Jid(robotChatView.getRobotId()), numPerPage, curMsgNum);
        List<IMMessage> imMessages = new ArrayList<IMMessage>(publishPlatformNewses.size());
        for (int i = publishPlatformNewses.size() - 1; i >= 0; i--) {
            PublishPlatformNews publishPlatformNews = publishPlatformNewses.get(i);
            IMMessage message = new IMMessage();
            message.setMsgType(publishPlatformNews.msgType);
            if (publishPlatformNews.direction == IMMessage.DIRECTION_SEND) {
                message.setFromID(CurrentPreference.getInstance().getUserid());
                message.setToID(publishPlatformNews.platformXmppId);
            } else {
                message.setFromID(publishPlatformNews.platformXmppId);
                message.setToID(CurrentPreference.getInstance().getUserid());
            }
            message.setType(ConversitionType.MSG_TYPE_SUBSCRIPT);
            message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
            message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
            message.setConversationID(publishPlatform.getId());
            message.setExt(publishPlatformNews.extentionFlag);
            imMessages.add(message);
        }
        chatView.addHistoryMessage(imMessages);
        curMsgNum += imMessages.size();
    }

    @Override
    public void showMoreOldMsgUp(boolean isFromGroup) {

    }

    @Override
    public void propose() {
        publishPlatform = publishPlatformDataModel.selectById(QtalkStringUtils.userId2Jid(robotChatView.getRobotId()));
        reloadMessages();
        if (publishPlatform != null && publishPlatform.getPublishPlatformInfo() != null) {
            robotChatView.setRobotInfo(JsonUtils.getGson().fromJson(publishPlatform.getPublishPlatformInfo(), RobotBody.class), publishPlatform.getPublishPlatformType());
        }
        int version = -1;
        if (publishPlatform != null) {
            version = publishPlatform.getVersion();
        }
        List<RequestRobotInfo> list = new ArrayList<RequestRobotInfo>(1);
        RequestRobotInfo requestRobotInfo = new RequestRobotInfo(robotChatView.getRobotId(), version);
        list.add(requestRobotInfo);
        RobotAPI.getRobotInfo(list, new ProtocolCallback.UnitCallback<RobotInfoResult>() {
            @Override
            public void onCompleted(RobotInfoResult robotInfoResult) {
                if (robotInfoResult.ret && !ListUtil.isEmpty(robotInfoResult.data)) {
                    List<RobotItemResult> results = robotInfoResult.data;
                    if (results.size() > 0) {
                        RobotItemResult item = results.get(0);
                        PublishPlatform platform = publishPlatform;
                        if (platform == null) {
                            platform = new PublishPlatform();
                            if (item.rbt_name != null) {
                                platform.setId(QtalkStringUtils.userId2Jid(item.rbt_name));
                            }
                        }
                        if (item.rbt_body != null) {
                            RobotBody body = item.rbt_body;
                            updateInfo(body, platform);
                            platform.setExtentionFlag((body.receiveswitch ? 1 : 0) | platform.getExtentionFlag());
                            platform.setPublishPlatformInfo(JsonUtils.getGson().toJson(item.rbt_body, RobotBody.class));
                        }
                        publishPlatformDataModel.insertOrUpdatePublishPlatform(platform);
                    }
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    private void updateInfo(RobotBody body, PublishPlatform platform) {
        if (body.robotDesc != null) {
            if (platform != null)
                platform.setDescription(body.robotDesc);
        }
        if (body.headerurl != null) {
            if (platform != null)
                platform.setGravatarUrl(body.headerurl);
        }
        if (body.robotCnName != null) {
            if (platform != null)
                platform.setName(body.robotCnName);
        }

        if (robotChatView != null)
            robotChatView.setRobotInfo(body, platform == null ? 1 : platform.getPublishPlatformType());
    }

    @Override
    public void sendMsg() {
        String msg = chatView.getInputMsg();
        msg = ChatTextHelper.textToHTML(msg);
        send2Server(msg);
    }

    private IMMessage send2Server(String msg) {
        IMMessage message = generateIMMessage();
        message.setBody(msg);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        connectionUtil.sendSubscriptionMessage(message);
        return message;
    }

    private IMMessage generateIMMessage() {
        IMMessage message = new IMMessage();
        Date time = Calendar.getInstance().getTime();
        String id = UUID.randomUUID().toString();
        String key = QtalkStringUtils.userId2Jid(chatView.getToId());
        message.setId(id);
        message.setMessageID(id);
        message.setUserId(CurrentPreference.getInstance().getPreferenceUserId());
        message.setType(ConversitionType.MSG_TYPE_SUBSCRIPT);
        message.setFromID(chatView.getFromId());
        message.setToID(key);
        message.setMessageID(id);
        message.setTime(time);
        message.setDirection(IMMessage.DIRECTION_SEND);
        message.setIsRead(IMMessage.MSG_READ);
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
        message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
        message.setConversationID(key);
        message.setMaType(MachineType.MachineTypeAndroid);
        return message;
    }

    private void updateDbOnSuccess(IMMessage message, boolean update) {
        PublishPlatformNews publishPlatformNews = new PublishPlatformNews();
        publishPlatformNews.id = message.getId();
        publishPlatformNews.extentionFlag = message.getExt();
        publishPlatformNews.state = message.getMessageState();
        publishPlatformNews.readTag = message.getIsRead();
        publishPlatformNews.platformXmppId = message.getConversationID();
        publishPlatformNews.direction = message.getDirection();
        publishPlatformNews.content = message.getBody();
        publishPlatformNews.latestUpdateTime = message.getTime().getTime();
        publishPlatformNews.msgType = message.getMsgType();
        publishPlatformNewsDataModel.insertOrUpdateNews(publishPlatformNews);
    }

    @Override
    public void receiveMsg(IMMessage message) {
        LogUtil.d("message", "robot message");
        if (message == null) return;
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeTopic_VALUE) {
            return;
        }
        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeReply_VALUE) {
            currentCookie = message.getBody();
            return;
        }
        if (message.getMaType().equals(MessageType.RQUEST_COOKIE_MESSAGE)) {
            if (!TextUtils.isEmpty(currentCookie)) {
                message = generateIMMessage();
                message.setBody(currentCookie);
            }
            return;
        }
        LogUtil.d("message", "robot message id:" + message.getConversationID());
        PublishPlatformNews publishPlatformNews = new PublishPlatformNews();
        publishPlatformNews.id = message.getId();
        publishPlatformNews.extentionFlag = message.getExt();
        publishPlatformNews.state = message.getMessageState();
        publishPlatformNews.readTag = message.getIsRead();
        publishPlatformNews.platformXmppId = message.getConversationID();
        publishPlatformNews.direction = message.getDirection();
        publishPlatformNews.content = message.getBody();
        publishPlatformNews.latestUpdateTime = message.getTime().getTime();
        publishPlatformNews.msgType = message.getMsgType();
        chatView.setNewMsg2DialogueRegion(message);
    }

    @Override
    public void close() {

    }

    @Override
    public void sendImage() {
        final IMMessage message = generateIMMessage();

        HttpUtil.uploadAndSendImage(message, chatView.getUploadImg(), chatView.getToId(), new HttpUtil.SendCallback() {
            @Override
            public void send() {
                chatView.setNewMsg2DialogueRegion(message);
                curMsgNum++;
            }

            @Override
            public void updataProgress(int progress, boolean isDone) {

            }
        });
        IMDatabaseManager.getInstance().InsertPublicNumberMessage(message);
    }

    @Override
    public void sendFile(final String file) {
        final String fileName = file.substring(file.lastIndexOf("/") + 1);
        final IMMessage message = generateIMMessage();
        message.setBody("发送文件:" + fileName);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);
        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = file;
        request.id = message.getId();
        request.FileType = UploadImageRequest.FILE;
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    String fileSize = FileUtils.getFormatFileSize(file);
                    TransitFileJSON json = new TransitFileJSON(result.httpUrl, fileName, fileSize, message.getId(), "");
                    message.setBody(JsonUtils.getGson().toJson(json));
                    IMDatabaseManager.getInstance().InsertPublicNumberMessage(message);
                    if (chatView.isFromChatRoom()) {
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
                } else {
                    message.setMessageState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                message.setMessageState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                InternDatas.sendingLine.remove(message.getId());
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);

    }

    @Override
    public void sendVideo(VideoMessageResult videoMessageResult) {

    }

    /**
     * 发送文件第一帧
     *
     * @param file
     */
    @Override
    public void sendVideo(final String file) {
        if (TextUtils.isEmpty(file)) {
            return;
        }
        final IMMessage message = generateIMMessage();
        //上传并发送视频
        HttpUtil.uploadAndSendVideo(file, message, new HttpUtil.SendCallback() {
            @Override
            public void send() {
                chatView.setNewMsg2DialogueRegion(message);
                curMsgNum++;
            }

            @Override
            public void updataProgress(int progress, boolean isDone) {

            }
        }, chatView.isFromChatRoom());
        IMDatabaseManager.getInstance().InsertPublicNumberMessage(message);
    }

    /**
     * 发送视频文件本身
     */
    public void sendVideoFile(final String sourceFilePath, final IMMessage message, final String frameUrl, int videoW, int videoH) {
        final String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
        message.setBody("发送视频:" + fileName);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
        final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(sourceFilePath);
        videoInfo.FileName = fileName;
        videoInfo.ThumbUrl = frameUrl;
        videoInfo.Height = String.valueOf(videoH);
        videoInfo.Width = String.valueOf(videoW);
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        File videoFile = new File(path, fileName);
        File sourceFile = new File(sourceFilePath);
        String uploadPath = sourceFilePath;
        if (sourceFile.renameTo(videoFile)) {
            uploadPath = videoFile.getPath();
        }
        message.setExt(JsonUtils.getGson().toJson(videoInfo));
        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;
        updateDbOnSuccess(message, true);
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = uploadPath;
        request.FileType = UploadImageRequest.FILE;
        request.id = message.getId();
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    message.setBody("发送了一段视频. [obj type=\"url\" value=\"" + QtalkStringUtils.addFilePathDomain(result.httpUrl, true)
                            + "\"]");
                    videoInfo.FileUrl = result.httpUrl;
                    message.setExt(JsonUtils.getGson().toJson(videoInfo));
                } else {
                    message.setMessageState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                }
            }

            @Override
            public void onError(String msg) {
                message.setMessageState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    @Override
    public void reset() {

    }

    @Override
    public void transferMessage() {
        IMMessage newMsg = generateIMMessage();
        List<IMMessage> selMessages = chatView.getSelMessages();
        if (selMessages == null || selMessages.size() == 0) return;
        IMMessage originMsg = selMessages.get(0);
        String toId = chatView.getTransferId();
        newMsg.setToID(toId);
        newMsg.setBody(originMsg.getBody());
        newMsg.setDirection(IMMessage.DIRECTION_SEND);
        newMsg.setConversationID(toId);
        newMsg.setMsgType(originMsg.getMsgType());
        newMsg.setExt(originMsg.getExt());
        if (toId.contains("@conference")) {
            newMsg.setType(ConversitionType.MSG_TYPE_GROUP);
            connectionUtil.sendGroupTextOrEmojiMessage(newMsg);
        } else {
            newMsg.setType(ConversitionType.MSG_TYPE_CHAT);
            connectionUtil.sendTextOrEmojiMessage(newMsg);
            newMsg.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
        }
    }

    @Override
    public void resendMessage() {
        List<IMMessage> selMessages = chatView.getSelMessages();
        if (selMessages == null || selMessages.size() == 0) return;
        final IMMessage message = selMessages.get(0);
        if (message.getMessageState() == MessageStatus.LOCAL_STATUS_FAILED) {
            message.setTime(new Timestamp(System.currentTimeMillis()
                    + CommonConfig.divideTime));
            chatView.deleteItem(message);

            chatView.setNewMsg2DialogueRegion(message);
            if (chatView.isFromChatRoom()) {
                connectionUtil.sendGroupTextOrEmojiMessage(message);
            } else {
                connectionUtil.sendTextOrEmojiMessage(message);
            }
        }
    }

    @Override
    public void sendLocation(QunarLocation location) {
        //我在这里，点击查看：http://api.map.baidu.com/marker?location=39.985460,116.312695&title=我的位置&content=中国北京市海淀区海淀街道苏州街&output=html
        StringBuilder sb = new StringBuilder();
        sb.append("我在这里，点击查看：");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://api.map.baidu.com/marker?location=");
        urlBuilder.append(location.latitude);
        urlBuilder.append(",");
        urlBuilder.append(location.longitude);
        urlBuilder.append("&title=我的位置&content=");
        urlBuilder.append(location.addressDesc);
        urlBuilder.append("&output=html");
        String urlString = ChatTextHelper.textToUrl(urlBuilder.toString());
        sb.append(urlString);
        sb.append(location.addressDesc);
        final ShareLocation jsonLocation = new ShareLocation();
        jsonLocation.latitude = Double.toString(location.latitude);
        jsonLocation.longitude = Double.toString(location.longitude);
        jsonLocation.adress = location.addressDesc;
        jsonLocation.fileUrl = "file://" + location.fileUrl;
        jsonLocation.name = location.name;
        String extInfo = JsonUtils.getGson().toJson(jsonLocation);
        final IMMessage message = generateIMMessage();
        message.setBody(sb.toString());
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE);
        message.setMessageState(MessageStatus.LOCAL_STATUS_PROCESSION);
        message.setExt(extInfo);
        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        IMDatabaseManager.getInstance().InsertPublicNumberMessage(message);
        final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
        packet.key = chatView.getToId();
        if (PbImageMessageQueue.packetMap.containsKey(chatView.getToId())) {
            PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(chatView.getToId());
            while (header.next != null) {
                header = header.next;
            }
            header.next = packet;
        } else {
            packet.isFirst = true;
            PbImageMessageQueue.packetMap.put(packet.key, packet);
        }
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = location.fileUrl;
        request.id = message.getId();
        request.FileType = UploadImageRequest.IMAGE;
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    File file = MyDiskCache.getFile(
                            QtalkStringUtils.addFilePathDomain(result.httpUrl,
                                    true));
                    File originFile = new File(jsonLocation.fileUrl);
                    FileUtils.copy(originFile, file);
                    originFile.delete();
                    jsonLocation.fileUrl = result.httpUrl;
                    String extInfo = JsonUtils.getGson().toJson(jsonLocation);
                    message.setExt(extInfo);
                    packet.message = message;
                    packet.approveSend();
                } else {
                    packet.removed();
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                packet.removed();
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                InternDatas.sendingLine.remove(message.getId());
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    @Override
    public void sendTypingStatus() {

    }

    @Override
    public void deleteMessge() {
        List<IMMessage> messages = chatView.getSelMessages();
        if (messages == null || messages.size() == 0) return;
        for (IMMessage message : messages) {
            chatView.deleteItem(message);
            connectionUtil.deleteMessage(message);
//            messageRecordDataModel.delSingleMessage(message, chatView.getToId());
            curMsgNum--;
        }
//        reloadMessages();
    }

    @Override
    public void transferConversation() {

    }


    @Override
    public void hongBaoMessage(HongbaoContent content) {

    }

    @Override
    public void revoke() {

    }

    @Override
    public void sendExtendMessage(ExtendMessageEntity entity) {
        String msg = entity.linkurl;
        if (!TextUtils.isEmpty(msg)) {
            String extStr = JsonUtils.getGson().toJson(entity);
            msg = ChatTextHelper.textToUrl(msg);
            IMMessage message = generateIMMessage();
            message.setBody(msg);
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE);
            message.setExt(extStr);

            curMsgNum++;

            chatView.setNewMsg2DialogueRegion(message);
        }
    }

    @Override
    public void shareMessage(List<IMMessage> shareMsgs) {

    }

    @Override
    public void sendSyncConversation() {

    }

    @Override
    public void sendRobotMsg(String msg) {

    }

    @Override
    public void setMessage(String msg) {

    }

    @Override
    public void sendVoiceMessage(String voicePath, int duration) {
        File file = new File(voicePath);
        String fileName = UUID.randomUUID().toString() + ".aar";
        final File targetFile = MyDiskCache.getVoiceFile(fileName);
        FileUtils.copy(file, targetFile);

        final TransitSoundJSON json = new TransitSoundJSON("", fileName, duration, voicePath);
        final IMMessage message = generateIMMessage();
        message.setBody(JsonUtils.getGson().toJson(json));
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);

        LogUtil.d("voice", voicePath);
//        final BodyExtension bodyExtension = new BodyExtension();
//        if (snapStatus) {
//            handleSnapMessage(message, bodyExtension);
//        }
        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;
//        chatView.isFromChatRoom();
        //上传语音文件并发送
        HttpUtil.uploadAndSendVoice(message, targetFile.getAbsolutePath(), json, chatView.isFromChatRoom());
        IMDatabaseManager.getInstance().InsertPublicNumberMessage(message);
    }

    @Override
    public void reloadMessages() {
        if (publishPlatform == null) {
            return;
        }
        List<PublishPlatformNews> list = publishPlatformNewsDataModel.getMsgWithLimit(QtalkStringUtils.userId2Jid(robotChatView.getRobotId()), numPerPage, curMsgNum);
        List<IMMessage> imMessages = new ArrayList<IMMessage>(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            PublishPlatformNews publishPlatformNews = list.get(i);
            IMMessage message = new IMMessage();
            message.setId(publishPlatformNews.id);
            message.setMessageID(publishPlatformNews.id);
            message.setMsgType(publishPlatformNews.msgType);
            if (publishPlatformNews.direction == IMMessage.DIRECTION_SEND) {
                message.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
                message.setToID(publishPlatformNews.platformXmppId);
            } else {
                message.setFromID(publishPlatformNews.platformXmppId);
                message.setToID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
            }
            message.setType(ConversitionType.MSG_TYPE_SUBSCRIPT);
            message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
            message.setMessageState(publishPlatformNews.state);
            message.setConversationID(publishPlatformNews.platformXmppId);
            message.setExt(publishPlatformNews.extentionFlag);
            message.setBody(publishPlatformNews.content);
            message.setDirection(publishPlatformNews.direction);
            message.setTime(new Date(publishPlatformNews.latestUpdateTime));
            imMessages.add(message);
        }
        curMsgNum = imMessages.size();
        chatView.setHistoryMessage(imMessages, 0);
    }

    @Override
    public void reloadMessagesFromTime(long time) {

    }

    @Override
    public void rushOrder(String dealId, final IMMessage message) {
        if (message.getMsgType() != ProtoMessageOuterClass.MessageType.MessageTypeConsult_VALUE) return;
        final PublishPlatformNews publishPlatformNews = new PublishPlatformNews();
        publishPlatformNews.content = message.getBody();
        publishPlatformNews.direction = message.getDirection();
        publishPlatformNews.latestUpdateTime = message.getTime().getTime();
        publishPlatformNews.extentionFlag = message.getExt();
        publishPlatformNews.platformXmppId = message.getConversationID();
        publishPlatformNews.id = message.getId();
        publishPlatformNews.msgType = message.getMsgType();
        publishPlatformNews.readTag = IMMessage.MSG_READ;
        publishPlatformNews.state = MessageStatus.REMOTE_STATUS_CHAT_SUCCESS;
        rushQueue.put(dealId, message);
        final ThirdRequestMsgJson requestMsgJson = JsonUtils.getGson().fromJson(message.getExt(), ThirdRequestMsgJson.class);
        String rushUrl = requestMsgJson.dealurl.contains("?") ?
                requestMsgJson.dealurl + "&qchat_id=" + QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()) :
                requestMsgJson.dealurl + "?qchat_id=" + QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid());
        HttpUrlConnectionHandler.executeGet(rushUrl, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                requestMsgJson.status = 1;
                publishPlatformNews.extentionFlag = JsonUtils.getGson().toJson(requestMsgJson);
                message.setExt(publishPlatformNews.extentionFlag);
                publishPlatformNewsDataModel.insertOrUpdateNews(publishPlatformNews);
                chatView.refreshDataset();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public void updateRushResult(ThirdResponseMsgJson msgJson) {

        IMMessage message = rushQueue.remove(msgJson.dealid);
        if (message != null && msgJson.result.equals("1")) {
            String key = QtalkStringUtils.userId2Jid(msgJson.sessionid);
            String nick = TextUtils.isEmpty(msgJson.nickname) ? "用户" + UUID.randomUUID().toString().substring(0, 6) :
                    msgJson.nickname;
            InternDatas.saveName(key, nick);
            UserVCard userVCard = new UserVCard();
            userVCard.nickname = nick;
            userVCard.id = key;
            userVCard.type = UserVCard.WECHAT_TYPE;

            PublishPlatformNews publishPlatformNews = new PublishPlatformNews();
            publishPlatformNews.content = message.getBody();
            publishPlatformNews.direction = message.getDirection();
            publishPlatformNews.latestUpdateTime = message.getTime().getTime();
            publishPlatformNews.extentionFlag = message.getExt();
            publishPlatformNews.platformXmppId = message.getConversationID();
            publishPlatformNews.id = message.getId();
            publishPlatformNews.msgType = message.getMsgType();
            publishPlatformNews.readTag = IMMessage.MSG_READ;
            publishPlatformNews.state = MessageStatus.REMOTE_STATUS_CHAT_SUCCESS;
            ThirdRequestMsgJson requestMsgJson = JsonUtils.getGson().fromJson(message.getExt(), ThirdRequestMsgJson.class);
            requestMsgJson.status = 2;
            publishPlatformNews.extentionFlag = JsonUtils.getGson().toJson(requestMsgJson);
            publishPlatformNewsDataModel.insertOrUpdateNews(publishPlatformNews);
        }
    }

    @Override
    public void clearRushQueue() {
        rushQueue.clear();
    }

    @Override
    public void initChanelId(String sessionId, String dealId) {

    }

    @Override
    public void saveConvMap(String sessionId) {
        if (chatView != null) {

        }
    }

    @Override
    public void checkAlipayAccount() {

    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION:
                connectionUtil.setSingleRead((IMMessage) args[0], MessageStatus.STATUS_SINGLE_READED + "");
                IMMessage imMessage = (IMMessage) args[0];
                chatView.setNewMsg2DialogueRegion(imMessage);
                break;
        }

    }
}
