package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Locale;

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
    private CcTime time;

    public CcMessage(long size, String data, long correct, CcTime time) {
        this.size = size;
        this.data = data;
        this.correct = correct;
        this.time = time;
    }

    public CcMessage(Parcel parcel) {
        correct = parcel.readLong();
        data = parcel.readString();
        size = parcel.readLong();
        time = parcel.readParcelable(CcTime.class.getClassLoader());
    }

    public long getSize() {
        return size;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return printVertical(": ", true);
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
        parcel.writeParcelable(time, flags);
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

    public String printHorizontalHeader(String sep) {
        return sep + "Size [b]" + sep + "Bit rate [b/s]" + sep + "Accuracy [%]"
                + time.printHorizontalHeader(sep) + sep + "Data";
    }

    public String printHorizontalFormat(String sep) {
        return sep + size + sep + getBitRate() + sep + getPercentAccuracy()
                + time.printHorizontalFormat(sep) + sep + data;
    }

    public String printVertical(String sep, boolean header) {

        String sizeV = header ? "Size [b]" + sep + "%d\n" : "%d\n";
        String bitRateV = header ? "Bit rate [b/s]" + sep + "%.3f\n" : "%.3f\n";
        String accuracyV = header ? "Accuracy [%%]" + sep + "%.3f\n" : "%.3f\n";
        String dataV = header ? "Data" + sep + "%s\n" : "%s\n";

        return String.format(Locale.US, sizeV + bitRateV + accuracyV + "%s" + dataV, size,
                getBitRate(), getPercentAccuracy(), time.printVertical(sep, header), data);
    }

    public double getBitRate() {
        return (double) size * 1000 / time.getDuration();
    }

    public CcTime getTime() {
        return time;
    }
}
