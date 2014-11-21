package com.steganomobile.sender.controller.cc;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;

public class UnixSocketDiscovery extends CcImpl {
    private static final String TAG = UnixSocketDiscovery.class.getSimpleName();
    private int port;
    private ServerSocket socket;

    public UnixSocketDiscovery(int port) {
        this.port = port;
    }

    @Override
    public void sendCc(Context context, int element) {
        super.sendCc(context, element);

        try {
            socket = new ServerSocket(port + element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finishCc() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
