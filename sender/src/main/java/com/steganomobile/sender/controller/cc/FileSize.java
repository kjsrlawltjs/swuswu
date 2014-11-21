package com.steganomobile.sender.controller.cc;

import android.content.Context;

import java.io.IOException;

public class FileSize extends FileImpl {
    private static final String TAG = FileSize.class.getSimpleName();

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);
        try {
            getAccessFile().setLength(element + FileImpl.BASE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
