package com.steganomobile.receiver.controller;

import android.content.Context;
import android.os.Handler;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImpl;

public class SyncHandler implements Sync {

    private static final String TAG = SyncHandler.class.getSimpleName();
    private CcImpl cc;
    final Runnable callback = new Runnable() {
        public void run() {
            int interval = cc.getCollector().getInfo().getInterval();
            handler.postDelayed(this, interval);
            if (cc == null) {
                handler.removeCallbacks(this);
            }
            cc.runCc(Const.NO_ACTION);
        }
    };
    private Handler handler = new Handler();

    public SyncHandler(CcImpl cc) {
        this.cc = cc;

        synchronized (this) {
            try {
                wait(Const.SYNC_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        handler.post(callback);
    }

    public CcImpl getCc() {
        return cc;
    }

    public void finish(Context context) {
        cc.clearCc();
        handler.removeCallbacks(callback);
        cc.getCollector().finish(context);
    }
}
