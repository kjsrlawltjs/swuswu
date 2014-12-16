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
        return printVertical(": ", true);
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

    public String printVertical(String sep, boolean header) {
        String startV = header ? "Start date" + sep + "%s\n" : "%s\n";
        String finishV = header ? "Finish date" + sep + "%s\n" : "%s\n";
        String durationV = header ? "Duration [ms]" + sep + "%d\n" : "%d\n";
        return String.format(startV + finishV + durationV, start, finish, duration);
    }

    public String printHorizontalHeader(String sep) {
        return sep + "Start date" + sep + "Finish date" + sep + "Duration [ms]";
    }

    public String printHorizontalFormat(String sep) {
        return sep + start + sep + finish + sep + duration;
    }
}