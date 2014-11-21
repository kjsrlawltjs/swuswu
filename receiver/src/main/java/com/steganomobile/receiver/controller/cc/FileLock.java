package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;

public class FileLock extends FileImpl {
    private static final String TAG = FileLock.class.getSimpleName();

    public FileLock(DataCollector collector) {
        super(collector);
    }

    @Override
    public void runCc(String action) {
        try {
            getFileChannel().tryLock().release();
            getCollector().setData(Const.DOWN_NUMBER);
        } catch (IOException e) {
            getCollector().setData(Const.UP_NUMBER);
        }
    }
}
