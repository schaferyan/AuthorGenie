package com.ryanschafer.authorgenie.ui.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {
    //    export to another class and make static
    public static void playSound(Context context, int sound, MediaPlayer.OnCompletionListener listener){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
        mediaPlayer.setOnCompletionListener(listener);
        mediaPlayer.start();
    }

    //    export to another class and make static
    public static void playSound(Context context, int sound){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    public static void playSounds(Context context, int[] sounds){
        Sound.playSound(context, sounds[0], new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (sounds[1] != 0) {
                    Sound.playSound(context, sounds[1]);
                }
            }
        });
    }

}
