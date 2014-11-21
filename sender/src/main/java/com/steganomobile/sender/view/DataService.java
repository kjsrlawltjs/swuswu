package com.steganomobile.sender.view;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcSegment;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.sender.controller.DataConverter;
import com.steganomobile.sender.controller.cc.CcImpl;
import com.steganomobile.sender.controller.cc.ContentOfUri;
import com.steganomobile.sender.controller.cc.FileExistence;
import com.steganomobile.sender.controller.cc.FileLock;
import com.steganomobile.sender.controller.cc.FileSize;
import com.steganomobile.sender.controller.cc.MemoryLoad;
import com.steganomobile.sender.controller.cc.SystemLoad;
import com.steganomobile.sender.controller.cc.TypeOfIntent;
import com.steganomobile.sender.controller.cc.UnixSocketDiscovery;
import com.steganomobile.sender.controller.cc.UsageTrend;
import com.steganomobile.sender.controller.cc.VolumeSettings;

public class DataService extends IntentService {

    private static final String TAG = DataService.class.getSimpleName();

    public DataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CcSenderItem item = intent.getParcelableExtra(Const.EXTRA_ITEM_SENDER_CC);

        int iterations = item.getInfo().getIterations();

        for (int i = 1; i <= iterations; i++) {

            start(i, item.getInfo());
            waitToSync();
            send(item);
            finish(item.getInfo());
            waitToSync();
        }
    }

    private void finish(CcInfo info) {
        Intent statusIntent = new Intent(Const.ACTION_INFO);

        CcStatus status = CcStatus.FINISH;
        CcSync sync = info.getSync();
        statusIntent.putExtra(Const.EXTRA_CC_INFO, new CcInfo(status, sync));
        sendBroadcast(statusIntent);
    }

    private void send(CcSenderItem item) {
        CcImpl cc = getMethod(item.getInfo());
        CcSegment segment = item.getInfo().getName().getSegment();
        int interval = item.getInfo().getInterval();

        for (byte element : DataConverter.getData(item.getData(), segment)) {
            cc.sendCc(this, element);
            cc.syncCc(this, item.getInfo().getSync());
            waitToSend(interval);
            cc.finishCc();
        }
        cc.clearCc();
    }

    private void start(int i, CcInfo info) {
        info.setIterations(i);

        Intent statusIntent = new Intent(Const.ACTION_INFO);
        statusIntent.putExtra(Const.EXTRA_CC_INFO, info);
        sendBroadcast(statusIntent);
    }

    private void waitToSync() {
        synchronized (this) {
            try {
                wait(Const.SYNC_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private CcImpl getMethod(CcInfo info) {
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
                return new VolumeSettings(this, info.getName().getStream());
            case FILE_LOCK:
                return new FileLock();
            case FILE_SIZE:
                return new FileSize();
            case FILE_EXISTENCE:
                return new FileExistence();
            case CONTENT_OF_URI:
                return new ContentOfUri();
            case TYPE_OF_INTENT:
                return new TypeOfIntent();
            case UNIX_SOCKET_DISCOVERY:
                return new UnixSocketDiscovery(info.getPort());
            case MEMORY_LOAD:
                return new MemoryLoad();
            case SYSTEM_LOAD:
                return new SystemLoad(info.getInterval());
            case USAGE_TREND:
                return new UsageTrend(info.getInterval());
        }
        return null;
    }

    private void waitToSend(int interval) {
        try {
            synchronized (this) {
                wait(interval);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void waitToSend(int interval, int nannos) {
        try {
            synchronized (this) {
                wait(interval - 1, 1000 - nannos);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
