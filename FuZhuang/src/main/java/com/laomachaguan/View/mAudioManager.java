package com.laomachaguan.View;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Administrator on 2016/11/7.
 */
public class mAudioManager {
    private static final String TAG = "mAudioManager";
    public static MediaPlayer mMediaplayer;
    private static boolean isPause;
    private static mAudioView audioView;
    public  static mAudioView getAudioView(){
        return  audioView;
    }

    public static void playSound(mAudioView view, String filePath, MediaPlayer.OnCompletionListener onCompletionListener, MediaPlayer.OnPreparedListener
            onPreparedListener) {
        audioView = view;
//        view.setTag(true);
        if (mMediaplayer == null) {
            mMediaplayer = new MediaPlayer();
            mMediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaplayer.reset();
                    return false;
                }
            });
        } else {
            mMediaplayer.reset();
        }
        try {
            mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaplayer.setDataSource(filePath);
            mMediaplayer.prepare();
            mMediaplayer.setOnCompletionListener(onCompletionListener);
            mMediaplayer.setOnPreparedListener(onPreparedListener);
            mMediaplayer.start();
            view.beginAnim();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void pause() {
        if (mMediaplayer != null && mMediaplayer.isPlaying()) {
            mMediaplayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaplayer != null && isPause) {
            mMediaplayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaplayer != null) {
            mMediaplayer.release();
            if(audioView!=null){
                audioView.setPlaying(false);
                audioView.resetAnim();
            }
            mMediaplayer = null;
        }
        if (audioView != null) {
            Log.w(TAG, "release: 关闭动画" );
            audioView.resetAnim();
        }
    }


}
