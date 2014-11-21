package com.steganomobile.common.receiver.model.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class NsdItem extends NsdBaseItem implements BaseColumns, Parcelable {

    public static final String TABLE_NAME = "nsd";
    public static final String ID = "_id";
    public static final String IS_PRESENT = "is_present";
    public static final String SERVICE_NAME = "service_name";
    public static Parcelable.Creator<NsdItem> CREATOR = new Parcelable.Creator<NsdItem>() {
        public NsdItem createFromParcel(Parcel parcel) {
            return new NsdItem(parcel);
        }

        public NsdItem[] newArray(int size) {
            return new NsdItem[size];
        }
    };
    private boolean isPresent;
    private String serviceName;
    private long id;

    public NsdItem(long id, NsdSocket socket, boolean isPresent, String serviceName) {
        super(socket);
        this.id = id;
        this.isPresent = isPresent;
        this.serviceName = serviceName;
    }

    public NsdItem(Parcel parcel) {
        super((NsdBaseItem) parcel.readParcelable(NsdBaseItem.class.getClassLoader()));
        id = parcel.readLong();
        boolean[] isPresents = new boolean[1];
        parcel.readBooleanArray(isPresents);
        isPresent = isPresents[0];
    }

    @Override
    public String toString() {
        String isOn = isPresent ? "ON" : "OFF";
        String format = "%s - %s : %s\n";
        return String.format(format, getSocket(), isOn, serviceName);
    }

    public boolean isPresent() {
        return isPresent;
    }

    public String getServiceName() {
        return serviceName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        NsdBaseItem baseItem = super.getNsdBaseItem();
        parcel.writeParcelable(baseItem, flags);
        parcel.writeLong(id);
        boolean[] isPresents = {isPresent};
        parcel.writeBooleanArray(isPresents);
    }
}