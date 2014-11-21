package com.steganomobile.receiver.view;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.receiver.R;
import com.steganomobile.receiver.controller.DataCollector;
import com.steganomobile.receiver.controller.SyncHandler;
import com.steganomobile.receiver.controller.SyncObserver;
import com.steganomobile.receiver.controller.SyncReceiver;
import com.steganomobile.receiver.controller.cc.CcImpl;
import com.steganomobile.receiver.controller.cc.ContentOfUri;
import com.steganomobile.receiver.controller.cc.FileExistence;
import com.steganomobile.receiver.controller.cc.FileLock;
import com.steganomobile.receiver.controller.cc.FileSize;
import com.steganomobile.receiver.controller.cc.MemoryLoad;
import com.steganomobile.receiver.controller.cc.SystemLoad;
import com.steganomobile.receiver.controller.cc.TypeOfIntent;
import com.steganomobile.receiver.controller.cc.UnixSocketDiscovery;
import com.steganomobile.receiver.controller.cc.UsageTrend;
import com.steganomobile.receiver.controller.cc.VolumeSettings;

public class CcService extends Service {

    private static final String TAG = CcService.class.getSimpleName();
    private Handler handler;

    private boolean isCcReceiverRegistered = false;
    private boolean isCcObserverRegistered = false;
    private boolean isCcHandlerRegistered = false;

    private IntentFilter intentFilter = new IntentFilter();
    private SyncReceiver syncReceiver;
    private SyncObserver syncObserver;
    private SyncHandler syncHandler;

    private ReceiverBinder mBinder = new ReceiverBinder();
    private BroadcastReceiver infoReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        infoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final CcInfo info = intent.getParcelableExtra(Const.EXTRA_CC_INFO);

                if (info.getStatus() == CcStatus.START) {

                    if (syncObserver != null) {
                        if (isCcObserverRegistered) {
                            syncObserver.finish(context);
                            getContentResolver().unregisterContentObserver(syncObserver);
                            isCcObserverRegistered = false;
                        }
                    }

                    if (syncReceiver != null) {
                        if (isCcReceiverRegistered) {
                            syncReceiver.finish(context);
                            unregisterReceiver(syncReceiver);
                            isCcReceiverRegistered = false;
                        }
                    }

                    if (syncHandler != null) {
                        if (isCcHandlerRegistered) {
                            syncHandler.finish(context);
                            isCcHandlerRegistered = false;
                        }
                    }

                    switch (info.getSync()) {
                        case NO_VALUE:
                            break;
                        case CONTENT_OBSERVER:
                            syncObserver = new SyncObserver(handler, initCc(context, info));
                            getContentResolver().registerContentObserver(Const.SYNC_OBSERVER, true, syncObserver);
                            isCcObserverRegistered = true;
                            break;
                        case BROADCAST_RECEIVER:
                            syncReceiver = new SyncReceiver(initCc(context, info));
                            intentFilter.addAction(Const.SYNC_RECEIVER);
                            registerReceiver(syncReceiver, intentFilter);
                            isCcReceiverRegistered = true;
                            break;
                        case HANDLER:
                            syncHandler = new SyncHandler(initCc(context, info));
                            isCcHandlerRegistered = true;
                            break;
                    }
                } else if (info.getStatus() == CcStatus.FINISH) {

                    switch (info.getSync()) {
                        case NO_VALUE:
                            break;
                        case CONTENT_OBSERVER:
                            if (isCcObserverRegistered) {
                                getContentResolver().unregisterContentObserver(syncObserver);
                                isCcObserverRegistered = false;
                                syncObserver.finish(context);
                            }
                            break;
                        case BROADCAST_RECEIVER:
                            if (isCcReceiverRegistered) {
                                unregisterReceiver(syncReceiver);
                                isCcReceiverRegistered = false;
                                syncReceiver.finish(context);
                            }
                            break;
                        case HANDLER:
                            if (isCcHandlerRegistered) {
                                isCcHandlerRegistered = false;
                                syncHandler.finish(context);
                            }
                            break;
                    }
                }
            }
        };
        startReceiverService();
        return Service.START_STICKY;
    }

    private void startReceiverService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler();
                for (int i = -127; i < 128; i++) {
                    intentFilter.addAction(Const.SYNC_RECEIVER + i);
                }
                registerReceiver(infoReceiver, new IntentFilter(Const.ACTION_INFO));
                makeToast(R.string.local_service_connected);
                Looper.loop();
            }
        }).start();
    }

    private CcImpl initCc(Context context, CcInfo info) {
        DataCollector collector = new DataCollector(info);

        switch (info.getName()) {
            case NO_VALUE:
                break;
            case VOLUME_MUSIC:
            case VOLUME_RING:
            case VOLUME_NOTIFICATION:
            case VOLUME_DTMF:
            case VOLUME_SYSTEM:
            case VOLUME_ALARM:
            case VOLUME_VOICE_CALL:
                return new VolumeSettings(context, collector);
            case FILE_LOCK:
                return new FileLock(collector);
            case FILE_SIZE:
                return new FileSize(collector);
            case FILE_EXISTENCE:
                return new FileExistence(collector);
            case CONTENT_OF_URI:
                return new ContentOfUri(collector);
            case TYPE_OF_INTENT:
                return new TypeOfIntent(collector);
            case UNIX_SOCKET_DISCOVERY:
                return new UnixSocketDiscovery(collector);
            case MEMORY_LOAD:
                return new MemoryLoad(context, collector);
            case SYSTEM_LOAD:
                return new SystemLoad(context, collector);
            case USAGE_TREND:
                return new UsageTrend(context, collector);
        }
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        makeToast(R.string.local_service_disconnected);

        if (isCcObserverRegistered) {
            getContentResolver().unregisterContentObserver(syncObserver);
            isCcObserverRegistered = false;
        }

        if (isCcReceiverRegistered) {
            unregisterReceiver(syncReceiver);
            isCcReceiverRegistered = false;
        }
    }

    private void makeToast(final int resId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ReceiverBinder extends Binder {
        CcService getService() {
            return CcService.this;
        }
    }
}
