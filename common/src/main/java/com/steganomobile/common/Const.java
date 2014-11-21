package com.steganomobile.common;

import android.net.Uri;

public class Const {

    // ---------------------------------------------------------------------------------------------
    //  Settings - KEYS
    // ---------------------------------------------------------------------------------------------
    public static final String PREF_KEY_PORT = "pref_key_unix_socket_port";
    public static final String PREF_KEY_INTERVAL = "pref_key_wait_time";
    public static final String PREF_KEY_ITERATIONS = "pref_key_test_iterations";
    public static final String PREF_KEY_METHODS = "pref_key_list_methods";
    public static final String PREF_KEY_CATEGORY_METHODS = "pref_key_category_methods";
    public static final String PREF_KEY_CATEGORY_GLOBAL = "pref_key_category_global";
    public static final String PREF_KEY_TYPE = "pref_key_type";
    public static final String PREF_KEY_SYNC = "pref_key_sync";
    // ---------------------------------------------------------------------------------------------
    //  Default values
    // ---------------------------------------------------------------------------------------------
    public static final String DEFAULT_PORT = "6666";
    public static final String DEFAULT_INTERVAL = "4";
    public static final String DEFAULT_SYNC = "1";
    public static final String DEFAULT_ITERATIONS = "1";
    public static final String DEFAULT_METHOD = "1";
    public static final String DEFAULT_TYPE = "1";
    public static final String DEFAULT_METHODS = "1";
    public static final long DEFAULT_USAGE_DIVIDER = 25;
    // ---------------------------------------------------------------------------------------------
    //  For sharing file
    // ---------------------------------------------------------------------------------------------
    public static final String SHARED_FILE = "stegano.xyz";
    public static final String READ_WRITE = "rw";
    // ---------------------------------------------------------------------------------------------
    //  For sending result to activity
    // ---------------------------------------------------------------------------------------------
    public static final String ACTION_FINISH_RECEIVER_CC = "ACTION_FINISH_RECEIVER_CC";
    public static final String ACTION_FINISH_RECEIVER_NSD = "ACTION_FINISH_RECEIVER_NSD";
    public static final String ACTION_INFO = "ACTION_INFO";
    public static final String ACTION_START_SOCKET = "ACTION_START_SOCKET";
    public static final String ACTION_START_PRESENCE = "ACTION_START_PRESENCE";
    public static final String ACTION_STOP_ANALYSER = "ACTION_STOP_ANALYSER";
    public static final String ACTION_START_SCENARIO = "ACTION_START_SCENARIO";
    public static final String ACTION_START_SENDER_CC = "ACTION_START_SENDER_CC";
    public static final String ACTION_START_STEGANO = "ACTION_START_STEGANO";
    public static final String ACTION_START_RECEIVER_CC = "ACTION_START_RECEIVER_CC";
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    // ---------------------------------------------------------------------------------------------
    //  Other
    // ---------------------------------------------------------------------------------------------
    public static final String PACKAGE_STEGANO_SENDER = "com.steganomobile.sender";
    public static final String PACKAGE_STEGANO_RECEIVER = "com.steganomobile.receiver";
    public static final String PACKAGE_STEGANO_ANALYSER = "com.steganomobile.analyser";
    // Request for activity
    public static final byte REQUEST_SETTINGS = 0;
    // UP - 1, DOWN - 0
    public static final byte UP_NUMBER = 1;
    public static final byte DOWN_NUMBER = 0;
    // Intents
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_INTERVAL = "EXTRA_INTERVAL";
    public static final String EXTRA_ITERATIONS = "EXTRA_TIME_EXTRA_TEST_ITERATIONS";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_CC_NAME_ID = "EXTRA_NAME";
    public static final String EXTRA_CC_INFO = "EXTRA_CC_INFO";
    public static final String EXTRA_SCENARIO = "EXTRA_SCENARIO";
    // Scenarios
    public static final int SCENARIO_1 = 1;
    public static final int SCENARIO_2 = 2;
    public static final int SCENARIO_3 = 3;
    public static final int SCENARIO_4 = 4;
    public static final int SCENARIO_5 = 5;
    public static final int SCENARIO_6 = 6;
    public static final int SCENARIO_7 = 7;
    public static final int SCENARIO_8 = 8;
    public static final int SCENARIO_9 = 9;
    public static final int SCENARIO_10 = 10;
    public static final int SCENARIO_11 = 11;
    public static final int SCENARIO_12 = 12;
    public static final int SCENARIO_13 = 13;
    public static final int SCENARIO_14 = 14;
    public static final int SCENARIO_NOT_FOUND = -1;

    public static final int CSV_PAIRS_ALL = 0;
    public static final int CSV_INTERVALS_ALL = 100;
    public static final int CSV_PROCESSES_ALL = 200;
    public static final String CSV_EMPTY = "Empty";
    public static final String EXTRA_ITEM_RECEIVER_CC = "EXTRA_ITEM_RECEIVER_CC";
    public static final String EXTRA_NSD_ITEM = "EXTRA_NSD_ITEM";
    public static final String EXTRA_ITEM_SENDER_CC = "EXTRA_ITEM_SENDER_CC";
    public static final String ACTION_START_SMS_LISTENING = "ACTION_START_SMS_LISTENING";
    public static final String ACTION_STOP_SMS_LISTENING = "ACTION_STOP_SMS_LISTENING";
    public static final String EXTRA_NSD_SERVICE_ITEM = "EXTRA_NSD_SERVICE_ITEM";
    public static final String SYNC_RECEIVER = "SYNC_RECEIVER";
    public static final Uri SYNC_OBSERVER = Uri.parse("content://stegano_sync");
    public static final String NO_ACTION = "NO_ACTION";
    public static final long SYNC_WAIT = 1000;
    public static final String EXTRA_SENT_ELEMENT = "EXTRA_SENT_ELEMENT";
}
