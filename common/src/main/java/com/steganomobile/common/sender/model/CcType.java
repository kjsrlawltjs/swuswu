package com.steganomobile.common.sender.model;

public enum CcType {
    NO_VALUE(0),
    PLAIN_TEXT(1),
    LOCATION(2),
    CELL_LOCATION(3),
    SMS(4),
    CONTACTS(5),
    IMEI(6),
    OPERATOR_NAME(8),
    FILE(9),
    NEW_SMS(10);

    public static final String[] NAMES = {
            "No type",
            "Plain Text",
            "Location",
            "Cell Location",
            "SMS",
            "Contacts",
            "IMEI",
            "Test - not used anymore",
            "Operator Name",
            "File",
            "New SMS"
    };

    private final int value;

    private CcType(int value) {
        this.value = value;
    }

    public static CcType getFromInt(int value) {
        for (CcType i : values()) {
            if (i.getValue() == value) {
                return i;
            }
        }
        return CcType.NO_VALUE;
    }

    public int getValue() {
        return value;
    }
}
