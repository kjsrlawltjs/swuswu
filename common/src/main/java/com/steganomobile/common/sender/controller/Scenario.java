package com.steganomobile.common.sender.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.sender.model.Cc;
import com.steganomobile.common.sender.model.CcSenderInfo;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

import static com.steganomobile.common.Bank.MESSAGE_100;
import static com.steganomobile.common.Const.ACTION_START_SENDER_CC;
import static com.steganomobile.common.Const.ACTION_STOP_ANALYSER;
import static com.steganomobile.common.Const.SCENARIO_1;
import static com.steganomobile.common.Const.SCENARIO_10;
import static com.steganomobile.common.Const.SCENARIO_11;
import static com.steganomobile.common.Const.SCENARIO_12;
import static com.steganomobile.common.Const.SCENARIO_13;
import static com.steganomobile.common.Const.SCENARIO_14;
import static com.steganomobile.common.Const.SCENARIO_2;
import static com.steganomobile.common.Const.SCENARIO_3;
import static com.steganomobile.common.Const.SCENARIO_4;
import static com.steganomobile.common.Const.SCENARIO_5;
import static com.steganomobile.common.Const.SCENARIO_6;
import static com.steganomobile.common.Const.SCENARIO_7;
import static com.steganomobile.common.Const.SCENARIO_8;
import static com.steganomobile.common.Const.SCENARIO_9;

public class Scenario {
    private static final String TAG = Scenario.class.getSimpleName();

    public static void send(final int option, final Context context) {
        final Handler handler = new Handler();
        final Runnable callback = new Runnable() {
            public void run() {
                switch (option) {
                    case SCENARIO_1:
                        scenario1(context, handler, option);
                        break;
                    case SCENARIO_2:
                        scenario2(context, handler, option);
                        break;
                    case SCENARIO_3:
                        scenario3(context, handler, option);
                        break;
                    case SCENARIO_4:
                        scenario4(context, handler, option);
                        break;
                    case SCENARIO_5:
                        scenario5(context, handler, option);
                        break;
                    case SCENARIO_6:
                        scenario6(context, handler, option);
                        break;
                    case SCENARIO_7:
                        scenario7(context, handler, option);
                        break;
                    case SCENARIO_8:
                        scenario8(context, handler, option);
                        break;
                    case SCENARIO_9:
                        scenario9(context, handler, option);
                        break;
                    case SCENARIO_10:
                        scenario10(context, handler, option);
                        break;
                    case SCENARIO_11:
                        scenario11(context, handler, option);
                        break;
                    case SCENARIO_12:
                        scenario12(context, handler, option);
                        break;
                    case SCENARIO_13:
                        scenario13(context, handler, option);
                        break;
                    case SCENARIO_14:
                        scenario14(context, handler, option);
                        break;
                }
            }
        };
        handler.post(callback);
    }

    private static Runnable stopAnalyser(final Context context) {
        return new Runnable() {

            public void run() {
                sendIntent(context);
            }

            private void sendIntent(final Context context) {
                Intent intent = new Intent(ACTION_STOP_ANALYSER);
                context.sendBroadcast(intent);
            }
        };
    }

    private static Runnable runCc(final CcSenderItem ccSenderItem, final Context context) {
        return new Runnable() {
            public void run() {
                sendIntent(ccSenderItem, context);
            }

            private void sendIntent(final CcSenderItem ccSenderItem, final Context context) {
                Intent intent = new Intent(ACTION_START_SENDER_CC);
                intent.putExtra(Const.EXTRA_ITEM_SENDER_CC, ccSenderItem);
                context.sendBroadcast(intent);
            }
        };
    }

    private static void scenario1(final Context context, final Handler handler, final int option) {
        CcSenderInfo info =
                new CcSenderInfo(CcStatus.START, Cc.VOLUME_MUSIC, 20, CcType.IMEI, 20, CcSync.HANDLER);

        CcSenderItem volumeA = new CcSenderItem(MESSAGE_100, info);
        CcSenderItem volumeB = new CcSenderItem(MESSAGE_100, info);
        CcSenderItem volumeC = new CcSenderItem(MESSAGE_100, info);
        CcSenderItem volumeD = new CcSenderItem(MESSAGE_100, info);

        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 10, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 1, 50, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 1, 20, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 0, 1, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 10, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 1, 20, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 1, 1, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 0, 2, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 4, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 0, 5, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 3, 0, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 0, 10, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 5, 0, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 1, 10, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 6, 0, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 0, 0, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 20, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 7, 10, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 1, 0, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 4, 0, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 20, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 8, 10, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 1, 0, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 5, 0, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 2, 20, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 1, 10, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 2, 0, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 1, 0, 30));
        handler.postDelayed(runCc(volumeA, context), Methods.setDelay(0, 0, 20, 0));
        handler.postDelayed(runCc(volumeB, context), Methods.setDelay(0, 2, 10, 0));
        handler.postDelayed(runCc(volumeC, context), Methods.setDelay(0, 1, 43, 100));
        handler.postDelayed(runCc(volumeD, context), Methods.setDelay(0, 3, 0, 30));
        handler.postDelayed(stopAnalyser(context), Methods.setDelay(0, 10, 0, 0));
    }

    private static void scenario2(Context context, Handler handler, int option) {

    }

    private static void scenario3(Context context, Handler handler, int option) {

    }

    private static void scenario4(Context context, Handler handler, int option) {

    }

    private static void scenario5(Context context, Handler handler, int option) {

    }

    private static void scenario6(Context context, Handler handler, int option) {

    }

    private static void scenario7(Context context, Handler handler, int option) {

    }

    private static void scenario8(Context context, Handler handler, int option) {

    }

    private static void scenario9(Context context, Handler handler, int option) {

    }

    private static void scenario10(Context context, Handler handler, int option) {

    }

    private static void scenario11(Context context, Handler handler, int option) {

    }

    private static void scenario12(Context context, Handler handler, int option) {

    }

    private static void scenario13(Context context, Handler handler, int option) {

    }

    private static void scenario14(Context context, Handler handler, int option) {

    }
}
