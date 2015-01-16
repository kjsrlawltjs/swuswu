package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

import java.io.IOException;

public class FileExistenceSender extends FileImplSender {
    private static final String TAG = FileExistenceSender.class.getSimpleName();

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);

        boolean result = getFile().delete();
        if (element == Const.UP_NUMBER) {
            try {
                result = getFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
