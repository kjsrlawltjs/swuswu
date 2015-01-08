package jf.andro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ScenarioService extends Service {

    // How many data the CC should transmit
    private static int getCCDataScheduled = 0;
    // The md5 sum of the transmitted message
    private static String md5Message = "";
    // If the covert channels CC should be disabled for these XP
    private static boolean idleCC = false;
    private PowerManager.WakeLock wl;
    // The number of XP to perform
    private int nbXP;
    // The id of the used Covert Channels
    private int idCC;
    private String email;
    private String TAG = ScenarioService.class.getSimpleName();

    public synchronized static int getCCDataScheduled() {
        int tmp = getCCDataScheduled;
        getCCDataScheduled = 0;
        return tmp;
    }

    public synchronized static void setCCDataScheduled(int nbdata, String md5) {
        Log.i("JFL", "Some data is scheduled to send !");
        getCCDataScheduled = nbdata;
        md5Message = md5;
    }

    public static String getCCDataScheduledMd5() {
        return md5Message;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // KEEP CPU RUNNING !
        PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JFL");
        wl.acquire();

        // Reset somes values
        setCCDataScheduled(0, "");

        // Tell the user we start
        Toast.makeText(this, "START SCENARIO: Switch off the SCREEN !", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        int scenario = extras.getInt("scenario");
        idleCC = extras.getBoolean("idleCC");
        nbXP = extras.getInt("nbXP");
        email = extras.getString("email");
        idCC = extras.getInt("idCC");
        RedFlashLight();

        Thread t = null;

        // SCENARIOS
        // *********
        switch (scenario) {

            case 3:

                File root = Environment.getExternalStorageDirectory();
                for (int i = 0; i <= 100; i++) {
                    String numberOfTests = String.format("%03d", i);
                    File file = new File(root, numberOfTests + "_" + EnergyLoggerService.filenameFinal);
                    file.delete();
                }

                t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            // Parameters for randomness
                            Random r = new Random();
                            int nb_messages_max = 1;//nbXP; // Max nb messages
                            int message_size_max = 5; //1000; // Size max 1000 Bytes
                            int nb_first_sleep_random_max = 30; // Max sleeping time
                            int nb_second_sleep_random_max = 30; // Max sleeping time
                            int wait_factor = 1;
                            sleep(3 * 1000); // Sleep a little before starting

                            // Choose a random number of messages to send
                            int nb_message = 1; // + r.nextInt(nb_messages_max);

                            // Starting Energy Collector Service for logging
                            Intent service = new Intent("jf.andro.energyservice");
                            service.putExtra("nbTest", nb_message);
                            service.putExtra("email", email);
                            service.putExtra("idCC", idCC);

                            startService(service);

                            // Random sleep before the first CC message sending
//                                sleep((10 + r.nextInt(nb_first_sleep_random_max)) * 1000);

                            if (!idleCC) // IDLE the stegano transmission
                            {
                                String data = generate(message_size_max, nb_message);
                                // Pick a random size for the message to transmit
                                int size_data = data.length() * 8; // bits

                                String md5 = null;
                                try {
                                    MessageDigest md = MessageDigest.getInstance("MD5");
                                    md.update(data.getBytes(), 0, data.length());
                                    md5 = new BigInteger(1, md.digest()).toString(16); // Hashed
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                                Log.i("JFL", "Sending  message " + nb_message + " of size " + size_data / 8 + " Bytes (" + size_data + " bits): " + md5);
                                setCCDataScheduled(size_data, md5);
                                // Send the intent that asks the Stegano sender to transmit !

                                Intent intent = new Intent(Const.ACTION_START_SENDER_CC);
                                CcMethod method = CcMethod.getFromInt(idCC + 1);
                                Log.w("JFL", String.format("Using %s method!", CcMethod.NAMES[method.getValue()]));

                                // iterations = 2, means that CC will be activated 2 times.
                                int iterations = 1;
                                // interval is time between synchronization step in stegano system.
                                // 200 [ms] is some kind of universal number, because it is the lowest number
                                // which will provide good accuracy for all CCs
                                int interval = 200;
                                CcType type = CcType.MESSAGE;
                                CcSync sync = CcSync.BROADCAST_RECEIVER;
                                CcInfo info = new CcInfo(CcStatus.START, method, iterations, type, interval, sync);
                                CcSenderItem item = new CcSenderItem(data, info);
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

    private void RedFlashLight() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.ledARGB = 0xFFff0000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;
        int LED_NOTIFICATION_ID = 0;
        nm.notify(LED_NOTIFICATION_ID, notif);
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
