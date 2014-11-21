package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

public class ContentOfUri extends CcImpl {
    private static final String TAG = ContentOfUri.class.getSimpleName();

    public ContentOfUri(DataCollector collector) {
        super(collector);
    }

    @Override
    public void runCc(String content) {
        getCollector().setData(Byte.parseByte(content));
    }
}