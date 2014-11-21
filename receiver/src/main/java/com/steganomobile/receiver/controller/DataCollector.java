package com.steganomobile.receiver.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.receiver.model.cc.CcMessage;
import com.steganomobile.common.receiver.model.cc.CcReceiverItem;
import com.steganomobile.common.receiver.model.cc.CcTime;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcSegment;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;
import com.steganomobile.receiver.db.ReceiverDatabase;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DataCollector {

    private static final String TAG = DataCollector.class.getSimpleName();
    private byte bit0;
    private byte bit1;
    private byte bit2;
    private byte bit3;
    private byte bit4;
    private byte bit5;
    private byte bit6;
    private byte remainder;
    private int iteration;
    private ArrayList<Byte> bytes = new ArrayList<Byte>();
    private ArrayList<Byte> receivedElements = new ArrayList<Byte>();
    private ArrayList<Byte> sentElements = new ArrayList<Byte>();
    private StringBuilder data = new StringBuilder();
    private long size;
    private long start = 0;
    private CcInfo info;

    public DataCollector(CcInfo info) {
        this.info = info;
    }

    public void setSentElement(byte element) {
        sentElements.add(element);
    }

    public void setData(byte number) {

        Log.v(TAG, "Received " + number);

        receivedElements.add(number);

        if (size == 0) {
            start = System.currentTimeMillis();
        }

        if (info.getName().getSegment() == CcSegment.ONE_BIT) {
            optionOneBitSegment(number);

        } else if (info.getName().getSegment() == CcSegment.TWO_BIT) {
            optionTwoBitSegment(number);

        } else if (info.getName().getSegment() == CcSegment.THREE_BIT) {
            optionThreeBitSegment(number);

        } else if (info.getName().getSegment() == CcSegment.FOUR_BIT) {
            optionFourBitSegment(number);

        } else if (info.getName().getSegment() == CcSegment.EIGHT_BIT) {
            optionEightBitSegment(number);
        }
    }

    public void setData(String data) {
        if (this.data.length() == 0) {
            start = System.currentTimeMillis();
        }
        this.data.append(data);
    }

    private void optionOneBitSegment(byte number) {
        // Log.v(TAG, "One Bit" + number);
        if (iteration % 8 == 0) {
            bit0 = number;
            iteration++;
        } else if (iteration % 8 == 1) {
            bit1 = number;
            iteration++;
        } else if (iteration % 8 == 2) {
            bit2 = number;
            iteration++;
        } else if (iteration % 8 == 3) {
            bit3 = number;
            iteration++;
        } else if (iteration % 8 == 4) {
            bit4 = number;
            iteration++;
        } else if (iteration % 8 == 5) {
            bit5 = number;
            iteration++;
        } else if (iteration % 8 == 6) {
            bit6 = number;
            iteration++;
        } else {
            // Last case - Build byte
            // Nxxx xxxx |
            // xNxx xxxx |
            // xxNx xxxx |
            // xxxN xxxx |
            // xxxx Nxxx |
            // xxxx xNxx |
            // xxxx xxNx |
            // xxxx xxxN
            // =========
            // NNNN NNNN
            final Byte myByte = (byte) ((bit0 << 7)
                    | (bit1 << 6)
                    | (bit2 << 5)
                    | (bit3 << 4)
                    | (bit4 << 3)
                    | (bit5 << 2)
                    | (bit6 << 1)
                    | number);
            bytes.add(myByte);
            iteration++;
        }
        size++;
    }

    private void optionTwoBitSegment(byte number) {
        if (iteration % 4 == 0) {
            bit0 = number;
            iteration++;
        } else if (iteration % 4 == 1) {
            bit1 = number;
            iteration++;
        } else if (iteration % 4 == 2) {
            bit2 = number;
            iteration++;
        } else {
            bit3 = number;
            // NNxx xxxx |
            // xxNN xxxx |
            // xxxx NNxx |
            // xxxx xxNN |
            // =========
            // NNNN NNNN
            final Byte myByte = (byte) ((bit0 << 6)
                    | (bit1 << 4)
                    | (bit2 << 2)
                    | bit3);
            bytes.add(myByte);
            iteration++;
        }
        size += 2;
    }

    private void optionThreeBitSegment(byte number) {
        if (iteration % 3 == 0) {
            bit0 = number;
            iteration++;
        } else if (iteration % 3 == 1) {
            bit1 = number;
            iteration++;
            if (iteration % 8 == 0) {
                // NNxx xxxx |
                // xxNN Nxxx |
                // xxxx xNNN
                // =========
                // NNNN NNNN
                final Byte myByte = (byte) (remainder
                        | (bit0 << 3)
                        | bit1);
                remainder = 0;
                bytes.add(myByte);
                iteration = 0;
            }
        } else {
            bit2 = number;
            if (iteration % 8 == 2) {
                // NNNx xxxx |
                // xxxN NNxx |
                // xxxx xxNN
                // =========
                // NNNN NNNN
                final Byte myByte = (byte) ((bit0 << 5)
                        | (bit1 << 2)
                        | (bit2 >> 1));
                // Nxxx xxxx
                remainder = (byte) (bit2 << 7);
                bytes.add(myByte);
            } else if (iteration % 8 == 5) {
                // Nxxx xxxx |
                // xNNN xxxx |
                // xxxx NNNx |
                // xxxx xxxN
                // =========
                // NNNN NNNN
                final Byte myByte = (byte) (remainder
                        | (bit0 << 4)
                        | (bit1 << 1)
                        | bit2 >> 2);
                // NNxx xxxx
                remainder = (byte) (bit2 << 6);
                bytes.add(myByte);
            }
            iteration++;
        }
        size += 3;
    }

    private void optionFourBitSegment(byte number) {
        if (iteration % 2 == 0) {
            bit0 = number;
            iteration++;
        } else {
            bit1 = number;
            // NNNN xxxx |
            // xxxx NNNN |
            // =========
            // NNNN NNNN
            final Byte myByte = (byte) ((bit0 << 4) | bit1);
            bytes.add(myByte);
            iteration++;
        }
        size += 4;
    }

    private void optionEightBitSegment(byte number) {
        bytes.add(number);
        size += 8;
    }

    public void finish(Context context) {
        ReceiverDatabase database = new ReceiverDatabase(context);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        long now = System.currentTimeMillis();

        String startDate = dateFormat.format(new Time(start));
        String finishDate = dateFormat.format(Calendar.getInstance().getTime());

        Byte[] newBytes = new Byte[bytes.size()];
        bytes.toArray(newBytes);
        byte[] myBytes = new byte[newBytes.length];
        for (int i = 0; i < newBytes.length; i++) {
            myBytes[i] = newBytes[i];
        }
        data.append(new String(myBytes));

        CcTime time = new CcTime(finishDate, startDate, now - start);
        CcMessage message = new CcMessage(size, data.toString(), countCorrectData());
        CcReceiverItem ccReceiverItem = new CcReceiverItem(0, message, time, info);
        long id = database.addCcItem(ccReceiverItem);
        ccReceiverItem.setId(id);
        Intent intent = new Intent(Const.ACTION_FINISH_RECEIVER_CC);
        intent.putExtra(Const.EXTRA_ITEM_RECEIVER_CC, ccReceiverItem);
        context.sendBroadcast(intent);
        Methods.playSound(context);

        data.setLength(0);
        size = 0;
        start = 0;
    }

    private long countCorrectData() {
        long counter = 0;
        if (info.getSync() != CcSync.HANDLER) {
            int length = receivedElements.size() < sentElements.size() ?
                    receivedElements.size() : sentElements.size();
            for (int i = 0; i < length; i++) {
                byte mask = 0x01;
                for (int j = 0; j < info.getName().getSegment().getValue(); j++) {
                    boolean received = (receivedElements.get(i) & mask) != 0;
                    boolean sent = (sentElements.get(i) & mask) != 0;
                    if (received == sent) {
                        counter++;
                    }
                    mask <<= 1;
                }
            }
        }
        return counter;
    }

    private String getTypeName(CcType type) {
        return CcType.NAMES[type.getValue()];
    }

    private String getMethodName(CcMethod name) {
        return CcMethod.NAMES[name.getValue()];
    }

    public CcInfo getInfo() {
        return info;
    }
}
