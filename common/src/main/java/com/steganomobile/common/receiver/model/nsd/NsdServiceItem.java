package com.steganomobile.common.receiver.model.nsd;

import android.os.Parcel;
import android.os.Parcelable;

public class NsdServiceItem implements Parcelable {

    public static Parcelable.Creator<NsdServiceItem> CREATOR = new Parcelable.Creator<NsdServiceItem>() {
        public NsdServiceItem createFromParcel(Parcel parcel) {
            return new NsdServiceItem(parcel);
        }

        public NsdServiceItem[] newArray(int size) {
            return new NsdServiceItem[size];
        }
    };
    private NsdSocket socket;
    private boolean isClientChecked;
    private boolean isServerChecked;
    private String serviceName;

    public NsdServiceItem(NsdSocket socket, boolean isClientChecked, boolean isServerChecked, String serviceName) {
        this.socket = socket;
        this.isClientChecked = isClientChecked;
        this.isServerChecked = isServerChecked;
        this.serviceName = serviceName;
    }

    public NsdServiceItem(Parcel parcel) {
        boolean[] isChecked = new boolean[2];
        parcel.readBooleanArray(isChecked);
        isClientChecked = isChecked[0];
        isServerChecked = isChecked[1];
        serviceName = parcel.readString();
        socket = parcel.readParcelable(NsdSocket.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        boolean[] isChecked = {isClientChecked, isServerChecked};
        parcel.writeBooleanArray(isChecked);
        parcel.writeString(serviceName);
        parcel.writeParcelable(socket, flags);
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isClientChecked() {
        return isClientChecked;
    }

    public boolean isServerChecked() {
        return isServerChecked;
    }

    public NsdSocket getSocket() {
        return socket;
    }
}
