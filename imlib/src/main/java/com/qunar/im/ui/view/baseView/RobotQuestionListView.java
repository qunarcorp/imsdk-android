package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RobotQuestionListView extends LinearLayout {
    private ListView questionList;
    private TextView titleText;
    private List<RbtSuggestionListJson.Item> dataList = new ArrayList<>();
    private LinearLayout view_more;
    RobotQuestionAdapter adapter;

    public RobotQuestionListView(Context context) {
        super(context);
        init(context);
    }

    public RobotQuestionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RobotQuestionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

//        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }

    public void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.atom_ui_item_question_list, null);
        questionList = (ListView) view.findViewById(R.id.question_list);
        view_more = view.findViewById(R.id.view_more);
        titleText = view.findViewById(R.id.titleText);
        adapter = new RobotQuestionAdapter(dataList,context);
        questionList.setAdapter(adapter);
        addView(view);
    }

    public void setDataList(List<RbtSuggestionListJson.Item> dataList){
        this.dataList = dataList;
        adapter.changeList(dataList);
    }
    public void setMoreClickListener(View.OnClickListener listener){
        view_more.setOnClickListener(listener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        questionList.setOnItemClickListener(listener);
    }

    public void setTitleText(String text){
        titleText.setText(text);
    }

    public void showMore(boolean show){
        if(show){
            view_more.setVisibility(View.VISIBLE);
        }else{
            view_more.setVisibility(View.GONE);
        }
    }

}
