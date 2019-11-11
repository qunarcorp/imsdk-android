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
        GroupVideoProcessor groupVideoProcessor = new GroupVideoProcessor();
        RobotQuestionListMessageProcessor questionListMessageProcessor = new RobotQuestionListMessageProcessor();
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE,extendMsgProcessor); // 666 for pecial message
//        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE,extendMsgProcessor); // 666 for special message
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE, new ActivityMsgProcessor());
        processorMap.put(ProcessorFactory.DEFAULT_PROCESSOR, textProccesser);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE, textProccesser);
        processorMap.put(MessageType.MSG_HISTORY_SPLITER,new MessageSpliterProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE, textProccesser);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeReply_VALUE, textProccesser);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE, new VoiceMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE, new FileMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE, new VideoMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE, new ShareLocationProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE, new ReadToDestroyProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeActionRichText_VALUE, new ActionRichTextProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRichText_VALUE, new RichTextProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE, new NoticeMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE, new SystemMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRedPack_VALUE,hongbao);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE,hongbaoPromptProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeNote_VALUE,noteMessageProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeProduct_VALUE,noteMessageProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeAA_VALUE,aaShoukMessageProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeAAInfo_VALUE,hongbaoPromptProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeShareLocation_VALUE,new LocationProcessor());
//        processorMap.put(MessageType.TRANSFER_TO_CUSTOMER,transferMessageProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE,transferMessageProcessor);
//        processorMap.put(MessageType.TRANSFER_BACK_CUSTOM,transferMessageProcessor);
//        processorMap.put(MessageType.TRANSFER_BACK_SERVER,transferMessageProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeConsult_VALUE,new ThirdMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE,robOrderProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE,robOrderProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE,new RevokeMeesageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeConsultRevoke_VALUE,new RevokeMeesageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeCommonProductInfo_VALUE,new OrderCardProcessor());
        processorMap.put(MessageType.EXTEND_OPS_MSG,extendMsgProcessor);
        processorMap.put(MessageType.PREDICTION_MSG,extendMsgProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE,rtcProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_AudioCall_VALUE,rtcProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE,rtcProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE,rtcProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_Group_VALUE,groupVideoProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoMeeting_VALUE,groupVideoProcessor);
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE,new RobotQuestionListMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionListNew_VALUE,new RobotNewQuestionListMessageProcessor());
//        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE,new RbtSuggesstionProccessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE,new RbtToUserProccessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeMeetingRemind_VALUE,new MeetingRemindProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeWorkWorldAtRemind_VALUE,new WorkWorldRemindProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeMedalRemind_VALUE,new MedalRemindProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE,new CodeMessageProcessor());
        processorMap.put(ProtoMessageOuterClass.MessageType.MessageTypeRobotAnswer_VALUE,new BottomButtonMessageProcessor());

        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeTopic_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeActionRichText_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRichText_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE);
//        middleType.add(MessageType.MSG_PRODUCT_CARD);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomer_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeConsult_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeConsultRevoke_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeCommonProductInfo_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeAAInfo_VALUE);
        middleType.add(MessageType.MSG_HISTORY_SPLITER);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE);
        middleType.add(ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE);
//        middleType.add(999);
    }

    public static Map<Integer, MessageProcessor> getProcessorMap() {
        return processorMap;
    }
    public static Set<Integer> getMiddleType(){return middleType;}
}
