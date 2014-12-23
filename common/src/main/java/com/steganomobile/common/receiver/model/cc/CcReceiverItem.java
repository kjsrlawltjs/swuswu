package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.steganomobile.common.sender.model.CcSenderInfo;

import java.util.Locale;

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
    private CcSenderInfo info;

    public CcReceiverItem(long id, CcMessage message, CcSenderInfo info) {
        super(info.getName().getValue(), info.getType().getValue());
        this.id = id;
        this.message = message;
        this.info = info;
    }

    public CcReceiverItem(Parcel parcel) {
        super((CcBaseItem) parcel.readParcelable(CcBaseItem.class.getClassLoader()));
        id = parcel.readLong();
        message = parcel.readParcelable(CcMessage.class.getClassLoader());
        info = parcel.readParcelable(CcSenderInfo.class.getClassLoader());
    }

    @Override
    public String toString() {
        return print(": ", true, false);
    }

    public String print(String sep, boolean header, boolean horizontal) {

        if (horizontal) {
            String dataS = info.printHorizontalFormat(sep) + message.printHorizontalFormat(sep);
            if (header) {
                String headerS = info.printHorizontalHeader(sep) + message.printHorizontalHeader(sep);
                return headerS + "\n" + dataS + "\n";
            }
            return dataS + "\n";
        }

        return String.format(Locale.US, "%s\n%s", info.printVertical(sep, header),
                message.printVertical(sep, header));
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

    public CcSenderInfo getInfo() {
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
    }
}