package com.steganomobile.sender.controller.cc;

import android.content.Context;

import com.steganomobile.common.Const;

import java.io.IOException;

public class FileExistence extends FileImpl {
    private static final String TAG = FileExistence.class.getSimpleName();

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);

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
