package com.steganomobile.sender.controller.cc;

import android.content.Context;

import java.io.IOException;

public class FileSizeSender extends FileImplSender {
    private static final String TAG = FileSizeSender.class.getSimpleName();

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);
        try {
            getAccessFile().setLength(element + FileImplSender.BASE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
