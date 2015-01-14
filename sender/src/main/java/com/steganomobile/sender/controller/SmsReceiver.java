package com.steganomobile.sender.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.Cc;
import com.steganomobile.common.sender.model.CcSenderInfo;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();
    private static boolean isListening = true;

    public SmsReceiver() {
    }

    private void addSettingsInfoToIntent(Context context, Intent intent, String message) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int interval = Integer.parseInt(preferences.getString(Const.PREF_KEY_INTERVAL, Const.DEFAULT_INTERVAL));
        int iterations = Integer.parseInt(preferences.getString(Const.PREF_KEY_ITERATIONS, Const.DEFAULT_ITERATIONS));
        int ccNameId = Integer.parseInt(preferences.getString(Const.PREF_KEY_METHODS, Const.DEFAULT_METHODS));
        int syncId = Integer.parseInt(preferences.getString(Const.PREF_KEY_SYNC, Const.DEFAULT_SYNC));

        CcStatus status = CcStatus.START;
        CcSync sync = CcSync.getFromInt(syncId);
        CcType type = CcType.NEW_SMS;
        Cc name = Cc.getFromInt(ccNameId);
        CcSenderInfo info = new CcSenderInfo(status, name, iterations, type, interval, sync);
        CcSenderItem item = new CcSenderItem(message, info);
        intent.putExtra(Const.EXTRA_ITEM_SENDER_CC, item);
    }

    private void sendStegano(Context context, String message) {
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_START_SENDER_CC);
        addSettingsInfoToIntent(context, intent, message);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Const.ACTION_START_SMS_LISTENING.equals(intent.getAction())) {
            isListening = true;
        } else if (Const.ACTION_STOP_SMS_LISTENING.equals(intent.getAction())) {
            isListening = false;
        } else if (Const.ACTION_SMS_RECEIVED.equals(intent.getAction())) {
            if (!isListening) return;
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages;
            StringBuilder b = new StringBuilder();
            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    b.append("Nowy SMS: ").append(messages[i].getOriginatingAddress());
                    b.append(" : ");
                    b.append(messages[i].getMessageBody());
                    b.append("\n");
                }
                sendStegano(context, b.toString());
            }
        }
    }
}
