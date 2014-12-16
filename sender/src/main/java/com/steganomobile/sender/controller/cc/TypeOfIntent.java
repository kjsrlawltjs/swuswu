package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.content.Intent;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcSync;

public class TypeOfIntent extends CcImpl {
    private static final String TAG = TypeOfIntent.class.getSimpleName();
    private int element;

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);
        // Sending is connected with sync in this method
        this.element = element;
    }

    @Override
    public void syncCc(Context context, CcSync sync, byte sentElement) {
        // Sending is connected with sync in this method
        Intent intent = new Intent(Const.SYNC_RECEIVER + this.element);
        intent.putExtra(Const.EXTRA_SENT_ELEMENT, sentElement);
        context.sendBroadcast(intent);
    }
}
