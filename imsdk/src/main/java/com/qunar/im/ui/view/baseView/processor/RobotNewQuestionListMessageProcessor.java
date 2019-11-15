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
import com.qunar.im.base.jsonbean.RbtNewSuggestionList;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RobotNewQuestionAdapter;
import com.qunar.im.ui.view.baseView.RobotNewQuestionListView;

public class RobotNewQuestionListMessageProcessor extends DefaultMessageProcessor {

//    private BottomSheetDialog bottomSheetDialog;
    private AdapterView.OnItemClickListener itemClickListener;

    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
//        super.processChatView(parent, item);
        String ex = item.getMessage().getExt();
        Logger.i("查看消息65536:" + ex);
        final RbtNewSuggestionList suggestionListJson
                = JsonUtils.getGson().fromJson(ex, RbtNewSuggestionList.class);
        IMMessage message = item.getMessage();
//        RobotQuestionListView view = new RobotQuestionListView(item.getContext());
        RobotNewQuestionListView view = new RobotNewQuestionListView(item.getContext());
//        BottomSheetDialog bottomSheetDialog = null;
        BottomSheetDialog  bottomSheetDialog = new BottomSheetDialog(item.getContext());


        String isClickButton = DataUtils.getInstance(item.getContext()).getPreferences("rbtButtonIsClick" + message.getMessageId(), "");
        view.setMessage(message);
        view.setAnswer(suggestionListJson.getContent());
        view.setButton(suggestionListJson.getBottom(), suggestionListJson.getBottom_tips(),isClickButton);
        view.setQuestionList(suggestionListJson.getListArea());
        view.setQuestionListTips(suggestionListJson.getListTips());
        view.setMoreClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
//                showPopWindow(item.getContext());
            }
        });
//        if (TextUtils.isEmpty(suggestionListJson.content)) {
//            view.setTitleText(suggestionListJson.listTips);
//        } else {
//            view.setTitleText(suggestionListJson.content);
//        }

//        view.setDataList(suggestionListJson.listArea.items);
//        view.setMoreClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopWindow(item.getContext());
//            }
//        });

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
                RbtNewSuggestionList.ListAreaBean.ItemsBean.EventBean event = suggestionListJson.getListArea().getItems().get(position).getEvent();
                if (null == event) {
                    return;
                }

                switch (event.getType()) {

                    case "interface":
                        sendMessage(event, item);
                        break;
                    case "text":
                        sendMessage(event, item);
                        break;

                    default:

                        break;
                }



//                        EventBus.getDefault().post(new QchatConsultEvent.showMsg(event.msgText));

//                if (TextUtils.isEmpty(event.url)) {
//                    return;
//                }
//
//                if ("interface".equalsIgnoreCase(event.type)) {
//                    String nameAndValues = Protocol.makeQVTHeader();
//                    Map<String, String> cookie = new HashMap<>();
//                    cookie.put("Cookie", "cookie=" + nameAndValues);
//                    Logger.i("点击问题列表 url：" + event.url);
//
//                    HttpUrlConnectionHandler.executeGet(event.url, cookie, new HttpRequestCallback() {
//                        @Override
//                        public void onComplete(InputStream response) throws IOException {
//
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//
//                        }
//                    });
//                    return;
//                }


            }


        };
        view.setOnItemClickListener(itemClickListener);


//        initPopupWindow(item.getContext(), suggestionListJson);


        View popview = LayoutInflater.from(item.getContext()).inflate(R.layout.atom_ui_list_popwindow, null);
        TextView titleText = popview.findViewById(R.id.titleText);
        IconView closeView = popview.findViewById(R.id.iconClose);
        final ListView listView = popview.findViewById(R.id.question_list);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!listView.canScrollVertically(-1)) {      //canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
                    listView.requestDisallowInterceptTouchEvent(false);
                } else {
                    listView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });


        RobotNewQuestionAdapter adapter = new RobotNewQuestionAdapter(suggestionListJson.getListArea().getItems(), item.getContext(), true);
        adapter.setDefCount(suggestionListJson.getListArea().getItems().size());
        listView.setAdapter(adapter);
        adapter.setDefCount(suggestionListJson.getListArea().getItems().size());
        listView.setOnItemClickListener(itemClickListener);
//        if (TextUtils.isEmpty(rbtSuggestionListJson.content)) {
//            titleText.setText(rbtSuggestionListJson.listTips);
//        } else {
        if(!TextUtils.isEmpty(suggestionListJson.getListTips())){
            titleText.setText(suggestionListJson.getListTips());
        }

//        }
//        BottomSheetDialog finalBottomSheetDialog = bottomSheetDialog;
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(popview);
//        bottomSheetDialog.setCancelable(false);

//        bottomSheetDialog.setCanceledOnTouchOutside(false);
        //给布局设置透明背景色
        bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet)
                .setBackgroundColor(item.getContext().getResources().getColor(android.R.color.transparent));





        parent.setVisibility(View.VISIBLE);
        parent.addView(view);



    }

    private void initPottomSheetDialog(BottomSheetDialog bottomSheetDialog,IMessageItem item, RbtNewSuggestionList suggestionListJson) {
//        BottomSheetDialog bottomSheetDialog;

    }

    private void sendMessage(RbtNewSuggestionList.ListAreaBean.ItemsBean.EventBean event, IMessageItem item) {
        if (!TextUtils.isEmpty(event.getMsgText())) {
            if (item.getContext() instanceof IChatView) {
                ((IChatView) item.getContext()).sendRobotMsg(event.getMsgText());
            }
        }
    }

    private void initPopupWindow(Context context, RbtNewSuggestionList rbtSuggestionListJson) {

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


    }

    private void showPopWindow(Context context) {
//        View rootview = LayoutInflater.from(context).inflate(R.layout.atom_ui_activity_chat, null);
//        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
//        bottomSheetDialog.show();
    }
}
