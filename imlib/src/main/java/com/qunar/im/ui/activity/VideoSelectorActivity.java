package com.qunar.im.ui.activity;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.VideoAdapter;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.ArrayList;

/**
 * 选择视频
 */
public class VideoSelectorActivity extends IMBaseActivity implements VideoAdapter.GetCheckedItemNumber {
    private static final String TAG = "VideoSelectorActivity";
    private ListView lv_videos;
    private VideoAdapter mVideoAdapter;
    private int mSelectedNumber = -1;
    private ArrayList<VideoInfo> videos;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mVideoAdapter = new VideoAdapter(VideoSelectorActivity.this, videos);
            lv_videos.setAdapter(mVideoAdapter);
            lv_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedNumber = position;
                    mVideoAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_actvity_select_video);
        bindViews();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void bindViews() {
        lv_videos = (ListView) this.findViewById(R.id.lv_videos);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_select_video);
        setActionBarRightIcon(R.string.atom_ui_new_send);
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedNumber == -1) {
                    Toast.makeText(VideoSelectorActivity.this, getString(R.string.atom_ui_tip_select_video), Toast.LENGTH_SHORT).show();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("filepath", videos.get(mSelectedNumber).path);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

    }

    public void initData() {
        final String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};

        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION,MediaStore.Video.Media.DATE_ADDED};
        CursorLoader loader = new CursorLoader(VideoSelectorActivity.this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                null, // Return all rows
                null, MediaStore.Video.Media.DATE_ADDED + " DESC");
        videos = new ArrayList();
        final Cursor mCursor = loader.loadInBackground();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mCursor.moveToNext()) {
                    String id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String size = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String duration = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                    LogUtil.d(TAG, path);
                    VideoInfo info = new VideoInfo();

                    Cursor thumbCursor = VideoSelectorActivity.this.getContentResolver().query(
                            MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                            thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                    + "=" + id, null, null);
                    if (thumbCursor != null && thumbCursor.moveToFirst()) {
                        info.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    }


                    info.path = path;
                    if(!TextUtils.isEmpty(size)){
                        info.size = size;
                    }
                    if(!TextUtils.isEmpty(duration)){
                        info.duration = duration;
                    }
                    videos.add(info);
                }
                mCursor.close();
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public int getNumber() {
        return mSelectedNumber;
    }

    public class VideoInfo {
        public String path;
        public String size = "0";
        public String name;
        public String duration = "0";
        public String thumbPath;
    }


}