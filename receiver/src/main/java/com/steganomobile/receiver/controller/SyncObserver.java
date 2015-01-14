package com.steganomobile.receiver.controller;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImplReceiver;
import com.steganomobile.receiver.controller.cc.CcReceiver;

public class SyncObserver extends ContentObserver implements Sync {

    private static final String TAG = SyncObserver.class.getSimpleName();
    private CcImplReceiver cc;

    public SyncObserver(Handler handler, CcReceiver cc) {
        super(handler);
        this.cc = (CcImplReceiver) cc;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        if (uri.getPath().contains(Const.SYNC_OBSERVER.getPath())) {
            String data = uri.getLastPathSegment();
            if (data != null) {
                cc.getCollector().setSentElement(Byte.parseByte(data));
            }
            cc.onReceive(data);
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
