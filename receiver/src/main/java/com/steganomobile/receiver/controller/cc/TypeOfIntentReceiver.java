package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

public class TypeOfIntentReceiver extends CcImplReceiver {
    private static final String TAG = TypeOfIntentReceiver.class.getSimpleName();

    public TypeOfIntentReceiver(DataCollector collector) {
        super(collector);
    }

    @Override
    public void onReceive(String action) {
        if (Const.NO_ACTION.equals(action)) {
            return;
        }

        if (action != null) {
            String stringByte = action.substring(Const.SYNC_RECEIVER.length());
            byte data = Byte.parseByte(stringByte);
            getCollector().setData(data);
        }
    }
}
