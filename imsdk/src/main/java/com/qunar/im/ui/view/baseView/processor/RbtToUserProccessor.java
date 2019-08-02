package com.qunar.im.ui.view.baseView.processor;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.NoLineClickSpan;

/**
 * Created by xinbo.wang on 2016-12-06.
 */
public class RbtToUserProccessor extends DefaultMessageProcessor {

    @Override
    public void processTimeText(TextView timeTextView, final IMessageItem item, ChatViewAdapter adapter) {
        String ex = item.getMessage().getExt();
        if(!TextUtils.isEmpty(ex)&&item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE)
        {
            RbtSuggestionListJson data
                    = JsonUtils.getGson().fromJson(ex,RbtSuggestionListJson.class);
            if (null!=data){

                if (null!=data.hints && data.hints.size()>0){
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    for (RbtSuggestionListJson.Item hintitem : data.hints){
                        if (TextUtils.isEmpty(hintitem.text))
                            continue;

                        if (null == hintitem.event) {
                            sb.append(hintitem.text);
                            continue;
                        }

                        if ("text".equalsIgnoreCase(hintitem.event.type)){
                            sb.append(hintitem.text);
                            continue;
                        }

                        if ("interface".equalsIgnoreCase(hintitem.event.type)){
                            sb.append(hintitem.text);
                            String url = hintitem.event.url;
                            int color = item.getContext().getResources().getColor(R.color.atom_ui_light_green2c);
                            ClickableSpan urlSpan = new NoLineClickSpan(url,
                                    color ,
                                    new NoLineClickSpan.ProcessHyperLinkClick() {
                                        @Override
                                        public void process(String text) {
//                                            if (item.getContext() instanceof QImSessionInfoActivity){
//                                                QImSessionInfoActivity activity = (QImSessionInfoActivity) item.getContext();
//                                                activity.rbtToUser();
//                                            }
                                        }
                                    });
                            sb.setSpan(urlSpan, sb.length()-hintitem.text.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            continue;
                        }

                        sb.append(hintitem.text);
                    }

                    timeTextView.setText(sb);
                    timeTextView.setClickable(true);
                    timeTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    timeTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
