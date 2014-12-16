package com.steganomobile.receiver.controller.cc;

import com.steganomobile.common.Const;
import com.steganomobile.receiver.controller.DataCollector;

public class TypeOfIntent extends CcImpl {
    private static final String TAG = TypeOfIntent.class.getSimpleName();

    public TypeOfIntent(DataCollector collector) {
        super(collector);
    }

    @Override
    public void runCc(String action) {
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
