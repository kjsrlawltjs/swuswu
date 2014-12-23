package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileSizeReceiver extends FileImplReceiver {
    private static final String TAG = FileSizeReceiver.class.getSimpleName();

    public FileSizeReceiver(DataCollector collector) {
        super(collector);
    }

    @Override
    public void onReceive(String action) {
        try {
            getCollector().setData((byte) (getFileChannel().size() - BASE_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}