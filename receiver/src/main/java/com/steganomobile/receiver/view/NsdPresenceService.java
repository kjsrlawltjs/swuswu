package com.steganomobile.receiver.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdManager.ResolveListener;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.receiver.model.nsd.NsdItem;
import com.steganomobile.common.receiver.model.nsd.NsdServiceItem;
import com.steganomobile.common.receiver.model.nsd.NsdSocket;
import com.steganomobile.receiver.db.ReceiverDatabase;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static android.net.nsd.NsdManager.DiscoveryListener;
import static android.net.nsd.NsdManager.PROTOCOL_DNS_SD;

public class NsdPresenceService extends Service {
    protected static final String TAG = NsdPresenceService.class.getSimpleName();
    protected static final String SERVICE_TYPE = "_presence._tcp";
    private static boolean isPresent = false;
    private static boolean isRunning = false;
    private static boolean isRegistrationRegistered = false;
    private static boolean isDiscoveryRegistered = false;
    private static boolean isResolveRegistered = false;
    protected String serviceName;
    protected NsdServiceInfo info;
    private RegistrationListener registrationListener;
    private DiscoveryListener discoveryListener;
    private ResolveListener resolveListener;
    private NsdManager nsdManager;
    private List<NsdServiceInfo> infos;

    public NsdPresenceService() {
    }

    public static void startActionStartPresence(Context context, boolean isClientChecked, boolean isServerChecked, String serviceName, int port) {
        Intent intent = new Intent(context, NsdPresenceService.class);
        intent.setAction(Const.ACTION_START_PRESENCE);
        NsdSocket socket = new NsdSocket(port);
        NsdServiceItem item = new NsdServiceItem(socket, isClientChecked, isServerChecked, serviceName);
        intent.putExtra(Const.EXTRA_NSD_SERVICE_ITEM, item);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        if (isRegistrationRegistered) {
            nsdManager.unregisterService(registrationListener);
            isRegistrationRegistered = false;
        }

        if (isDiscoveryRegistered) {
            nsdManager.stopServiceDiscovery(discoveryListener);
            isDiscoveryRegistered = false;
        }

        super.onDestroy();
    }

    private void registerService(int port, String name) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        serviceInfo.setServiceName(name);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        nsdManager.registerService(serviceInfo, PROTOCOL_DNS_SD, registrationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Const.ACTION_START_PRESENCE.equals(action)) {
                NsdServiceItem item = intent.getParcelableExtra(Const.EXTRA_NSD_SERVICE_ITEM);
                final String serviceName = item.getServiceName();
                final int port = item.getSocket().getPort();
                final boolean isClientChecked = item.isClientChecked();
                final boolean isServerChecked = item.isServerChecked();
                handleActionStartPresence(serviceName, isClientChecked, isServerChecked, port);
            }
        }
        return Service.START_NOT_STICKY;
    }

    private void handleActionStartPresence(String serviceName, boolean isClientChecked, boolean isServerChecked, int port) {
        isRunning = true;
        infos = new ArrayList<NsdServiceInfo>();
        nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (NsdServiceInfo info : infos) {
                        final String host = info.getHost().toString().replace("/", "");
                        final int port = info.getPort();
                        final Semaphore mutex = new Semaphore(1);
                        final String name = info.getServiceName();

                        try {
                            mutex.acquire();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Socket connection = new Socket(host, port);
                                    isPresent = true;
                                    connection.close();
                                } catch (IOException e) {
                                    isPresent = false;
                                }
                                NsdSocket socket = new NsdSocket(host, port);
                                sendData(isPresent, name, socket);
                                mutex.release();
                            }
                        }).start();

                        try {
                            mutex.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mutex.release();
                    }
                    infos.clear();
                }
            }
        }).start();

        if (isServerChecked) {
            initializeRegistrationListener();
            registerService(port, serviceName);
        } else if (isRegistrationRegistered) {
            nsdManager.unregisterService(registrationListener);
        }

        if (isClientChecked) {
            initializeResolveListener();
        }

        if (isClientChecked) {
            initializeDiscoveryListener();
            nsdManager.discoverServices(SERVICE_TYPE, PROTOCOL_DNS_SD, discoveryListener);
        } else if (isDiscoveryRegistered) {
            nsdManager.stopServiceDiscovery(discoveryListener);
        }

        isRegistrationRegistered = isServerChecked;
        isDiscoveryRegistered = isClientChecked;
        isResolveRegistered = isClientChecked;
    }

    public void initializeRegistrationListener() {

        registrationListener = new RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                serviceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }
        };
    }

    public void initializeDiscoveryListener() {

        discoveryListener = new DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success");
                Log.d(TAG, String.format("%s %s %s %d",
                        service.getServiceName(),
                        service.getServiceType(),
                        service.getHost(),
                        service.getPort()));
                if (!service.getServiceType().contains(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(serviceName)) {
                    Log.d(TAG, "Same machine: " + serviceName);
                } else {
                    if (isResolveRegistered) {
                        nsdManager.resolveService(service, resolveListener);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "Lost Service " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, serviceType + " Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, serviceType + " Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, serviceType + " Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        resolveListener = new ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(serviceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                info = serviceInfo;
                infos.add(info);
            }
        };
    }

    private void sendData(boolean isPresent, String name, NsdSocket socket) {
        Intent intent = new Intent(Const.ACTION_FINISH_RECEIVER_NSD);
        final ReceiverDatabase database = new ReceiverDatabase(getBaseContext());
        NsdItem item = new NsdItem(0, socket, isPresent, name);
        long id = database.addNsdItem(item);
        item.setId(id);
        intent.putExtra(Const.EXTRA_NSD_ITEM, item);
        sendBroadcast(intent);
        Methods.playSound(getBaseContext());
    }

}
