package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.EmotionEntry;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.UnzipUtils;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by saber on 16-1-19.
 */
public class EmoticonAdapter extends CommonAdapter<EmotionEntry> {
    Context context;
    public EmoticonAdapter(Context cxt, List<EmotionEntry> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
        context = cxt;
    }

    public void changeDatas(List<EmotionEntry> list)
    {
        super.mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public void convert(CommonViewHolder viewHolder, final EmotionEntry entry) {
        final ImageView downLoadView = viewHolder.getView(R.id.imgDownload);
        final SimpleDraweeView icon = viewHolder.getView(R.id.imgView);
        final ProgressBar progressBar = viewHolder.getView(R.id.progress_bar);
        final TextView textView = viewHolder.getView(R.id.emot_exist);
        final TextView textDesc = viewHolder.getView(R.id.imgDesc);
        final TextView textName = viewHolder.getView(R.id.imgText);
        textDesc.setText(entry.desc);
        textName.setText(entry.name+"("+FileUtils.getFormatSizeStr(entry.file_size)+")");
        FacebookImageUtil.loadWithCache(entry.thumb,icon,true);
        if(!entry.exist) {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            downLoadView.setVisibility(View.VISIBLE);
            downLoadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DownloadRequest request = new DownloadRequest();
                    final String savePath = FileUtils.getExternalFilesDir(context).getPath()+"/" + entry.name + ".zip";
                    request.savePath=savePath;
                    request.url = entry.file;
                    request.requestComplete = new IDownloadRequestComplete() {
                        @Override
                        public void onRequestComplete(DownloadImageResult result) {
                            if (result != null && result.isDownloadComplete()) {
                                File zipFile = new File(savePath);
//                                File dir = new File(context.getFilesDir(), Constants.SYS.EMOTICON_DIR);
                                File dir = EmotionUtils.getExtEmoticonFileDir();
                                File imgDir = UnzipUtils.unZipFile(dir, zipFile, true);

                                EmotionUtils.loadSpecialExtEmot(context, entry.name,entry.pkgid,imgDir);
                                EventBus.getDefault().post(new EventBusEvent.DownEmojComplete(entry.pkgid,
                                        entry.name));
                            }
                            QunarIMApp.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    downLoadView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    textView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    };

                    progressBar.setVisibility(View.VISIBLE);
                    downLoadView.setVisibility(View.GONE);
                    CommonDownloader.getInsatnce().setDownloadRequest(request);
                }
            });
        }
        else {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmotionUtils.removeEmotions(context, entry.name,entry.pkgid);
                    EventBus.getDefault().post(new EventBusEvent.DownEmojComplete(entry.pkgid,
                            entry.name));
                    QunarIMApp.mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            downLoadView.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                        }
                    });
                }
            });
            downLoadView.setVisibility(View.GONE);
        }
    }
}
