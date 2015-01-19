package com.steganomobile.sender.controller.cc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcSync;

public abstract class CcImplSender implements CcSender {
    public static final int NO_VALUE = -1;
    private static final String TAG = CcImplSender.class.getSimpleName();

    @Override
    public void onSend(Context context, int element) {
        Log.i(TAG, "Sent " + element);
    }

    @Override
    public void onSync(Context context, CcSync sync, byte element) {
        switch (sync) {
            case NO_VALUE:
                break;
            case CONTENT_OBSERVER:
                context.getContentResolver().notifyChange(
                        Uri.parse(Const.SYNC_OBSERVER + "/" + element), null);
                break;
            case BROADCAST_RECEIVER:
                Intent intent = new Intent(Const.SYNC_RECEIVER);
                intent.putExtra(Const.EXTRA_SENT_ELEMENT, element);
                context.sendBroadcast(intent);
                break;
            case HANDLER:
                break;
        }
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onFinish() {

    }
}
