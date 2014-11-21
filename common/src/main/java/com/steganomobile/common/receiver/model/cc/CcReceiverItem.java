package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.steganomobile.common.sender.model.CcInfo;

public class CcReceiverItem extends CcBaseItem implements BaseColumns, Parcelable {

    public static final String TABLE_NAME = "cc";
    public static final String ID = "_id";

    public static Creator<CcReceiverItem> CREATOR = new Creator<CcReceiverItem>() {
        public CcReceiverItem createFromParcel(Parcel parcel) {
            return new CcReceiverItem(parcel);
        }

        public CcReceiverItem[] newArray(int size) {
            return new CcReceiverItem[size];
        }
    };

    private long id;
    private CcMessage message;
    private CcTime time;
    private CcInfo info;

    public CcReceiverItem(long id, CcMessage message, CcTime time, CcInfo info) {
        super(info.getName().getValue(), info.getType().getValue());
        this.id = id;
        this.message = message;
        this.time = time;
        this.info = info;
    }

    public CcReceiverItem(Parcel parcel) {
        super((CcBaseItem) parcel.readParcelable(CcBaseItem.class.getClassLoader()));
        id = parcel.readLong();
        message = parcel.readParcelable(CcMessage.class.getClassLoader());
        info = parcel.readParcelable(CcInfo.class.getClassLoader());
        time = parcel.readParcelable(CcTime.class.getClassLoader());
    }

    @Override
    public String toString() {
        final double bitRate = (double) message.getSize() * 1000 / time.getDuration();
        String format = "%s%sBit rate: %f [b/s]\n%s\n";
        return String.format(format, info, time, bitRate, message);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CcMessage getMessage() {
        return message;
    }

    public CcTime getTime() {
        return time;
    }

    public CcInfo getInfo() {
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(new CcBaseItem(info.getName().getValue(), info.getType().getValue()), flags);
        parcel.writeLong(id);
        parcel.writeParcelable(message, flags);
        parcel.writeParcelable(info, flags);
        parcel.writeParcelable(time, flags);
    }
}