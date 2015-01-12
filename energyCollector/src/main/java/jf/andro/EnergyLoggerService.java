package jf.andro;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.receiver.model.cc.CcReceiverItem;
import com.steganomobile.common.sender.model.CcMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class EnergyLoggerService extends Service {

    public static final String SEP = ";";
    public final static String filenameFinal = "energy.csv";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    private final String filename = "energy.tmp";
    //private Vector<String> uidNames = null;
    PowerTutorReceiver mResultReceiver;
    private PowerManager.WakeLock wl;
    private Timer timer = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    private String numberOfTests = "000";
    private static int nbTest = -1;
    private String email;
    private Vector<Integer> uidIndex = null;
    private int idCC;
    private File energyFile;
    private File infoFile;
    private File root = Environment.getExternalStorageDirectory();
    private SteganoStopReceiver steganoReceiver;
    private ArrayList<Pair<String, CcReceiverItem>> results = new ArrayList<Pair<String, CcReceiverItem>>();
    private int testCounter = 0;

    public static int finished = 0;
    public static int started = 0;

    public synchronized static String isFinishedTrue() {
        if (finished > 0) {
            finished = 0;
            return "1";
        }
        return "0";
    }

    public synchronized static String isStartedTrue() {
        if (started > 0) {
            started = 0;
            return "1";
        }
        return "0";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        steganoReceiver = new SteganoStopReceiver();
        IntentFilter filter = new IntentFilter(Const.ACTION_FINISH_RECEIVER_CC);
        filter.addAction(Const.ACTION_FINISH_STEGANO);
        filter.addAction(Const.ACTION_START_STEGANO);
        registerReceiver(steganoReceiver, filter);

        testCounter = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Tell the user we stopped.
        Toast.makeText(this, "START !", Toast.LENGTH_SHORT).show();

        // KEEP CPU RUNNING !
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JFL");
        wl.acquire();

        // Getting the numbering of the current test if any
        Bundle extras = intent.getExtras();
        if (extras != null) {
            nbTest = extras.getInt("nbTest");
            numberOfTests = String.format("%03d", nbTest);
            email = extras.getString("email");
            idCC = extras.getInt("idCC") + 1;
        }

        // Reset energy collected
        PowerTutorReceiver.resetEnergy();

        // Reset tables
        uidIndex = new Vector<Integer>();
//		uidNames = new Vector<String>();

        // Cancel old tasks
        if (timer != null)
            timer.cancel();

        Log.i("JFL", "Service STARTED !");

        // Read one time for purging energies
        EnergyReader.readEnergyValues();
        ScenarioService.getCCDataScheduled();
        EnergyLoggerService.isFinishedTrue();
        PowerTutorReceiver.getUIDEnergy();
        PowerTutorReceiver.getUIDNames();
        File file = new File(root, filename);
        clearFile(file);
        energyFile = new File(root, "" + numberOfTests + "_" + CcMethod.FILE_NAMES[idCC] + "_" + filenameFinal);
        infoFile = new File(root, "" + numberOfTests + "_" + CcMethod.FILE_NAMES[idCC] + "_info.csv");
        clearFile(energyFile);
        clearFile(infoFile);

        timer = new Timer();

        BlueLightOn();

        TimerTask t = new TimerTask() {

            @Override
            public void run() {

                EnergyReader.readEnergyValues();
                Energy e = EnergyReader.readLastEnergy();

                MemoryInfo mi = new MemoryInfo();
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                activityManager.getMemoryInfo(mi);
                long memory = mi.availMem;


                String values = String.valueOf(e.current_now) + SEP + e.capacity + SEP +
                        e.voltage_now + SEP + e.charge_now + SEP + memory + SEP + e.readCpu + SEP +
                        e.deltaCpu + SEP;

                int hiddenDataSend = ScenarioService.getCCDataScheduled();
                String started = EnergyLoggerService.isStartedTrue();
                String md5 = ScenarioService.getCCDataScheduledMd5(testCounter);
                //Log.w("JFL", "Logging md5 " + md5);
                values += started + SEP;
                values += EnergyLoggerService.isFinishedTrue() + SEP;
                values += "1".equals(started) ? hiddenDataSend + SEP : SEP;
                values += "1".equals(started) ? md5 : "";

                HashMap<Integer, Integer> h = PowerTutorReceiver.getUIDEnergy();
                HashMap<Integer, String> hname = PowerTutorReceiver.getUIDNames();

                // Scanning all uids
                SharedPreferences seenApps = getApplicationContext().getSharedPreferences("apps", Context.MODE_PRIVATE);
                int nbAppTotal = seenApps.getInt("nbAppTotal", 0);
                SharedPreferences.Editor seenAppsEditor = null;
                HashMap<Integer, String> nbToNameApp = new HashMap<Integer, String>();
                for (int uid : h.keySet()) {
                    String nameApp = hname.get(uid);
                    if (seenApps.contains(nameApp)) {
                        int assignedNbApp = seenApps.getInt(nameApp, -1);
                        nbToNameApp.put(assignedNbApp, nameApp);
                        //Log.w("JFL", "Seen: " + nameApp + " and numbered: " + assignedNbApp);
                    } else {
                        nbAppTotal++;
                        if (seenAppsEditor == null)
                            seenAppsEditor = seenApps.edit();
                        seenAppsEditor.putInt("nbAppTotal", nbAppTotal);
                        seenAppsEditor.putInt(nameApp, nbAppTotal);
                        nbToNameApp.put(nbAppTotal, nameApp);
                        //Log.w("JFL", "Creating " + nameApp + " and numbered: " + nbAppTotal);
                    }

                }

                if (seenAppsEditor != null)
                    seenAppsEditor.apply();

                String uidEnergies = "";
                nbAppTotal = seenApps.getInt("nbAppTotal", 0);
                for (int eachApp = 1; eachApp <= nbAppTotal; eachApp++) {
                    int appEnergyValue = 0;
                    for (int uid : h.keySet()) {
                        if (hname.get(uid).equals(nbToNameApp.get(eachApp))) // this process is for app with name of app nb eachapp
                        {
                            Integer energy = h.get(uid);
                            appEnergyValue += energy;
                        }
                    }

                    uidEnergies += SEP + appEnergyValue;
                }

                uidEnergies += "\n";
                values += uidEnergies;

                File file = new File(root, filename);

                try {
                    FileWriter filewriter = new FileWriter(file, true);
                    BufferedWriter out = new BufferedWriter(filewriter);

                    out.write(dateFormat.format(new Date()) + SEP + values);

                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(t, 1000, 1000);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(steganoReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void BlueLightOn() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.ledARGB = 0xFF0000ff;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 1;
        notif.ledOffMS = 0;
        int LED_NOTIFICATION_ID = 0;
        nm.notify(LED_NOTIFICATION_ID, notif);
    }

    private void BlueLightOff() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.ledARGB = 0xFF000000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 0;
        notif.ledOffMS = 0;
        int LED_NOTIFICATION_ID = 0;
        nm.notify(LED_NOTIFICATION_ID, notif);
    }

    private void writeToFile(String data, File file) {

        try {
            FileWriter filewriter = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(filewriter);
            out.write(data);
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Tell the user we stopped.
        Toast.makeText(this, "STOP !", Toast.LENGTH_SHORT).show();
    }

    private void sendEmail(String email) {

        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.fromFile(energyFile));
        uris.add(Uri.fromFile(infoFile));
        Methods.sendEmail(this, uris, email);
    }

    private String processEnergyData() {
        String header = "\nDate" + SEP + "Current now" + SEP + "Level%" + SEP + "Voltage" + SEP +
                "Charging" + SEP + "Memory" + SEP + "ReadCPU" + SEP + "DeltaCPU" + SEP +
                "StartCC" + SEP + "EndCC" + SEP + "HiddenDataSent" + SEP + "MessageMd5";

        StringBuilder b = new StringBuilder(header);
        SharedPreferences seenApps = getSharedPreferences("apps", Context.MODE_PRIVATE);
        int nbAppTotal = seenApps.getInt("nbAppTotal", 0);

        Map<String, Integer> map = (Map<String, Integer>) seenApps.getAll();
        HashMap<Integer, String> nbToNameApp = new HashMap<Integer, String>();
        for (String eachApp : map.keySet()) {
            if (!eachApp.equals("nbAppTotal"))
                nbToNameApp.put(map.get(eachApp), eachApp);
        }

        for (int eachApp = 1; eachApp <= nbAppTotal; eachApp++) {
            b.append(SEP).append(nbToNameApp.get(eachApp));
        }
        b.append("\n");

        FileReader tempFile;
        try {
            tempFile = new FileReader(root + "/" + filename);
            BufferedReader in = new BufferedReader(tempFile);
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    b.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        clearFile(new File(root + "/" + filename));

        return b.toString();
    }

    private void clearFile(File file) {
        try {
            new RandomAccessFile(file, "rw").setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class SteganoStopReceiver extends BroadcastReceiver {

        private final String TAG = SteganoStopReceiver.class.getSimpleName();
        private boolean printHeader = true;

        private void GreenFlashLight(Context context) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification();
            notif.ledARGB = 0xFF00ff00;
            notif.flags = Notification.FLAG_SHOW_LIGHTS;
            notif.ledOnMS = 100;
            notif.ledOffMS = 100;
            int LED_NOTIFICATION_ID = 0;
            nm.notify(LED_NOTIFICATION_ID, notif);
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Const.ACTION_FINISH_RECEIVER_CC.equals(intent.getAction())) {
                CcReceiverItem item = intent.getParcelableExtra(Const.EXTRA_ITEM_RECEIVER_CC);
                writeToFile(item.print(SEP, printHeader, true), infoFile);
                printHeader = false;
                EnergyLoggerService.finished++; // for detecting the end when logging
                Log.i("JFL", "End of one stegano transmission in " + item.getMessage().getTime().getDuration() + " ms");
            }
            if (Const.ACTION_FINISH_STEGANO.equals(intent.getAction())) {
                writeToFile(processEnergyData(), energyFile);
                testCounter++;
                Log.i("JFL", "Done: " + testCounter + " XP over " + EnergyLoggerService.nbTest);
            }
            if (Const.ACTION_START_STEGANO.equals(intent.getAction())) {
                Log.i("JFL", "Start of stegano transmission !");
                started++; // for detecting the beginning when logging
            }

            if (testCounter == EnergyLoggerService.nbTest) {
                sendEmail(email);
                Log.i("JFL", String.format("All XP finished: stopping the logger service."));
                Intent serviceLogger = new Intent("jf.andro.scenarioservice");
                stopService(serviceLogger);
                GreenFlashLight(context);

                Methods.playSound(context);
                timer.cancel();
                wl.release();
                stopSelf();
            }
        }
    }
}
