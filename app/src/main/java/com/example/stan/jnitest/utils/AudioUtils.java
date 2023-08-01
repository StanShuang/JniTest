package com.example.stan.jnitest.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * @Author Stan
 * @Description
 * @Date 2023/2/13 11:42
 */
public class AudioUtils {
    public static void audioCheck(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String state = "";
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                state = "声音模式";
                break;

            case AudioManager.RINGER_MODE_SILENT:
                state = "静音模式";
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                state = "震动模式";
                break;
        }
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        Toast.makeText(context, "Audio 当前模式为:" + state + ";当前音量为:" + currentVolume, Toast.LENGTH_LONG).show();
    }

}
