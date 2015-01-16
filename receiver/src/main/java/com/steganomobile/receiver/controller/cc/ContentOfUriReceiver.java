package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

public class ContentOfUriReceiver extends CcImplReceiver {
    private static final String TAG = ContentOfUriReceiver.class.getSimpleName();

    public ContentOfUriReceiver(DataCollector collector) {
        super(collector);
    }

    @Override
    public void onReceive(String content) {
        getCollector().setData(Byte.parseByte(content));
    }
}