package com.steganomobile.common.sender.model;

public enum CcStatus {
    NO_VALUE(-1),
    START(0),
    FINISH(1);

    private final int value;

    private CcStatus(int value) {
        this.value = value;
    }

    public static CcStatus getFromInt(int value) {
        for (CcStatus i : values()) {
            if (i.getValue() == value) {
                return i;
            }
        }
        return CcStatus.NO_VALUE;
    }

    public int getValue() {
        return value;
    }
}
