如何自定义消息气泡（各客户端需保持一致）
---
 ```step 1
  1.各客户端同步定义消息气泡MsgType、ExtendInfo值
  例如，卡片消息MsgType值为 666，ExtendInfo值为自定消息气泡样式以及事件处理相关数据，多数情况可定义为一个JSON串 
  {
  	"title": "xxx和xxx的聊天记录",
  	"desc": "",
  	"linkurl": "https:\/\/qim.qunar.com\/xxxxx"
  }
  ```
 ```step 2
  2.在com.qunar.im.ui.view.baseView.processor包下创建自定义消息processor并继承DefaultMessageProcessor
  例如，卡片消息ExtendMsgProcessor，将创建的Processor添加到ProcessorFactory的processorMap中，key为定义消息的MsgType（processorMap中的Processor会显示在会话的左右侧，middleType中的Processor显示在会话页面的中间）
  
  ```
 ```step 3
  3.在创建的processor中实现processChatView方法
  在这里你可以自定义下消息气泡的View，可以代码创建view或者通过引入layout布局，之后将ExtendInfo里面的数据渲染到气泡中，并添加气泡点击等事件处理，最后把创建的view add到方法的第一个参数ViewGroup中
  
  ```
 ```step 4
  4.发送自定义消息
  只需在ChatPresenter中定义一个方法,将IMMessage的MsgTyoe、Ext赋值
  例如发送视频文件消息：
  public void sendVideo(VideoMessageResult videoMessageResult) {//videoMessageResult为ExtendInfo值
          IMMessage message = generateIMMessage();
          message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);//此处改成自己定义的消息值
          String jsonVideo = JsonUtils.getGson().toJson(videoMessageResult);
          message.setBody(jsonVideo);
          message.setExt(jsonVideo);
          HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
          if (isFromChatRoom) {
              ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
          } else {
              ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
          }
      }
  ```  