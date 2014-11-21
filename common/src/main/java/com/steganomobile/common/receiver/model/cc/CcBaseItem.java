package com.steganomobile.common.receiver.model.cc;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcType;

public class CcBaseItem implements BaseColumns, Parcelable {

    private static final String TAG = CcBaseItem.class.getSimpleName();
    public static Parcelable.Creator<CcBaseItem> CREATOR = new Parcelable.Creator<CcBaseItem>() {
        public CcBaseItem createFromParcel(Parcel parcel) {
            return new CcBaseItem(parcel);
        }

        public CcBaseItem[] newArray(int size) {
            return new CcBaseItem[size];
        }
    };
    private int typeId;
    private int nameId;

    public CcBaseItem(int nameId, int typeId) {
        this.nameId = nameId;
        this.typeId = typeId;
    }

    public CcBaseItem(Parcel parcel) {
        nameId = parcel.readInt();
        typeId = parcel.readInt();
    }

    public CcBaseItem(CcBaseItem item) {
        nameId = item.getNameId();
        typeId = item.getTypeId();
    }

    @Override
    public String toString() {
        String format = "%s\n Type: %s";
        return String.format(format, CcMethod.NAMES[nameId], CcType.NAMES[typeId]);
    }

    public int getTypeId() {
        return typeId;
    }

    public int getNameId() {
        return nameId;
    }

    public CcBaseItem getCcBaseItem() {
        return new CcBaseItem(nameId, typeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(nameId);
        parcel.writeInt(typeId);
    }
}
