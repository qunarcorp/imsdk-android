package com.qunar.im.ui.view.baseView.processor;

import com.qunar.im.base.structs.MessageType;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhaokai on 15-8-14.
 */
public class ProcessorFactory {
    public static final int DEFAULT_PROCESSOR = 0;
    private static final Map<Integer, MessageProcessor> processorMap = new HashMap<>();
    private static final Set<Integer> middleType = new HashSet<>();

    static {
        TextMessageProcessor textProccesser = new TextMessageProcessor();
        HongbaoMessageProcessor hongbao = new HongbaoMessageProcessor();
        AAShoukMessageProcessor aaShoukMessageProcessor = new AAShoukMessageProcessor();
        HongbaoPromptProcessor hongbaoPromptProcessor = new HongbaoPromptProcessor();
        NoteMessageProcessor noteMessageProcessor = new NoteMessageProcessor();
        TransferMessageProcessor transferMessageProcessor = new TransferMessageProcessor();
        ExtendMsgProcessor extendMsgProcessor = new ExtendMsgProcessor();
        RTCProcessor rtcProcessor = new RTCProcessor();
        RobOrderProcessor robOrderProcessor = new RobOrderProcessor();
        processorMap.put(MessageType.EXTEND_MSG,extendMsgProcessor); // 666 for pecial message
//        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE,extendMsgProcessor); // 666 for special message
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE, new ActivityMsgProcessor());
        processorMap.put(ProcessorFactory.DEFAULT_PROCESSOR, textProccesser);
        processorMap.put(MessageType.TEXT_MESSAGE, textProccesser);
        processorMap.put(MessageType.MSG_HISTORY_SPLITER,new MessageSpliterProcessor());
        processorMap.put(MessageType.IMAGE_MESSAGE, textProccesser);
        processorMap.put(MessageType.COMMENT_MESSAGE, textProccesser);
        processorMap.put(MessageType.VOICE_MESSAGE, new VoiceMessageProcessor());
        processorMap.put(MessageType.FILE_MESSAGE, new FileMessageProcessor());
        processorMap.put(MessageType.VIDEO_MESSAGE, new VideoMessageProcessor());
        processorMap.put(MessageType.LOCATION_MESSAGE, new ShareLocationProcessor());
        processorMap.put(MessageType.READ_TO_DESTROY_MESSAGE, new ReadToDestroyProcessor());
        processorMap.put(MessageType.MSG_ACTION_RICH_TEXT, new ActionRichTextProcessor());
        processorMap.put(MessageType.MSG_RICH_TEXT, new RichTextProcessor());
        processorMap.put(MessageType.MSG_TYPE_RBT_NOTICE, new NoticeMessageProcessor());
        processorMap.put(MessageType.MSG_TYPE_RBT_SYSTEM, new SystemMessageProcessor());
        processorMap.put(MessageType.MSG_HONGBAO_MESSAGE,hongbao);
        processorMap.put(MessageType.MSG_HONGBAO_PROMPT,hongbaoPromptProcessor);
        processorMap.put(MessageType.MSG_NOTE,noteMessageProcessor);
        processorMap.put(MessageType.MSG_PRODUCT_CARD,noteMessageProcessor);
        processorMap.put(MessageType.MSG_AA_MESSAGE,aaShoukMessageProcessor);
        processorMap.put(MessageType.MSG_AA_PROMPT,hongbaoPromptProcessor);
        processorMap.put(MessageType.SHARE_LOCATION,new LocationProcessor());
//        processorMap.put(MessageType.TRANSFER_TO_CUSTOMER,transferMessageProcessor);
        processorMap.put(MessageType.TRANSFER_TO_SERVER,transferMessageProcessor);
//        processorMap.put(MessageType.TRANSFER_BACK_CUSTOM,transferMessageProcessor);
//        processorMap.put(MessageType.TRANSFER_BACK_SERVER,transferMessageProcessor);
        processorMap.put(MessageType.MSG_TYPE_RUNSHING_ORDER,new ThirdMessageProcessor());
        processorMap.put(MessageType.MSG_TYPE_ROB_ORDER,robOrderProcessor);
        processorMap.put(MessageType.MSG_TYPE_ROB_ORDER_RESPONSE,robOrderProcessor);
        processorMap.put(MessageType.REVOKE_MESSAGE,new RevokeMeesageProcessor());
        processorMap.put(MessageType.ORDER_INFO_MSG,new OrderCardProcessor());
        processorMap.put(MessageType.EXTEND_OPS_MSG,extendMsgProcessor);
        processorMap.put(MessageType.PREDICTION_MSG,extendMsgProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE,rtcProcessor);
        processorMap.put(MessageType.MSG_TYPE_WEBRTC_AUDIO,rtcProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE,new RbtSuggesstionProccessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE,new RbtToUserProccessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeMeetingRemind_VALUE,new MeetingRemindProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE,new CodeMessageProcessor());
        middleType.add(MessageType.MSG_ACTION);
        middleType.add(MessageType.MSG_ACTION_RICH_TEXT);
        middleType.add(MessageType.MSG_RICH_TEXT);
        middleType.add(MessageType.MSG_TYPE_RBT_NOTICE);
        middleType.add(MessageType.MSG_TYPE_RBT_SYSTEM);
        middleType.add(MessageType.MSG_HONGBAO_PROMPT);
//        middleType.add(MessageType.MSG_PRODUCT_CARD);
        middleType.add(MessageType.TRANSFER_TO_CUSTOMER);
        middleType.add(MessageType.TRANSFER_TO_SERVER);
        middleType.add(MessageType.MSG_TYPE_RUNSHING_ORDER);
        middleType.add(MessageType.MSG_TYPE_ROB_ORDER);
        middleType.add(MessageType.MSG_TYPE_ROB_ORDER_RESPONSE);
        middleType.add(MessageType.REVOKE_MESSAGE);
        middleType.add(MessageType.ORDER_INFO_MSG);
        middleType.add(MessageType.MSG_AA_PROMPT);
        middleType.add(MessageType.MSG_HISTORY_SPLITER);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE);
    }

    public static Map<Integer, MessageProcessor> getProcessorMap() {
        return processorMap;
    }
    public static Set<Integer> getMiddleType(){return middleType;}
}
