package com.steganomobile.common.sender.model;

import android.media.AudioManager;

public enum Cc {
    NO_VALUE(0, CcSegment.NO_VALUE),
    VOLUME_MUSIC(1, CcSegment.FOUR_BIT, AudioManager.STREAM_MUSIC),
    VOLUME_RING(2, CcSegment.THREE_BIT, AudioManager.STREAM_RING),
    VOLUME_NOTIFICATION(3, CcSegment.THREE_BIT, AudioManager.STREAM_NOTIFICATION),
    VOLUME_DTMF(4, CcSegment.THREE_BIT, AudioManager.STREAM_DTMF),
    VOLUME_SYSTEM(5, CcSegment.THREE_BIT, AudioManager.STREAM_SYSTEM),
    VOLUME_ALARM(6, CcSegment.THREE_BIT, AudioManager.STREAM_ALARM),
    VOLUME_VOICE_CALL(7, CcSegment.TWO_BIT, AudioManager.STREAM_VOICE_CALL),
    FILE_LOCK(8, CcSegment.ONE_BIT),
    FILE_SIZE(9, CcSegment.ONE_BYTE),
    FILE_EXISTENCE(10, CcSegment.ONE_BIT),
    CONTENT_OF_URI(11, CcSegment.ONE_BYTE),
    TYPE_OF_INTENT(12, CcSegment.ONE_BYTE),
    UNIX_SOCKET_DISCOVERY(13, CcSegment.ONE_BYTE),
    MEMORY_LOAD(14, CcSegment.ONE_BYTE),
    SYSTEM_LOAD(15, CcSegment.ONE_BIT),
    USAGE_TREND(16, CcSegment.ONE_BIT);

    public static final String[] NAMES = {
            "No method",
            "Volume Settings - Music (4 bit)",
            "Volume Settings - Ring (3 bit)",
            "Volume Settings - Notification (3 bit)",
            "Volume Settings - DTMF (3 bit)",
            "Volume Settings - System (3 bit)",
            "Volume Settings - Alarm (3 bit)",
            "Volume Settings - Voice Call (2 bit)",
            "File Lock (1 bit)",
            "File Size (8 bit)",
            "File Existence (1 bit)",
            "Content of Uri (8 bit)",
            "Type of Intent (8 bit)",
            "Unix Socket Discovery (8 bit)",
            "Memory Load (8 bit)",
            "System Load (1 bit)",
            "Usage Trend (1 bit)"
    };

    public static final String[] FILE_NAMES = {
            "no_method",
            "volume_music",
            "volume_ring",
            "volume_notification",
            "volume_dtmf",
            "volume_system",
            "volume_alarm",
            "volume_voice_call",
            "file_lock",
            "file_size",
            "file_existence",
            "content_of_uri",
            "type_of_intent",
            "unix_socket_discovery",
            "memory_load",
            "system_load",
            "usage_trend"
    };

    private static final int NO_VALUE_HERE = -1;
    private final int value;
    private final CcSegment segment;
    private final int stream;

    private Cc(int value, CcSegment segment, int stream) {
        this.value = value;
        this.segment = segment;
        this.stream = stream;
    }

    private Cc(int value, CcSegment segment) {
        this.value = value;
        this.segment = segment;
        this.stream = NO_VALUE_HERE;
    }

    public static Cc getFromInt(int value) {
        for (Cc i : values()) {
            if (i.getValue() == value) {
                return i;
            }
        }
        return Cc.NO_VALUE;
    }

    public final int getValue() {
        return value;
    }

    public CcSegment getSegment() {
        return segment;
    }

    public int getStream() {
        return stream;
    }
}
