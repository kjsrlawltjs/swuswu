package com.steganomobile.receiver.controller.cc;

import android.content.Context;
import android.media.AudioManager;

import com.steganomobile.receiver.controller.DataCollector;

public class VolumeSettingsReceiver extends CcImplReceiver {

    private static final String TAG = VolumeSettingsReceiver.class.getSimpleName();
    private AudioManager audioManager;

    public VolumeSettingsReceiver(Context context, DataCollector collector) {
        super(collector);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onReceive(String action) {
        int stream = getCollector().getInfo().getName().getStream();
        getCollector().setData((byte) audioManager.getStreamVolume(stream));
    }
}