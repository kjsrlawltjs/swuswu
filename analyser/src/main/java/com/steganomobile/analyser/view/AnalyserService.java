package com.steganomobile.analyser.view;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.analyser.model.Interval;
import com.steganomobile.common.analyser.model.Process;
import com.steganomobile.common.analyser.model.ProcessInterval;
import com.steganomobile.common.analyser.model.ProcessPair;
import com.steganomobile.common.sender.controller.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class AnalyserService extends Service {

    private static final int N = 5;
    private static final int SIMPLE_BORDER = 1;
    private final String TAG = AnalyserService.class.getSimpleName();
    private final IBinder binder = new InfoBinder();
    private final Handler handler = new Handler();
    private final SparseArray<Process> processes = new SparseArray<Process>();
    private final SparseArray<Interval> intervals = new SparseArray<Interval>();
    private ActivityManager activityManager;
    private int id = 0;
    private long previousTime = System.currentTimeMillis();
    private final Runnable intervalCallback = new Runnable() {
        public static final int TIME_RANGE = 2;
        public static final int TIME_INTERVAL = 1000;
        private Random random = new Random();

        public void run() {
            long currentTime = System.currentTimeMillis();
            int timesNumber = random.nextInt(TIME_RANGE) + 1;
            readProcStats(currentTime - previousTime);
            handler.postDelayed(this, timesNumber * TIME_INTERVAL);
            previousTime = currentTime;
        }
    };
    private List<String> systemApps = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent intent) {
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        prepareListener();
        handler.post(intervalCallback);

        int flags = PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);

        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 1) {
                systemApps.add(appInfo.processName);
            }
        }
        return binder;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(intervalCallback);
        super.onDestroy();
    }

    private void sendData() {
        List<ProcessPair> processPairs = createProcessPairs(processes);
        updateIntervalsDeltas(intervals);
        updateProcessesDeltas(processes, intervals);
        countPairs(intervals, processPairs);
        Methods.sendScenarioByEmail(this, intervals, processes, processPairs);
        clearData();
    }

    private void clearData() {
        intervals.clear();
        processes.clear();
        id = 0;
    }

    private void updateProcessesDeltas(SparseArray<Process> processes, SparseArray<Interval> intervals) {
        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.valueAt(i);
            long previousUsage = 0;
            long totalDelta = 0;
            for (int j = 0; j < process.getProcessIntervals().size(); j++) {
                int key = process.getProcessIntervals().keyAt(j);
                ProcessInterval processInterval = process.getProcessIntervals().valueAt(j);
                Interval interval = intervals.get(key);
                long delta = j == 0 ? 0 : processInterval.getUsage() - previousUsage;
                processInterval.setDelta(delta);
                interval.getProcesses().append(process.getPid(), process);
                totalDelta += delta;
                previousUsage = processInterval.getUsage();
            }
            process.setDelta(totalDelta);
        }
    }

    private void countPairs(SparseArray<Interval> intervals, List<ProcessPair> processPairs) {
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.valueAt(i);
            interval.countSimplePairs(processPairs, interval.getId(), SIMPLE_BORDER);
            interval.countAveragePairs(processPairs, interval.getId());
            interval.countAverageNPairs(processPairs, interval.getId(), N);
        }
    }

    private void updateIntervalsDeltas(SparseArray<Interval> intervals) {
        long previousUsage = 0;
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.valueAt(i);
            long delta = i == 0 ? 0 : interval.getUsage() - previousUsage;
            interval.setDelta(delta);
            previousUsage = interval.getUsage();
        }
    }

    private List<ProcessPair> createProcessPairs(SparseArray<Process> processes) {
        List<ProcessPair> processPairs = new ArrayList<ProcessPair>();
        for (int i = 0; i < processes.size(); i++) {
            Process processA = processes.valueAt(i);
            for (int j = i + 1; j < processes.size(); j++) {
                Process processB = processes.valueAt(j);
                processPairs.add(new ProcessPair(processA, processB));
            }
        }
        return processPairs;
    }

    void readProcStats(long duration) {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses != null) {
            Map<Integer, String> pids = new TreeMap<Integer, String>();
            for (ActivityManager.RunningAppProcessInfo processInfo : appProcesses) {
                if (systemApps.indexOf(processInfo.processName) == -1) {
                    pids.put(processInfo.pid, processInfo.processName);
                }
            }
            updateData(pids, duration);
        } else {
            Log.e(TAG, "Error in getting running apps");
        }
    }

    private void updateData(Map<Integer, String> pids, long duration) {
        Process process;
        long totalUsage = 0;

        long start = System.currentTimeMillis();

        for (int pid : pids.keySet()) {
            if (pids.get(pid).equals(Const.PACKAGE_STEGANO_ANALYSER)) {
                continue;
            }

            if (processes.indexOfKey(pid) < 0) {
                process = new Process(pids.get(pid), pid);
                processes.put(pid, process);
            } else {
                process = processes.get(pid);
            }
            long usage = Methods.readCpuUsage(pid);
//            long usage = Methods.readMemoryUsage(pid);
            totalUsage += usage;
            process.getProcessIntervals().put(id, new ProcessInterval(process, id, usage));
        }
        intervals.put(id, new Interval(id, totalUsage, duration));
        id++;

        Log.e(TAG, " " + (System.currentTimeMillis() - start));
    }

    private void prepareListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_STOP_ANALYSER);
        filter.addAction(Const.ACTION_START_SCENARIO);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    if (Const.ACTION_STOP_ANALYSER.equals(intent.getAction())) {
                        sendData();
                    } else if (Const.ACTION_START_SCENARIO.equals(intent.getAction())) {
                        int scenario = intent.getIntExtra(Const.EXTRA_SCENARIO, 1);
                        Scenario.send(scenario, getBaseContext());
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    public class InfoBinder extends Binder {
        AnalyserService getService() {
            return AnalyserService.this;
        }
    }
}
