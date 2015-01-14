package com.steganomobile.receiver.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImplReceiver;
import com.steganomobile.receiver.controller.cc.CcReceiver;

public class SyncReceiver extends BroadcastReceiver implements Sync {

    private static final String TAG = SyncReceiver.class.getSimpleName();
    private CcImplReceiver cc;

    public SyncReceiver(CcReceiver cc) {
        this.cc = (CcImplReceiver) cc;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();

        // Broadcast Receiver Sync
        if (action != null && action.contains(Const.SYNC_RECEIVER)) {
            if (cc != null) {
                cc.getCollector().setSentElement(intent.getByteExtra(Const.EXTRA_SENT_ELEMENT, (byte) 0));
                cc.onReceive(action);
            }
        }
    }

    public CcImplReceiver getCc() {
        return cc;
    }

    public void onFinish(Context context) {
        cc.onFinish();
        cc.getCollector().finish(context);
    }
}
