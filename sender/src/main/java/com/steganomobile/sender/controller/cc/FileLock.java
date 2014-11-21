package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

import java.io.IOException;

public class FileLock extends FileImpl {
    private static final String TAG = FileLock.class.getSimpleName();
    private java.nio.channels.FileLock fileLock;

    @Override
    public void sendCc(Context context, int element) {

        super.sendCc(context, element);

        if (element == Const.UP_NUMBER) {
            try {
                fileLock = getFileChannel().tryLock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finishCc() {
        try {
            if (fileLock != null) {
                fileLock.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
