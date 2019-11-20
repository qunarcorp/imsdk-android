package com.qunar.im.common;

import android.media.AmrInputStream;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import io.kvh.media.amr.AmrEncoder;
import io.kvh.media.amr.Pcm2Wav;

/**
 * Created by huayu.chen on 2016/6/16.
 */
public class AudioRecordFunc {
    private final static String PCM_FILE_PATH = "PCM.tmp";
    private final static String WAV_FILE_PATH = "WAV.tmp";
    //采用频率
    public final static int AUDIO_SAMPLE_RATE = 8000; //44.1KHz,普遍使用的频率
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    private AudioRecord audioRecord;
    //private boolean isRecord = false;// 设置正在录制的状态

    private volatile static AudioRecordFunc mInstance;

    private int maxAmplitude = 0;

    File rawFile;
    File wavFile;
    File amrFile;

    final private static byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};

    private AudioRecordFunc() {

    }


    public synchronized static AudioRecordFunc getInstance() {
        if (mInstance == null) {
            mInstance = new AudioRecordFunc();
        }
        return mInstance;
    }

    public int getMaxAmplitude()
    {
        return maxAmplitude;
    }


    public void prepareFile(String filePath) {
        amrFile = new File(filePath);
        rawFile = new File(CommonConfig.globalContext.getCacheDir(),PCM_FILE_PATH);
        wavFile = new File(CommonConfig.globalContext.getCacheDir(),WAV_FILE_PATH);
        if(rawFile.exists()) rawFile.delete();
        if(wavFile.exists()) wavFile.delete();
        try {
            amrFile.createNewFile();
            rawFile.createNewFile();
            wavFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecordAndFile(String filePath) {
        prepareFile(filePath);
        creatAudioRecord();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setNoiseSuppress(audioRecord.getAudioSessionId());
        }
        audioRecord.startRecording();
        // 开启音频文件写入线程
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                writeDateTOFile();
            }
        });
    }

    private void setNoiseSuppress(int sessionId) {
        NoiseSuppressorHelper.setAcousticEchoCanceler(sessionId);
        NoiseSuppressorHelper.setAutomaticGainControl(sessionId);
        NoiseSuppressorHelper.setNoiseSuppressor(sessionId);
    }

    public void cancelRecord()
    {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();//释放资源
            audioRecord = null;
        }
        rawFile.delete();
        wavFile.delete();
        amrFile.delete();
    }

    public void stopRecordAndFile() {
        if (audioRecord != null) {
            LogUtil.d("stopRecord");
            audioRecord.stop();
            audioRecord.release();//释放资源
            OutputStream out = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){//7.0及以上
                    pcm2wal();
                    convertAMR(wavFile,amrFile);
                }else {
                    out = new BufferedOutputStream(new FileOutputStream(amrFile, true));
                    out.write(0x23);
                    out.write(0x21);
                    out.write(0x41);
                    out.write(0x4D);
                    out.write(0x52);
                    out.write(0x0A);
                    pcm2amr(out,rawFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(out!=null) try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rawFile.delete();
            }
            audioRecord = null;
        }
    }


    private void creatAudioRecord() {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if(bufferSizeInBytes% AmrInputStream.SAMPLES_PER_FRAME>0) bufferSizeInBytes = (bufferSizeInBytes/ AmrInputStream.SAMPLES_PER_FRAME+1)* AmrInputStream.SAMPLES_PER_FRAME;
        // 创建AudioRecord对象
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes * 2);
    }

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */

    private void writeDateTOFile() {
        // new一个short数组用来存一些字节数据，大小为缓冲区大小
        short[] audiodata = new short[bufferSizeInBytes];
        int readsize = 0;
        while (AudioRecord.RECORDSTATE_RECORDING == audioRecord.getRecordingState()) {
            readsize =audioRecord.read(audiodata, readsize, bufferSizeInBytes);
            if(readsize>0){
                if(readsize>2) {
                    int ab = (audiodata[0]&0xff)<<8|audiodata[1];
                    maxAmplitude = Math.abs(ab);
                }
                OutputStream outputStream = null;
                try {
                    outputStream =  new BufferedOutputStream(new FileOutputStream(rawFile,true));
                    outputStream.write(short2ByteArray(audiodata));
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if(outputStream!=null)
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }

            }
            if(readsize < 0) readsize = 0;
        }
    }

    public byte[] short2ByteArray(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            byte high = (byte) (shorts[i] >> 8);
            byte low = (byte) shorts[i];
            bytes[i * 2] = low;
            bytes[i * 2 + 1] = high;
        }
        return bytes;
    }

    private void pcm2wal(){
        Pcm2Wav tool = new Pcm2Wav();
        try {
            tool.convertAudioFiles(rawFile,wavFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pcm2amr(OutputStream out,String wavFileName) throws IOException {
        InputStream inStream  = new FileInputStream(wavFileName);//这个是SDcard的wav文件路径
        AmrInputStream aStream = new AmrInputStream(inStream);
        try {
            byte[] x = new byte[1024];
            int len=0;
            while ((len = aStream.read(x)) > 0) {
                out.write(x, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            aStream.close();
            out.close();
        }
    }

    private void convertAMR(File inFile,File outFile){
        try {
            AmrEncoder.init(0);
            List<short[]> armsList = new ArrayList<>();
            FileInputStream inputStream = new FileInputStream(inFile);
            FileOutputStream outStream = new FileOutputStream(outFile);
            //写入Amr头文件
            outStream.write(header);

            int byteSize = 320;
            byte[] buff = new byte[byteSize];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, byteSize)) > 0) {
                short[] shortTemp = new short[160];
                //将byte[]转换成short[]
                ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortTemp);
                //将short[]转换成byte[]
//				ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortTemp);
                armsList.add(shortTemp);
            }

            for (int i = 0; i < armsList.size(); i++) {
                int size = armsList.get(i).length;
                byte[] encodedData = new byte[size*2];
                int len = AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(), armsList.get(i), encodedData);
                if (len>0) {
                    byte[] tempBuf = new byte[len];
                    System.arraycopy(encodedData, 0, tempBuf, 0, len);
                    outStream.write(tempBuf, 0, len);
                }
            }
//            AmrEncoder.reset();
            AmrEncoder.exit();

            outStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
