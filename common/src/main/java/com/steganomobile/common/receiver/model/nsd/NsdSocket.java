package com.steganomobile.common.receiver.model.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class NsdSocket implements BaseColumns, Parcelable {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static Parcelable.Creator<NsdSocket> CREATOR = new Parcelable.Creator<NsdSocket>() {
        public NsdSocket createFromParcel(Parcel parcel) {
            return new NsdSocket(parcel);
        }

        public NsdSocket[] newArray(int size) {
            return new NsdSocket[size];
        }
    };
    private String host = "localhost";
    private int port;

    public NsdSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public NsdSocket(int port) {
        this.port = port;
    }

    public NsdSocket(Parcel parcel) {
        host = parcel.readString();
        port = parcel.readInt();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        String format = "%s:%d";
        return String.format(format, host, port);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(host);
        parcel.writeInt(port);
    }
}
