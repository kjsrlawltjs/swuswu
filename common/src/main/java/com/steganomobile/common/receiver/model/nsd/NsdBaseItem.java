package com.steganomobile.common.receiver.model.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class NsdBaseItem implements BaseColumns, Parcelable {

    public static Parcelable.Creator<NsdBaseItem> CREATOR = new Parcelable.Creator<NsdBaseItem>() {
        public NsdBaseItem createFromParcel(Parcel parcel) {
            return new NsdBaseItem(parcel);
        }

        public NsdBaseItem[] newArray(int size) {
            return new NsdBaseItem[size];
        }
    };
    private final NsdSocket socket;

    public NsdBaseItem(NsdSocket socket) {
        this.socket = socket;
    }

    public NsdBaseItem(Parcel parcel) {
        socket = parcel.readParcelable(NsdSocket.class.getClassLoader());
    }

    public NsdBaseItem(NsdBaseItem nsdBaseItem) {
        socket = nsdBaseItem.getSocket();
    }

    @Override
    public String toString() {
        return socket.toString();
    }

    public NsdSocket getSocket() {
        return socket;
    }

    public NsdBaseItem getNsdBaseItem() {
        return new NsdBaseItem(socket);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(socket, flags);
    }
}
