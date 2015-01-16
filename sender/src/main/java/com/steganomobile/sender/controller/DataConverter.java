package com.steganomobile.sender.controller;

import com.steganomobile.common.sender.model.CcSegment;

import java.util.ArrayList;

public class DataConverter {

    private static final String TAG = DataConverter.class.getSimpleName();

    public static Byte[] getData(String data, CcSegment segment) {
        switch (segment) {
            case ONE_BIT:
                return optionOneBitSegment(data.getBytes());
            case TWO_BIT:
                return optionTwoBitSegment(data.getBytes());
            case THREE_BIT:
                return optionThreeBitSegment(data.getBytes());
            case FOUR_BIT:
                return optionFourBitSegment(data.getBytes());
            case ONE_BYTE:
                return optionEightBitSegment(data.getBytes());
            default:
                return null;
        }
    }

    private static Byte[] optionEightBitSegment(byte[] bytes) {
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    private static Byte[] optionOneBitSegment(byte[] bytes) {
        ArrayList<Byte> temp = new ArrayList<Byte>();
        for (byte myByte : bytes) {
            // (Nxxx xxxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            byte number = (byte) ((myByte >> 7) & 1);
            temp.add(number);
            // (xNxx xxxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 6) & 1);
            temp.add(number);
            // (xxNx xxxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 5) & 1);
            temp.add(number);
            // (xxxN xxxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 4) & 1);
            temp.add(number);
            // (xxxx Nxxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 3) & 1);
            temp.add(number);
            // (xxxx xNxx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 2) & 1);
            temp.add(number);
            // (xxxx xxNx -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte >> 1) & 1);
            temp.add(number);
            // (xxxx xxxN -> xxxx xxxN) & 0000 0001 -> N&1 = number
            number = (byte) ((myByte) & 1);
            temp.add(number);
        }
        Byte[] result = new Byte[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }

    private static Byte[] optionTwoBitSegment(byte[] bytes) {
        ArrayList<Byte> temp = new ArrayList<Byte>();
        for (byte myByte : bytes) {
            // (NNxx xxxx -> xxxx xxNN) & 0000 0011 -> NN&11 = number
            byte number = (byte) ((myByte >> 6) & 3);
            temp.add(number);
            // (xxNN xxxx -> xxxx xxNN) & 0000 0011 -> NN&11 = number
            number = (byte) ((myByte >> 4) & 3);
            temp.add(number);
            // (xxxx NNxx -> xxxx xxNN) & 0000 0011 -> NN&11 = number
            number = (byte) ((myByte >> 2) & 3);
            temp.add(number);
            // (xxxx xxNN -> xxxx xxNN) & 0000 0011 -> NN&11 = number
            number = (byte) (myByte & 3);
            temp.add(number);
        }
        Byte[] result = new Byte[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }

    private static Byte[] optionThreeBitSegment(byte[] bytes) {
        ArrayList<Byte> temp = new ArrayList<Byte>();
        int iteration = 0;
        byte remainder = 0;
        for (byte myByte : bytes) {
            if (iteration % 3 == 0) {
                // (NNNx xxxx -> xxxx xNNN) & 0000 0111 -> NNN&111 = number
                byte number = (byte) ((myByte >> 5) & 7);
                temp.add(number);
                // (xxxN NNxx -> xxxx xNNN) & 0000 0111 -> NNN&111 = number
                number = (byte) ((myByte >> 2) & 7);
                temp.add(number);
                // xxxx xxNN & 011 = remainder
                remainder = (byte) (myByte & 3);
            } else if (iteration % 3 == 1) {
                // (Nxxx xxxx -> xxxx xxxN) & 0000 0001 -> (xxN&001 | NNx) = number
                byte number = (byte) (((myByte >> 7) & 1) | (remainder << 1));
                temp.add(number);
                // (xNNN xxxx -> xxxx xNNN) & 0000 0111 -> NNN&111 = number
                number = (byte) ((myByte >> 4) & 7);
                temp.add(number);
                // (xxxx NNNx -> xxxx xNNN) & 0000 0111 -> NNN&111 = number
                number = (byte) ((myByte >> 1) & 7);
                temp.add(number);
                // xxxx xxxN & 001 = remainder
                remainder = (byte) (myByte & 1);
            } else {
                // (NNxx xxxx -> xxxx xxNN) & 0000 0011 -> (xNN&011 | Nxx) = number
                byte number = (byte) (((myByte >> 6) & 3) | (remainder << 2));
                temp.add(number);
                // xxNN Nxxx -> xxxx xNNN) & 0000 0111 -> NNN&111 = number
                number = (byte) ((myByte >> 3) & 7);
                temp.add(number);
                // xxxx xNNN & 0000 0111 = number
                number = (byte) (myByte & 7);
                // Remainder unnecessary
                remainder = 0;
                temp.add(number);
            }
            iteration++;
        }
        if (remainder != 0) {
            if (iteration % 3 == 1) {
                // xNN -> NNx
                remainder = (byte) (remainder << 1);
            } else if (iteration % 3 == 2) {
                // xxN -> Nxx
                remainder = (byte) (remainder << 2);
            }
            temp.add(remainder);
        }
        Byte[] result = new Byte[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }

    private static Byte[] optionFourBitSegment(byte[] bytes) {
        ArrayList<Byte> temp = new ArrayList<Byte>();
        for (byte myByte : bytes) {
            // (NNNN xxxx -> xxxx NNNN) & 0000 1111 -> NNNN&1111 = number
            byte number = (byte) ((myByte >> 4) & (byte) 0x0F);
            temp.add(number);
            // xxxx NNNN & 0000 1111 = number
            number = (byte) (myByte & 0x0F);
            temp.add(number);
        }
        Byte[] result = new Byte[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }
}