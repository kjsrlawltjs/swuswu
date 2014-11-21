package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.net.Uri;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcSync;

public class ContentOfUri extends CcImpl {
    private static final String TAG = ContentOfUri.class.getSimpleName();
    private int element;

    @Override
    public void sendCc(Context context, int element) {
        // Sending is connected with sync in this method
        this.element = element;
    }

    @Override
    public void syncCc(Context context, CcSync sync) {
        // Sending is connected with sync in this method
        context.getContentResolver().notifyChange(Uri.parse(Const.SYNC_OBSERVER + "/" + element), null);
    }
}
