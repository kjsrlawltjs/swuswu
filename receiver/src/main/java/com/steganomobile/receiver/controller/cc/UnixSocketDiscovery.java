package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

import java.io.IOException;
import java.net.ServerSocket;

public class UnixSocketDiscovery extends CcImpl {
    private static final String TAG = UnixSocketDiscovery.class.getSimpleName();

    public UnixSocketDiscovery(DataCollector collector) {
        super(collector);
    }

    private static boolean isSocketOpen(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public void runCc(String action) {
        int port = getCollector().getInfo().getPort();

        for (int i = port - 127; i < port + 127; i++) {
            if (isSocketOpen(i)) {
                getCollector().setData((byte) (i - port));
            }
        }
    }
}