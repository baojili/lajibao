package com.ln.base.tool;

import android.content.Context;


public class RawMediaPlayer implements android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnErrorListener {
    private Context context;
    private android.media.MediaPlayer player;

    public RawMediaPlayer(Context context) {
        this.context = context;
    }

    public void onCompletion(android.media.MediaPlayer mp) {
        //音频文件播放完成时自动调用

    }

    public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
        //音频文件播放发生错误时自动调用
        return false;
    }

    /**
     * 初始化
     *
     * @param resid 媒体资源id
     */
    public void init(int resid) {
        if (player != null) {
            player.release();
            player = null;
        }
        player = android.media.MediaPlayer.create(context, resid);
        player.setOnCompletionListener(this);
    }

    /**
     * 播放媒体文件
     *
     * @param looping 是否循环播放
     */
    public void playSound(boolean looping) {
        try {
            if (player == null) return;
            //设置是否循环
            player.setLooping(looping);
            player.start();
        } catch (IllegalStateException e) {
            Log.e(e.getMessage(), e);
        }
    }

    public android.media.MediaPlayer getPlayer() {
        return this.player;
    }

    /**
     * 停止播放
     */
    public void stopSound() {
        if (player == null) {
            return;
        }
        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (IllegalStateException e) {
            Log.e(e.getMessage(), e);
        }
    }

    /**
     * 暂停播放
     */
    public void pauseSound() {
        if (player == null) {
            return;
        }
        try {
            if (player.isPlaying()) {
                player.pause();
            }

        } catch (IllegalStateException e) {
            Log.e(e.getMessage(), e);
        }
    }

    public void seekTo(int msec) {
        if (player == null) {
            return;
        }
        try {
            player.seekTo(msec);

        } catch (IllegalStateException e) {
            Log.e(e.getMessage(), e);
        }
    }

    public void release() {
        if (player == null) return;
        player.release();
        player = null;
    }


}

