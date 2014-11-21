package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class CcMessage implements BaseColumns, Parcelable {

    public static final String SIZE = "size";
    public static final String CORRECT = "correct";
    public static final String DATA = "data";

    public static Parcelable.Creator<CcMessage> CREATOR = new Parcelable.Creator<CcMessage>() {
        public CcMessage createFromParcel(Parcel parcel) {
            return new CcMessage(parcel);
        }

        public CcMessage[] newArray(int size) {
            return new CcMessage[size];
        }
    };
    private long correct;
    private long size;
    private String data;

    public CcMessage(long size, String data, long correct) {
        this.size = size;
        this.data = data;
        this.correct = correct;
    }

    public CcMessage(Parcel parcel) {
        correct = parcel.readLong();
        data = parcel.readString();
        size = parcel.readLong();
    }

    public long getSize() {
        return size;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        String format =
                "Size: %d [b]\n" +
                        "Accuracy: %.3f%%\n" +
                        "Data: \n%s";
        return String.format(format, size, getPercentAccuracy(), data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(correct);
        parcel.writeString(data);
        parcel.writeLong(size);
    }

    public double getAccuracy() {
        return (double) correct / size;
    }

    public double getPercentAccuracy() {
        return (double) correct / size * 100;
    }

    public long getCorrect() {
        return correct;
    }
}
