package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.content.Intent;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcSync;

public class TypeOfIntentSender extends CcImplSender {
    private static final String TAG = TypeOfIntentSender.class.getSimpleName();
    private int element;

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);
        // Sending is connected with sync in this method
        this.element = element;
    }

    @Override
    public void onSync(Context context, CcSync sync, byte sentElement) {
        // Sending is connected with sync in this method
        Intent intent = new Intent(Const.ACTION_TYPE_OF_INTENT + this.element);
        intent.putExtra(Const.EXTRA_SENT_ELEMENT, sentElement);
        context.sendBroadcast(intent);
    }
}
