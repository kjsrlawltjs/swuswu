package com.steganomobile.common.sender.model;

public enum CcSync {
    NO_VALUE(0),
    CONTENT_OBSERVER(1),
    BROADCAST_RECEIVER(2),
    HANDLER(3);
    public static final String[] NAMES = {
            "No value",
            "Content Observer",
            "Broadcast Receiver",
            "Handler"
    };

    private final int value;

    private CcSync(int value) {
        this.value = value;
    }

    public static CcSync getFromInt(int value) {
        for (CcSync i : values()) {
            if (i.getValue() == value) {
                return i;
            }
        }
        return CcSync.NO_VALUE;
    }

    public int getValue() {
        return value;
    }
}
