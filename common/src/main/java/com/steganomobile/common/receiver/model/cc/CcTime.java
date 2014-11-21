package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class CcTime implements BaseColumns, Parcelable {

    public static final String START = "start";
    public static final String FINISH = "finish";
    public static final String DURATION = "duration";

    public static Creator<CcTime> CREATOR = new Creator<CcTime>() {
        public CcTime createFromParcel(Parcel parcel) {
            return new CcTime(parcel);
        }

        public CcTime[] newArray(int size) {
            return new CcTime[size];
        }
    };

    private String finish;
    private String start;
    private long duration;

    public CcTime(String finish, String start, long duration) {
        this.finish = finish;
        this.start = start;
        this.duration = duration;
    }

    public CcTime(Parcel parcel) {
        duration = parcel.readLong();
        finish = parcel.readString();
        start = parcel.readString();
    }

    public String getFinish() {
        return finish;
    }

    public String getStart() {
        return start;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        String format = "Start date: %s\nFinish date: %s\nDuration: %d [s]\n";
        return String.format(format, start, finish, duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(duration);
        parcel.writeString(finish);
        parcel.writeString(start);
    }
}
