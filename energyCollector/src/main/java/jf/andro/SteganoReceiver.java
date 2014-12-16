package jf.andro;

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
            finished++;
        } else if (Const.ACTION_START_STEGANO.equals(intent.getAction())) {
            Log.w("JFL", "Start of stegano transmission !");
            started++;
        }
    }
}
