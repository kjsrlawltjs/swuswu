package com.steganomobile.receiver.controller;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImpl;

public class SyncObserver extends ContentObserver implements Sync {

    private static final String TAG = SyncObserver.class.getSimpleName();
    private CcImpl cc;

    public SyncObserver(Handler handler, CcImpl cc) {
        super(handler);
        this.cc = cc;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        if (uri.getPath().contains(Const.SYNC_OBSERVER.getPath())) {
            cc.runCc(uri.getLastPathSegment());
        }
    }

    public CcImpl getCc() {
        return cc;
    }

    public void finish(Context context) {
        cc.clearCc();
        cc.getCollector().finish(context);
    }
}
