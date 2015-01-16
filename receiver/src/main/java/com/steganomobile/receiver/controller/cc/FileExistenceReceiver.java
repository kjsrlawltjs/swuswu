package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileExistenceReceiver extends FileImplReceiver {
    private static final String TAG = FileExistenceReceiver.class.getSimpleName();

    public FileExistenceReceiver(DataCollector collector) {
        super(collector);
    }

    @Override
    public void onReceive(String action) {
        boolean result = getFile().delete();
        if (result) {
            getCollector().setData(Const.UP_NUMBER);
            try {
                result = getFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getCollector().setData(Const.DOWN_NUMBER);
        }
    }
}