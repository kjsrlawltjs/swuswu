package com.steganomobile.common.sender.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CcSenderItem implements Parcelable {

    public static Parcelable.Creator<CcSenderItem> CREATOR = new Parcelable.Creator<CcSenderItem>() {
        public CcSenderItem createFromParcel(Parcel parcel) {
            return new CcSenderItem(parcel);
        }

        public CcSenderItem[] newArray(int size) {
            return new CcSenderItem[size];
        }
    };

    private String data;
    private CcSenderInfo info;

    public CcSenderItem(String data, CcSenderInfo info) {
        this.data = data;
        this.info = info;
    }

    public CcSenderItem(CcSenderInfo info) {
        this.info = info;
    }

    public CcSenderItem(Parcel parcel) {
        data = parcel.readString();
        info = parcel.readParcelable(CcSenderInfo.class.getClassLoader());
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(data);
        parcel.writeParcelable(info, flags);
    }

    public CcSenderInfo getInfo() {
        return info;
    }
}
