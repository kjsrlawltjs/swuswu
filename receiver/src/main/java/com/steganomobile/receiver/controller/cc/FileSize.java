package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileSize extends FileImpl {
    private static final String TAG = FileSize.class.getSimpleName();

    public FileSize(DataCollector collector) {
        super(collector);
    }

    @Override
    public void runCc(String action) {
        try {
            getCollector().setData((byte) (getFileChannel().size() - BASE_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}