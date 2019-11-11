package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RobotQuestionAdapter;
import com.qunar.im.ui.view.baseView.RobotQuestionListView;
import com.qunar.im.ui.view.baseView.ViewPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RobotQuestionListMessageProcessor extends DefaultMessageProcessor {

    //    private RbtSuggestionListJson data;
    private BottomSheetDialog bottomSheetDialog;
    private AdapterView.OnItemClickListener itemClickListener;

    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
//        super.processChatView(parent, item);
        String ex = item.getMessage().getExt();
        Logger.i("查看消息65536:"+ex);
        final RbtSuggestionListJson suggestionListJson
                = JsonUtils.getGson().fromJson(ex, RbtSuggestionListJson.class);
        IMMessage message = item.getMessage();
//        RobotQuestionListView view = new RobotQuestionListView(item.getContext());
        RobotQuestionListView view = ViewPool.getView(RobotQuestionListView.class, item.getContext());
        if (TextUtils.isEmpty(suggestionListJson.content)) {
            view.setTitleText(suggestionListJson.listTips);
        } else {
            view.setTitleText(suggestionListJson.content);
        }

        view.setDataList(suggestionListJson.listArea.items);
        view.setMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(item.getContext());
            }
        });

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(bottomSheetDialog.isShowing()){
                    bottomSheetDialog.dismiss();
                }
                RbtSuggestionListJson.ItemEvent event = suggestionListJson.listArea.items.get(position).event;
                if (null == event) {
                    return;
                }


                if (!TextUtils.isEmpty(event.msgText))
                    if (item.getContext() instanceof IChatView) {
                        ((IChatView) item.getContext()).sendRobotMsg(event.msgText);
                    }
//                        EventBus.getDefault().post(new QchatConsultEvent.showMsg(event.msgText));

                if (TextUtils.isEmpty(event.url)) {
                    return;
                }

                if ("interface".equalsIgnoreCase(event.type)) {
                    String nameAndValues = Protocol.makeQVTHeader();
                    Map<String, String> cookie = new HashMap<>();
                    cookie.put("Cookie", "cookie=" + nameAndValues);
                    Logger.i("点击问题列表 url：" + event.url);

                    HttpUrlConnectionHandler.executeGet(event.url, cookie, new HttpRequestCallback() {
                        @Override
                        public void onComplete(InputStream response){

                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    return;
                }


            }


        };
        view.setOnItemClickListener(itemClickListener);


        initPopupWindow(item.getContext(),suggestionListJson);
        parent.setVisibility(View.VISIBLE);
        parent.addView(view);
    }

    private void initPopupWindow(Context context,RbtSuggestionListJson rbtSuggestionListJson) {

//        //要在布局中显示的布局
//        View contentView = LayoutInflater.from(context).inflate(R.layout.atom_ui_list_popwindow, null, false);
//        //实例化PopupWindow并设置宽高
//        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        //点击外部消失，这里因为PopupWindow填充了整个窗口，所以这句代码就没用了
//        popupWindow.setOutsideTouchable(true);
//        //设置可以点击
//        popupWindow.setTouchable(true);
//        //进入退出的动画
////        popupWindow.setAnimationStyle(R.style.MyPopWindowAnim);

        bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.atom_ui_list_popwindow, null);
        TextView titleText = view.findViewById(R.id.titleText);
        IconView closeView = view.findViewById(R.id.iconClose);
        final ListView listView = view.findViewById(R.id.question_list);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!listView.canScrollVertically(-1)) {      //canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
                    listView.requestDisallowInterceptTouchEvent(false);
                }else{
                    listView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });


        RobotQuestionAdapter adapter = new RobotQuestionAdapter(rbtSuggestionListJson.listArea.items,context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        if (TextUtils.isEmpty(rbtSuggestionListJson.content)) {
            titleText.setText(rbtSuggestionListJson.listTips);
        } else {
            titleText.setText(rbtSuggestionListJson.content);
        }
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
//        bottomSheetDialog.setCancelable(false);

//        bottomSheetDialog.setCanceledOnTouchOutside(false);
        //给布局设置透明背景色
        bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet)
                .setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
    }

    private void showPopWindow(Context context) {
//        View rootview = LayoutInflater.from(context).inflate(R.layout.atom_ui_activity_chat, null);
//        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        bottomSheetDialog.show();
    }

}
