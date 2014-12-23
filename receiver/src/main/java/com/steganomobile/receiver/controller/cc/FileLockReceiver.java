package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileLockReceiver extends FileImplReceiver {
    private static final String TAG = FileLockReceiver.class.getSimpleName();

    public FileLockReceiver(DataCollector collector) {
        super(collector);
    }

    @Override
    public void onReceive(String action) {
        try {
            getFileChannel().tryLock().release();
            getCollector().setData(Const.DOWN_NUMBER);
        } catch (IOException e) {
            getCollector().setData(Const.UP_NUMBER);
        }
    }
}
