package com.steganomobile.common.sender.model;

public enum CcSegment {
    NO_VALUE(-1),
    ONE_BIT(1),
    TWO_BIT(2),
    THREE_BIT(3),
    FOUR_BIT(4),
    EIGHT_BIT(8);

    private final int value;

    private CcSegment(int value) {
        this.value = value;
    }

    public static CcSegment getFromInt(int value) {
        for (CcSegment i : values()) {
            if (i.getValue() == value) {
                return i;
            }
        }
        return CcSegment.NO_VALUE;
    }

    public int getValue() {
        return value;
    }
}
