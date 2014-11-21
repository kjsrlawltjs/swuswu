package com.steganomobile.receiver.view;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.receiver.model.nsd.NsdServiceItem;
import com.steganomobile.common.receiver.model.nsd.NsdSocket;

import java.io.IOException;
import java.net.ServerSocket;

public class NsdSocketService extends IntentService {

    public static final String TAG = NsdSocketService.class.getSimpleName();

    public NsdSocketService() {
        super(TAG);
    }

    public static void startActionStart(Context context, boolean isClientChecked, boolean isServerChecked, String serviceName) {
        Intent intent = new Intent(context, NsdSocketService.class);
        intent.setAction(Const.ACTION_START_SOCKET);
        NsdSocket socket = new NsdSocket(0);
        NsdServiceItem presence = new NsdServiceItem(socket, isClientChecked, isServerChecked, serviceName);
        intent.putExtra(Const.EXTRA_NSD_SERVICE_ITEM, presence);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();

            if (Const.ACTION_START_SOCKET.equals(action)) {
                NsdServiceItem presence = intent.getParcelableExtra(Const.ACTION_START_SOCKET);
                final String serviceName = presence.getServiceName();
                final boolean isClientChecked = presence.isClientChecked();
                final boolean isServerChecked = presence.isServerChecked();

                handleActionStart(serviceName, isClientChecked, isServerChecked);
            }
        }
    }

    private void handleActionStart(String serverName, boolean isClientChecked, boolean isServerChecked) {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            NsdPresenceService.startActionStartPresence(getBaseContext(), isClientChecked, isServerChecked, serverName, port);
//          for (Socket client; running && (client = server.accept()) != null;) {
//              Log.i(TAG, "New connection from " + client.getInetAddress());
//          }
            server.close();
        } catch (IOException e) {
            Log.e(TAG, "Server socket start fail!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
