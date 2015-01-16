package com.steganomobile.sender.view;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.Cc;
import com.steganomobile.common.sender.model.CcSegment;
import com.steganomobile.common.sender.model.CcSenderInfo;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.sender.controller.DataConverter;
import com.steganomobile.sender.controller.cc.CcSender;
import com.steganomobile.sender.controller.cc.ContentOfUriSender;
import com.steganomobile.sender.controller.cc.FileExistenceSender;
import com.steganomobile.sender.controller.cc.FileLockSender;
import com.steganomobile.sender.controller.cc.FileSizeSender;
import com.steganomobile.sender.controller.cc.MemoryLoadSender;
import com.steganomobile.sender.controller.cc.SystemLoadSender;
import com.steganomobile.sender.controller.cc.TypeOfIntentSender;
import com.steganomobile.sender.controller.cc.UnixSocketDiscoverySender;
import com.steganomobile.sender.controller.cc.UsageTrendSender;
import com.steganomobile.sender.controller.cc.VolumeSettingsSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DataService extends IntentService {

    private static final String TAG = DataService.class.getSimpleName();
    public static volatile boolean running = true;

    public DataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //try
        //{

// What is that ? I Had error with this (leaked intent)
//        running = true;
//        BroadcastReceiver stopReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (Const.ACTION_FORCE_STOP.equals(intent.getAction())) {
//                    running = false;
//                }
//            }
//        };
//
//        registerReceiver(stopReceiver, new IntentFilter(Const.ACTION_FORCE_STOP));
        CcSenderItem item = intent.getParcelableExtra(Const.EXTRA_ITEM_SENDER_CC);
        int iterations = item.getInfo().getIterations();

        if (item.getInfo().getName() == Cc.CONTENT_OF_URI) {
            item.getInfo().setSync(CcSync.CONTENT_OBSERVER);
        } else if (item.getInfo().getName() == Cc.TYPE_OF_INTENT) {
            item.getInfo().setSync(CcSync.BROADCAST_RECEIVER);
        }

        for (int i = 1; i <= iterations; i++) {
            item.setCurrentsubpart(i); // indicates the number of the current XP
            start(i, item.getInfo());
            if (i > 0)
            waitToStart();
            sendBroadcast(new Intent(Const.ACTION_START_STEGANO));
            send(item);
            finish(item.getInfo());
            waitToFinish();
            sendBroadcast(new Intent(Const.ACTION_FINISH_STEGANO));
        }

        //unregisterReceiver(stopReceiver);


//        catch(Exception e)
//        {
////stream for writing text
//            FileWriter writer=null;
//            try
//            {
//                File root = Environment.getExternalStorageDirectory();
//                File dir = new File(root.getAbsolutePath() + "/");
//                File newfile = new File(dir, "jfllog.log");
//                writer = new FileWriter(newfile);
//                PrintWriter pw = new PrintWriter (writer);
//                Log.e("JFL", "ERROR: " + e);
//                e.printStackTrace();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
//                Date date = new Date();
//                writer.write(dateFormat.format(date)+"\n");
//                e.printStackTrace(pw);
//            } catch(Throwable t) {}
//            finally
//            {
//                if(writer != null) try {
//                    writer.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//
//        }
    }

    private void waitToFinish() {
        synchronized (this) {
            try {
                Random r = new Random();
                int rt = (5 + r.nextInt(Const.SYNC_WAIT_SLEEP_RANDOM));
                Log.i("CCDataService", "Waiting random time before ending: " + rt + "s...");
                wait(rt* 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitToStart() {
        synchronized (this) {
            try {
                Random r = new Random();
                int rt = (5 + r.nextInt(Const.SYNC_WAIT_SLEEP_RANDOM));
                Log.i("CCDataService", "Waiting random time before starting: " + rt + "s...");
                wait(rt * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void finish(CcSenderInfo info) {
        Intent statusIntent = new Intent(Const.ACTION_INFO);

        CcStatus status = CcStatus.FINISH;
        CcSync sync = info.getSync();
        statusIntent.putExtra(Const.EXTRA_CC_INFO, new CcSenderInfo(status, sync));
        Log.i("CCDataService", "Ultimate broadcast ACTION_INFO");
        sendBroadcast(statusIntent);
    }

    private void send(CcSenderItem item) {
        CcSender cc = getCcSender(item.getInfo());
        CcSegment segment = item.getInfo().getName().getSegment();
        int interval = item.getInfo().getInterval();
        Log.i("CCDataService", "In sender sending the message: " + item.getData());

        for (byte element : DataConverter.getData(item.getData(), segment)) {
            cc.onSend(this, element);
            cc.onSync(this, item.getInfo().getSync(), element);
            waitToSend(interval);
            cc.onRestart();
            if (!running) break;
        }
        cc.onFinish();
    }

    private void start(int i, CcSenderInfo info) {
        info.setIterations(i);

        Intent statusIntent = new Intent(Const.ACTION_INFO);
        statusIntent.putExtra(Const.EXTRA_CC_INFO, info);
        sendBroadcast(statusIntent);
    }

    private CcSender getCcSender(CcSenderInfo info) {
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
                return new VolumeSettingsSender(this, info.getName().getStream());
            case FILE_LOCK:
                return new FileLockSender();
            case FILE_SIZE:
                return new FileSizeSender();
            case FILE_EXISTENCE:
                return new FileExistenceSender();
            case CONTENT_OF_URI:
                return new ContentOfUriSender();
            case TYPE_OF_INTENT:
                return new TypeOfIntentSender();
            case UNIX_SOCKET_DISCOVERY:
                return new UnixSocketDiscoverySender(info.getPort());
            case MEMORY_LOAD:
                return new MemoryLoadSender();
            case SYSTEM_LOAD:
                return new SystemLoadSender(info.getInterval());
            case USAGE_TREND:
                return new UsageTrendSender(info.getInterval());
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
