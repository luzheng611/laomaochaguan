package com.laomachaguan.TouGao;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.laomachaguan.Utils.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/10/20.
 */
public class recordAudio {
    private static final String TAG = "recordAudio";
    public boolean isRecording = false;
    /**
     * 录音
     */
    public MediaRecorder mRecorder;
    /**
     * 播放
     */
    public MediaPlayer mPlayer;

    private String path = "";
    public static recordAudio myRecord = null;

    // 单例
    private recordAudio() {
        mkMyDir();
    }

    ;

    public static synchronized recordAudio getInstance() {
        if (myRecord == null) {
            myRecord = new recordAudio();
        }
        return myRecord;
    }

    // 在sdcard上创建文件夹
    public void mkMyDir() {
        File dir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + Constants.NAME_LOW + "/voice/");
        path = dir.getAbsolutePath();
        if (!dir.exists())
            dir.mkdirs();
    }

    // 获取文件夹路径
    public String getPath() {
        return path;
    }

    // 开始录音
    public void startRecord(String filePath) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//         设定录音文件大小 ，大概是1s 就是1k的大小
//        mRecorder.setMaxFileSize(10 * 1024);
//         设定录音的最大时长,貌似这个跟上面的数据差距有点大 ，最大得到的数据是17k多的数据
//         mRecorder.setMaxDuration(60 * 1000);
        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mRecorder.start();
        isRecording = true;
    }

    // 停止录音
    public void stopRecord() {
        isRecording = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    // 播放录音
    public void startPlay(String fileName) {
        Log.w(TAG, " startPlay  播放地址"+fileName);
            mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                    mPlayer.reset();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 停止播放
    public void stopPlay() {
        if(mPlayer!=null){
            mPlayer.stop();
//            mPlayer.release();
            mPlayer.reset();
//            mPlayer = null;
        }

    }
}
