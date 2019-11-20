package com.qunar.im.utils;

import android.util.Xml;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.common.CurrentPreference;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/**
 * Created by may on 2017/7/19.
 */

public class XmlUtils {


    public static JSONObject parseMessageObject(String xml, String domain, String toId, String from, String to) {
        JSONObject result = new JSONObject();

        try {
            String tt = "";
            String ff = "";
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(xml));
            int event = parser.getEventType();

            String msgText = null, type = null;
            int platform = 0, msgType = 0, maType = 0;
            long msec_times = 0;
            String stime = "";
            boolean isBodyTag = false;

            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.TEXT: {
                        if (isBodyTag) {
                            msgText = parser.getText();
                        }
                    }
                    break;
                    case XmlPullParser.START_TAG: {
                        {
                            String name = parser.getName();
                            int count = parser.getAttributeCount();

                            if (name.equalsIgnoreCase("message")) {
                                for (int index = 0; index < count; ++index) {
                                    String itemName = parser.getAttributeName(index);
                                    if (itemName.equalsIgnoreCase("type")) {
                                        type = parser.getAttributeValue(index);
                                    } else if (itemName.equals("msec_times")) {
                                        msec_times = Long.parseLong(parser.getAttributeValue(index));
                                    } else if (itemName.equals("channelid")) {
                                        result.put(itemName, parser.getAttributeValue(index));
                                    } else if (itemName.equals("qchatid")) {
                                        result.put(itemName, parser.getAttributeValue(index));
                                    } else if (itemName.equals("to")) {
                                        tt = QtalkStringUtils.parseIdAndDomain(parser.getAttributeValue(index));
                                        result.put(itemName, tt);
                                    } else if (itemName.equals("from")) {
                                        ff = QtalkStringUtils.parseIdAndDomain(parser.getAttributeValue(index));
                                        result.put(itemName, ff);
                                    } else {
                                        result.put(itemName, parser.getAttributeValue(index));
                                    }
                                }
                            } else if (name.equalsIgnoreCase("body")) {
                                isBodyTag = true;
                                String msgBody = parser.getText();
                                for (int index = 0; index < count; ++index) {
                                    String itemName = parser.getAttributeName(index);
                                    if (itemName.equals("platformType")) {
//                                        platform = Integer.parseInt(parser.getAttributeValue(index));
                                    } else if (itemName.equals("maType")) {
                                        platform = Integer.parseInt(parser.getAttributeValue(index));
                                    } else if (itemName.equals("msgType")) {
                                        msgType = Integer.parseInt(parser.getAttributeValue(index));
                                    } else if (itemName.equals("id")) {
                                        result.put("MsgId", parser.getAttributeValue(index));
                                    }else if(itemName.equals("extendInfo")){
                                        result.put("extendInfo", parser.getAttributeValue(index));
                                    }
                                }
                                if (StringUtils.isNotEmpty(msgBody)) {
                                    result.put("msgText", msgBody);
                                }
                            } else if (name.equalsIgnoreCase("stime")) {
                                for (int index = 0; index < count; ++index) {
                                    String itemName = parser.getAttributeName(index);
                                    if (itemName.equals("stamp")) {
                                        stime = parser.getAttributeValue(index);
                                    }

                                }
                            }
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG: {
                        String name = parser.getName();
                        if (name.equals("body")) {
                            isBodyTag = false;
                        }

                    }
                    break;
                }
                event = parser.next();
            }

//            String key = String.format("%s@%s", from.equalsIgnoreCase(toId) ? from : to, domain);
            //这个是获取会话列表Id

//            result.put("SessionId", key);
            result.put("messageType", type);  // 这个不是用来插库的
            result.put("From", ff);
            result.put("To", tt);
            result.put("MsgType", msgType);
            result.put("Content", msgText);
            result.put("Platform", platform);
//            int direction = lId.equals(ff) ? 1 : 0;
//            result.put("MsgDirection", direction);
            result.put("MsgDateTime", msec_times - 1);
            result.put("MsgStime", stime);
            //批量插入的数据 消息状态都是1
            result.put("MsgState", "1");
            result.put("MsgRaw", xml);
        } catch (XmlPullParserException e) {
            Logger.e(e, "parseXmlMessage crashed");
        } catch (IOException e) {
            Logger.e(e, "parseXmlMessage crashed");
        } catch (JSONException e) {
            Logger.e(e, "parseXmlMessage crashed");
        }

        return result;
    }

    /**
     * 解析一个xml类型的消息,
     *
     * @param message 参数为xml样式的字符串
     * @return 返回一个jsonObject
     */
    public static JSONObject parseXmlMessage(String message) {
        JSONObject result = new JSONObject();

        try {
            result.put("message", new JSONObject());
            result.put("body", new JSONObject());
            result.put("stime", new JSONObject());

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(message));
            int event = parser.getEventType();

            String lastNodeName = null;
            boolean isBodyTag = false;
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {

                    case XmlPullParser.TEXT: {

                        if (isBodyTag) {
                            String msgText = parser.getText();
//                        if (!msgText.equals("\\n") && !msgText.equals("\n")) {
                            result.getJSONObject(lastNodeName).put("_text", msgText);
                        }

//                        }
//                        if (!msgText.equals("\\n") && !msgText.equals("\n")) {
//                            result.getJSONObject(lastNodeName).put("_text", msgText);
//                        }

                    }

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        lastNodeName = name;
                        int count = parser.getAttributeCount();

                        if (name.equalsIgnoreCase("message")) {
                            for (int index = 0; index < count; ++index) {
                                result.getJSONObject(name).put(parser.getAttributeName(index), parser.getAttributeValue(index));
//                                String itemName = parser.getAttributeName(index);
//                                if (itemName.equalsIgnoreCase("type")) {
//                                    result.getJSONObject("message").put(itemName, parser.getAttributeValue(index));
//                                } else if (itemName.equals("msec_times")) {
//                                    result.put("msec_times", Long.parseLong(parser.getAttributeValue(index)));
//                                } else if (itemName.equals("channelid")) {
//                                    result.put(itemName, parser.getAttributeValue(index));
//                                } else if (itemName.equals("qchatid")) {
//                                    result.put(itemName, parser.getAttributeValue(index));
//                                } else {
//                                    result.put(itemName, parser.getAttributeValue(index));
//                                }
                            }
                        } else if (name.equalsIgnoreCase("body")) {
                            isBodyTag = true;
                            String msgBody = parser.getText();
                            for (int index = 0; index < count; ++index) {
                                result.getJSONObject(name).put(parser.getAttributeName(index), parser.getAttributeValue(index));
//                                String itemName = parser.getAttributeName(index);
//                                if (itemName.equals("platformType")) {
//                                    platform = Integer.parseInt(parser.getAttributeValue(index));
//                                } else if (itemName.equals("msgType")) {
//                                    msgType = Integer.parseInt(parser.getAttributeValue(index));
//                                } else if (itemName.equals("extendInfo")) {
//                                    extendInfo = parser.getAttributeValue(index);
//                                } else if (itemName.equals("id")) {
//                                    msgId = parser.getAttributeValue(index);
//                                }
                            }
//                            if (StringUtils.isNotEmpty(msgBody)) {
//                                result.put("msgText", msgBody);
//                            }
                        } else if (name.equals("stime")) {
                            for (int index = 0; index < count; ++index) {
                                result.getJSONObject(name).put(parser.getAttributeName(index), parser.getAttributeValue(index));
                            }
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG: {
                        String name = parser.getName();
                        if (name.equals("body")) {
                            isBodyTag = false;
                        }
                    }
                    break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            Logger.e(e, "parseXmlMessage crashed");
        } catch (IOException e) {
            Logger.e(e, "parseXmlMessage crashed");
        } catch (JSONException e) {
            Logger.e(e, "parseXmlMessage crashed");
        }

        return result;
    }

    public static IMMessage parseXmlToIMMessage(String xml) {
        JSONObject messageItem = parseXmlMessage(xml);

        try {
            JSONObject body = messageItem.getJSONObject("body");
            JSONObject message = messageItem.getJSONObject("message");
            JSONObject stime = messageItem.getJSONObject("stime");

            IMMessage imMessage = new IMMessage();
            String from = QtalkStringUtils.parseIdAndDomain(message.getString("from"));
            imMessage.setFromID(from);
            imMessage.setToID(message.getString("to"));
            imMessage.setTime(new Date(message.getLong("msec_times")));
            if(message.has("realfrom")){
                imMessage.setRealfrom(message.getString("realfrom"));
            }

            String type =message.getString("type");
            if(type.equals("groupchat")){
                imMessage.setType(7);
                if(CurrentPreference.getInstance().getPreferenceUserId().equals(message.getString("realfrom"))){
                    imMessage.setDirection(1);
                }else{
                    imMessage.setDirection(0);
                }
            }else{
                if(CurrentPreference.getInstance().getPreferenceUserId().equals(from)){
                    imMessage.setDirection(1);
                }else {
                    imMessage.setDirection(0);
                }
                imMessage.setType(6);
            }
            imMessage.setMsgType(body.getInt("msgType"));
            if(body.has("maType")){
                imMessage.setMaType(body.getString("maType"));
            }

            imMessage.setId(body.getString("id"));
            imMessage.setMessageID(body.getString("id"));
            imMessage.setMessageID(body.getString("id"));
            imMessage.setBody(body.getString("_text"));
            imMessage.setReadState(1);


            return  imMessage;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

}
