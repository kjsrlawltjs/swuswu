package jf.andro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.StringTokenizer;

public class PowerTutorReceiver extends BroadcastReceiver {

    private static int senderEnergy = 0;
    private static int receiverEnergy = 0;
    private static HashMap<Integer, Integer> allUIDPower = new HashMap<Integer, Integer>();
    private static HashMap<Integer, String> allUIDNames = new HashMap<Integer, String>();

    public static synchronized void resetEnergy() {
        senderEnergy = 0;
        receiverEnergy = 0;
        allUIDPower = new HashMap<Integer, Integer>();
    }

    private static synchronized void addSenderEnergy(int energy) {
        senderEnergy = senderEnergy + energy;
    }

    private static synchronized void addReceiverEnergy(int energy) {
        receiverEnergy = receiverEnergy + energy;
    }

    public static synchronized int getReceiverEnergy() {
        int tmp = receiverEnergy;
        receiverEnergy = 0;
        return tmp;
    }

    public static synchronized int getSenderEnergy() {
        int tmp = senderEnergy;
        senderEnergy = 0;
        return tmp;
    }

    public static synchronized HashMap<Integer, Integer> getUIDEnergy() {
        HashMap<Integer, Integer> tmp = allUIDPower;
        allUIDPower = new HashMap<Integer, Integer>();
        return tmp;
    }

    public static synchronized HashMap<Integer, String> getUIDNames() {
        HashMap<Integer, String> tmp = allUIDNames;
        allUIDNames = new HashMap<Integer, String>();
        return tmp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extra = intent.getExtras();

        if (extra == null) {
            Log.e("JFL", "ERROR: extra is null !");
            return;
        }
        if (extra.containsKey("sender"))
            addSenderEnergy(extra.getInt("sender"));
        if (extra.containsKey("receiver"))
            addReceiverEnergy(extra.getInt("receiver"));
        if (extra.containsKey("allUID"))
            addAllUIDEnergy(extra.getString("allUID"));


    }


    private void addAllUIDEnergy(String s) {

        StringTokenizer st = new StringTokenizer(s, ";");
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            StringTokenizer st2 = new StringTokenizer(token, "=");
            if (st2.countTokens() == 3) {
                int uid = Integer.parseInt(st2.nextToken());
                int power = Integer.parseInt(st2.nextToken());
                String name = st2.nextToken();

                //if (name.contains("Stegano"))
                //  	  Log.w("JFL", "STEGANO: " + uid + "=" +  name);

                Integer oldPower = allUIDPower.get(uid);
                String oldName = allUIDNames.get(uid);
                if (oldPower == null || oldName == null) // May happen if interrupted between storing actions
                {
                    allUIDPower.put(uid, Integer.valueOf(power));
                    allUIDNames.put(uid, name);
                } else {
                    allUIDPower.put(uid, Integer.valueOf(power + oldPower.intValue()));
                    if (!oldName.equals(name)) {
                        Log.e("JFL", "This uid " + uid + " changed of name from " + allUIDNames.get(uid) + " to " + name);
                        allUIDNames.put(uid, name);
                    }
                }
            } else {
                Log.e("JFL", "Error of received format ! should be uid=45=Machin instead of " + token);
            }
        }

    }
}
