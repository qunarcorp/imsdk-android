package com.qunar.im.ui.view.medias.record;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.common.AudioRecordFunc;
import com.qunar.im.ui.R;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;

import java.io.File;

/**
 * Created by saber on 15-11-19.
 */
public class RecordView extends LinearLayout implements VoiceView.OnRecordListener {
    private final String TAG = "RecordView";
    private VoiceView mVoiceView;
    private Handler mHandler;
    private boolean mIsRecording;
    private AudioRecordFunc audioRecordFunc;
    private IRecordCallBack callback;
    private long recordStartTime;
    private File outputFile;
    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_recorder, this, true);
        mVoiceView = this.findViewById(R.id.record_btn);
    }

    public void setStatusView(TextView view,Handler h)
    {
        mHandler = h;
        mVoiceView.setStatusView(view, h);
    }

    public void initView(String filepath)
    {
        LogUtil.d(TAG, "onRecordStart");
        try {
            audioRecordFunc = AudioRecordFunc.getInstance();
//            mMediaRecorder = new MediaRecorder();
            outputFile = new File(filepath);
            mVoiceView.setOnRecordListener(this);
        } catch (Exception e) {
            LogUtil.e(TAG,"ERROR",e);
        }
    }

    @Override
    public void onRecordStart() {
        mIsRecording = true;
        try {
            if(callback!=null)
            {
                callback.recordStart();
            }
            if(outputFile.exists()) outputFile.delete();
            recordStartTime=System.currentTimeMillis();
            audioRecordFunc.startRecordAndFile(outputFile.getPath());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    float radius = (float) Math.log10(Math.max(1, audioRecordFunc.getMaxAmplitude() - 500)) * Utils.dipToPixels(QunarIMApp.getContext(), 20);
                    mVoiceView.animateRadius(radius);
                    if (mIsRecording) {
                        mHandler.postDelayed(this, 50);
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG,"ERROR",e);
        }

    }

    @Override
    public void onRecordFinish() {
        mIsRecording = false;
        try {
            audioRecordFunc.stopRecordAndFile();
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
        if(callback!=null)
            callback.recordFinish(System.currentTimeMillis() - recordStartTime);
    }

    @Override
    public void onRecordCancel() {
        mIsRecording = false;
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                audioRecordFunc.cancelRecord();
                if(callback!=null)
                    callback.recordCancel();
            }
        });
//        try {
//            audioRecordFunc.cancelRecord();
//        }catch (Exception e)
//        {
//            LogUtil.e(TAG,"ERROR",e);
//        }
//        if(callback!=null)
//            callback.recordCancel();
    }

    public void destroy()
    {
        if(mIsRecording){
            try {
                audioRecordFunc.stopRecordAndFile();
            }
            catch (Exception e)
            {
                LogUtil.e(TAG,"ERROR",e);
            }

            mIsRecording = false;
        }
        try {
//            mMediaRecorder.release();
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
        finally {
            mVoiceView.destroy();
        }
    }

    public void stop()
    {
        mIsRecording = false;
        audioRecordFunc.stopRecordAndFile();
        mVoiceView.stop();
    }

    public void setCallBack(IRecordCallBack callback)
    {
         this.callback = callback;
    }

    public interface IRecordCallBack{
        void recordStart();
        void recordFinish(long duration);
        void recordCancel();
    }
}
