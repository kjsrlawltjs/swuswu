package com.steganomobile.receiver.controller.cc;

import android.content.Context;
import android.media.AudioManager;

import com.steganomobile.receiver.controller.DataCollector;

public class VolumeSettings extends CcImpl {

    private static final String TAG = VolumeSettings.class.getSimpleName();
    private AudioManager audioManager;

    public VolumeSettings(Context context, DataCollector collector) {
        super(collector);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void runCc(String action) {
        int stream = getCollector().getInfo().getName().getStream();
        getCollector().setData((byte) audioManager.getStreamVolume(stream));
    }
}