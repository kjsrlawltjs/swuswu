package com.steganomobile.receiver.controller;

import android.content.Context;
import android.os.Handler;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.cc.CcImplReceiver;
import com.steganomobile.receiver.controller.cc.CcReceiver;

public class SyncHandler implements Sync {

    private static final String TAG = SyncHandler.class.getSimpleName();
    private CcImplReceiver cc;
    final Runnable callback = new Runnable() {
        public void run() {
            int interval = cc.getCollector().getInfo().getInterval();
            handler.postDelayed(this, interval);
            if (cc == null) {
                handler.removeCallbacks(this);
            }
            cc.onReceive(Const.NO_ACTION);
        }
    };
    private Handler handler = new Handler();

    public SyncHandler(CcReceiver cc) {
        this.cc = (CcImplReceiver) cc;

        synchronized (this) {
            try {
                wait(Const.SYNC_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        handler.post(callback);
    }

    public CcImplReceiver getCc() {
        return cc;
    }

    public void onFinish(Context context) {
        cc.onFinish();
        handler.removeCallbacks(callback);
        cc.getCollector().finish(context);
    }
}
