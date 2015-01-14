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

    // Used to ask to send a subpart of the message for each iteration
    // for example if the message is ABCDEF and there is 3 iteration to do
    // then for iteration 1, send AB then for iteration 2 send CD, and then EF.
    private int sendsubparts = 0;

    private int currentsubpart = -1; // 1 to N

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
        if (sendsubparts == 0)
            return data;
        else {
            // There is sendsubparts XP to perform
            int chunk_length = data.length() / sendsubparts;
            return data.substring(chunk_length * (currentsubpart - 1), chunk_length * (currentsubpart));
        }

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

    public void setSendsubparts(int sendsubparts) {
        this.sendsubparts = sendsubparts;
    }

    public void setCurrentsubpart(int currentsubpart) {
        this.currentsubpart = currentsubpart;
    }
}
