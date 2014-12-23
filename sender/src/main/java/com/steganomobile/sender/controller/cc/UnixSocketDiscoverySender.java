package com.steganomobile.sender.controller.cc;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;

public class UnixSocketDiscoverySender extends CcImplSender {
    private static final String TAG = UnixSocketDiscoverySender.class.getSimpleName();
    private int port;
    private ServerSocket socket;

    public UnixSocketDiscoverySender(int port) {
        this.port = port;
    }

    @Override
    public void onSend(Context context, int element) {
        super.onSend(context, element);

        try {
            socket = new ServerSocket(port + element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestart() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
