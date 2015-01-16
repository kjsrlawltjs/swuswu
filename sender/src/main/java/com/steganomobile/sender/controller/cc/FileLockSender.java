package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

import java.io.IOException;

public class FileLockSender extends FileImplSender {
    private static final String TAG = FileLockSender.class.getSimpleName();
    private java.nio.channels.FileLock fileLock;

    @Override
    public void onSend(Context context, int element) {

        super.onSend(context, element);

        if (element == Const.UP_NUMBER) {
            try {
                fileLock = getFileChannel().tryLock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRestart() {
        try {
            if (fileLock != null) {
                fileLock.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
