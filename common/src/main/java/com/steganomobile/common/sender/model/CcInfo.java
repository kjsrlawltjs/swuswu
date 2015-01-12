package com.steganomobile.common.sender.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class CcInfo implements Parcelable, BaseColumns {

    public static final String INTERVAL = "interval";
    public static final String ITERATIONS = "iterations";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String SYNC = "sync";

    private static final int NO_VALUE = -1;
    private int iterations = NO_VALUE;
    private int interval = NO_VALUE;
    private int port = NO_VALUE;



    public static Parcelable.Creator<CcInfo> CREATOR = new Parcelable.Creator<CcInfo>() {
        public CcInfo createFromParcel(Parcel parcel) {
            return new CcInfo(parcel);
        }

        public CcInfo[] newArray(int size) {
            return new CcInfo[size];
        }
    };

    private CcType type = CcType.NO_VALUE;
    private CcMethod name = CcMethod.NO_VALUE;
    private CcStatus status = CcStatus.NO_VALUE;
    private CcSync sync = CcSync.NO_VALUE;

    public CcInfo(CcStatus status, CcMethod name, int iterations, CcType type, int interval, CcSync sync) {
        this.status = status;
        this.name = name;
        this.iterations = iterations;
        this.type = type;
        this.interval = interval;
        this.sync = sync;
        this.port = NO_VALUE;
    }

    public CcInfo(Parcel parcel) {
        interval = parcel.readInt();
        iterations = parcel.readInt();
        name = CcMethod.getFromInt(parcel.readInt());
        port = parcel.readInt();
        status = CcStatus.getFromInt(parcel.readInt());
        sync = CcSync.getFromInt(parcel.readInt());
        type = CcType.getFromInt(parcel.readInt());
    }

    public CcInfo(CcStatus status, CcSync sync) {
        this.status = status;
        this.sync = sync;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(interval);
        parcel.writeInt(iterations);
        parcel.writeInt(name.getValue());
        parcel.writeInt(port);
        parcel.writeInt(status.getValue());
        parcel.writeInt(sync.getValue());
        parcel.writeInt(type.getValue());
    }

    @Override
    public String toString() {
        return printVertical(": ", true);
    }

    public CcStatus getStatus() {
        return status;
    }

    public CcMethod getName() {
        return name;
    }

    public int getInterval() {
        return interval;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public CcType getType() {
        return type;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public CcSync getSync() {
        return sync;
    }

    public void setSync(CcSync sync) {
        this.sync = sync;
    }

    public String printHorizontalHeader(String sep) {
        return sep + "Name" + sep + "Type" + sep + "Sync" + sep + "Iterations" + sep
                + "Interval [ms]";
    }

    public String printHorizontalFormat(String sep) {
        return sep + CcMethod.NAMES[name.getValue()] + sep + CcType.NAMES[type.getValue()]
                + sep + CcSync.NAMES[sync.getValue()] + sep + iterations + sep + interval;
    }

    public String printVertical(String sep, boolean header) {
        String nameV = header ? "Name" + sep + "%s\n" : "%s\n";
        String typeV = header ? "Type" + sep + "%s\n" : "%s\n";
        String syncV = header ? "Sync" + sep + "%s\n" : "%s\n";
        String iterationsV = header ? "Iterations" + sep + "%d\n" : "%d\n";
        String intervalV = header ? "Interval [ms]" + sep + "%d\n" : "%d\n";

        return String.format(nameV + typeV + syncV + iterationsV + intervalV,
                CcMethod.NAMES[name.getValue()], CcType.NAMES[type.getValue()],
                CcSync.NAMES[sync.getValue()], iterations, interval);
    }

}
