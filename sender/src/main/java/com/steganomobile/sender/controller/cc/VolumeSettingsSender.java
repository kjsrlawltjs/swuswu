package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.media.AudioManager;

public class VolumeSettingsSender extends CcImplSender {
    private static final String TAG = VolumeSettingsSender.class.getSimpleName();
    private int stream;
    private AudioManager audioManager;

    public VolumeSettingsSender(Context context, int stream) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.stream = stream;
    }

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);
        audioManager.setStreamVolume(stream, element, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
