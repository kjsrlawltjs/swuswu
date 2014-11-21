package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileExistence extends FileImpl {
    private static final String TAG = FileExistence.class.getSimpleName();

    public FileExistence(DataCollector collector) {
        super(collector);
    }

    @Override
    public void runCc(String action) {
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