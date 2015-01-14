package jf.andro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.Cc;
import com.steganomobile.common.sender.model.CcSenderInfo;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ScenarioService extends Service {

    // The data to transmit
    private static String getCCDataScheduled = "";
    // If the covert channels CC should be disabled for these XP
    private static boolean idleCC = false;
    private PowerManager.WakeLock wl;
    // The number of XP to perform
    private static int nbXP;
    // The id of the used Covert Channels
    private int idCC;
    private String email;

    public synchronized static int getCCDataScheduled() {
        return getCCDataScheduled.length() / nbXP;
    }

    public synchronized static void setCCDataScheduled(String data) {
        getCCDataScheduled = data;
        if (!data.equals(""))
            Log.i("JFL", "Some data is scheduled to send: " + nbXP + " x " + getCCDataScheduled());
    }

    public static String getCCDataScheduledMd5(int currentsubpart) {
        if (currentsubpart > nbXP || currentsubpart < 1)
            return "";
        //Log.w("JFL", "Getting chunk "+ (currentsubpart+1) + " over " + nbXP);
        int chunk_length = getCCDataScheduled.length() / nbXP;
        String data = getCCDataScheduled.substring(chunk_length * (currentsubpart-1), chunk_length * (currentsubpart));
        //Log.w("JFL", "Computing md5 on message "+ data);
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes(), 0, data.length());
            md5 = new BigInteger(1, md.digest()).toString(16); // Hashed
        } catch (NoSuchAlgorithmException e) {
            Log.e("JFL", "No such algorithme exception ! " + e);
        }
        return md5;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // KEEP CPU RUNNING !
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JFL");
        wl.acquire();

        // Reset somes values
        setCCDataScheduled("");

        // Tell the user we start
        Toast.makeText(this, "START SCENARIO: Switch off the SCREEN !", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        int scenario = extras.getInt("scenario");
        idleCC = extras.getBoolean("idleCC");
        nbXP = extras.getInt("nbXP");
        email = extras.getString("email");
        idCC = extras.getInt("idCC");

        Thread t;

        // SCENARIOS
        // *********
        switch (scenario) {

            case 3:

                t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            // Parameters for randomness
                            int message_size_max = 5; //1000; // Size max 1000 Bytes

                            // Sleep a little before starting the energy recording
                            sleep(1000);

                            // Starting Energy Collector Service for logging
                            Intent service = new Intent("jf.andro.energyservice");
                            service.putExtra("nbTest", nbXP);
                            service.putExtra("email", email);
                            service.putExtra("idCC", idCC);
                            startService(service);

                            // Short sleep before starting
                            sleep(2 * 1000);

                            if (!idleCC) // IDLE the stegano transmission
                            {
                                String data = generate(nbXP * message_size_max, 1); // seed is fixed to 1
                                int size_data = message_size_max * 8; // bits


                                Log.i("JFL", "Sending " + nbXP + " messages of size " + size_data / 8 + " Bytes (" + size_data + " bits)");
                                setCCDataScheduled(data);
                                // Send the intent that asks the Stegano sender to transmit !

                                Intent intent = new Intent(Const.ACTION_START_SENDER_CC);
                                Cc method = Cc.getFromInt(idCC + 1);
                                Log.i("JFL", String.format("Using %s method!", Cc.NAMES[method.getValue()]));

                                // iterations = 2, means that CC will be activated 2 times.
                                int iterations = nbXP; // We activate only one time the CC.
                                // interval is time between synchronization step in stegano system.
                                // 200 [ms] is some kind of universal number, because it is the lowest number
                                // which will provide good accuracy for all CCs
                                int interval = 200;
                                //int interval = 10;
                                CcType type = CcType.PLAIN_TEXT;
                                CcSync sync = CcSync.BROADCAST_RECEIVER;
                                CcSenderInfo info = new CcSenderInfo(CcStatus.START, method, iterations, type, interval, sync);


                                CcSenderItem item = new CcSenderItem(data, info);
                                item.setSendsubparts(nbXP); // Used to ask the CC sender to send a subpart of the message
                                // for example if the message is ABCDEF and there is 3 iteration to do
                                // then for iteration 1, send AB then for iteration 2 send CD, and then EF.
                                intent.putExtra(Const.EXTRA_ITEM_SENDER_CC, item);
                                sendBroadcast(intent);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();

                break;
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wl.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String generate(int message_size_max, int seed) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; // Tu supprimes les lettres dont tu ne veux pas
        String pass = "";
        Random r = new Random(seed);
        int length = (100 + r.nextInt(message_size_max));
        for (int x = 0; x < length; x++) {
            int i = r.nextInt(62); // Si tu supprimes des lettres tu diminues ce nb
            pass += chars.charAt(i);
        }
        return pass;
    }
}
