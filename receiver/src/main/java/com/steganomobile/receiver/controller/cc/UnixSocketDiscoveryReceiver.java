package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;
import java.net.ServerSocket;

public class UnixSocketDiscoveryReceiver extends CcImplReceiver {
    private static final String TAG = UnixSocketDiscoveryReceiver.class.getSimpleName();

    public UnixSocketDiscoveryReceiver(DataCollector collector) {
        super(collector);
    }

    static {
        System.loadLibrary("stegano");
    }

    private static boolean isSocketOpen(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public native int getOpenPort(int start, int end);

    @Override
    public void onReceive(String action) {
        int port = getCollector().getInfo().getPort();

        getCollector().setData((byte) getOpenPort(port - 128, port + 127));

//        for (int i = port - 127; i < port + 127; i++) {
//            if (isSocketOpen(i)) {
//                getCollector().setData((byte) (i - port));
//            }
//        }
    }
}