package jf.andro;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.receiver.model.cc.CcReceiverItem;

public class SteganoReceiver extends BroadcastReceiver {

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
    public void onReceive(Context context, Intent intent) {
        if (Const.ACTION_FINISH_RECEIVER_CC.equals(intent.getAction())) {
            Log.w("JFL", "End of stegano transmission !");
            CcReceiverItem item = intent.getParcelableExtra(Const.EXTRA_ITEM_RECEIVER_CC);
            Toast.makeText(context, "Finished in " + item.getMessage().getTime().getDuration() + " ms", Toast.LENGTH_LONG).show();

            Log.w("JFL", String.format("All XP finished: stopping the logger service."));
            GreenFlashLight(context);
            Intent stopservice = new Intent(EnergyLoggerService.ACTION_STOP_SERVICE);
            // Send information to logger about our sending
            // Test number tells how many times the CC was running
            // (how many times there was execution of sendBroadcast to stegano system
            stopservice.putExtra(Const.EXTRA_TEST_NUMBER, 1);
            context.sendBroadcast(stopservice);

            finished++;
        } else if (Const.ACTION_START_STEGANO.equals(intent.getAction())) {
            Log.w("JFL", "Start of stegano transmission !");
            started++;
        }
    }

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
}
