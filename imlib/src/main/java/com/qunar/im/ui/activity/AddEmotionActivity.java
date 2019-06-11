package com.qunar.im.ui.activity;
import android.os.Bundle;
import android.widget.ListView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.EmoticonAdapter;
import com.qunar.im.base.jsonbean.EmotionEntry;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.ui.util.emoticon.EmotionDownloader;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaokai on 15-8-6.
 */
public class AddEmotionActivity extends IMBaseActivity {
    private ListView emotionList;
    private EmoticonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_add_emotion);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_add_emotion);
        emotionList = (ListView) findViewById(R.id.emotion_list);
        adapter = new EmoticonAdapter(this,new ArrayList<EmotionEntry>(1),R.layout.atom_ui_item_download_emotion);
        emotionList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EmotionDownloader downloader = new EmotionDownloader();
        downloader.getJSON(new EmotionDownloader.gotEmojiListCallback() {
            @Override
            public void onComplete(final List<EmotionEntry> entryList) {
                List<Map<String, String>> existsEmt = EmotionUtils.getEmotions(AddEmotionActivity.this);
                for (EmotionEntry entry : entryList) {
                    for (int i = 0; i < existsEmt.size(); i++) {
                        if (existsEmt.get(i).get("name").equals(entry.name)) {
                            entry.exist = true;
                            existsEmt.remove(i);
                            break;
                        }
                    }
                }
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                       adapter.changeDatas(entryList);
                    }
                });
            }
        });
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }


}
