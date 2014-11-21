package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.media.AudioManager;

public class VolumeSettings extends CcImpl {
    private static final String TAG = VolumeSettings.class.getSimpleName();
    private int stream;
    private AudioManager audioManager;

    public VolumeSettings(Context context, int stream) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.stream = stream;
    }

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);
        audioManager.setStreamVolume(stream, element, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
