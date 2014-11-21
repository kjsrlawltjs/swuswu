package com.steganomobile.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.steganomobile.common.analyser.model.Interval;
import com.steganomobile.common.analyser.model.Process;
import com.steganomobile.common.analyser.model.ProcessPair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static com.steganomobile.common.Const.CSV_PAIRS_ALL;
import static com.steganomobile.common.Const.CSV_PROCESSES_ALL;

public class Methods {

    private static final String TAG = Methods.class.getSimpleName();

    public static long readMemoryUsage(int pid) {
        try {
            RandomAccessFile myReaderApp = new RandomAccessFile("/proc/" + pid + "/statm", "r");
            myReaderApp.seek(0);
            String load = myReaderApp.readLine();
            myReaderApp.close();
            String[] tokes = load.split(" ");
            return Long.parseLong(tokes[0]);
        } catch (Exception e) {
            Log.e(TAG, "Error in opening stat files: " + e.toString());
        }
        return -1;
    }


    public static long readCpuUsage(int pid) {
        try {
            RandomAccessFile myReaderApp = new RandomAccessFile("/proc/" + pid + "/stat", "r");
            myReaderApp.seek(0);
            String load = myReaderApp.readLine();
            myReaderApp.close();
            String[] tokes = load.split(" ");
            return Long.parseLong(tokes[9]) + Long.parseLong(tokes[13]) + Long.parseLong(tokes[14]) + Long.parseLong(tokes[15]) + Long.parseLong(tokes[16]);
        } catch (Exception e) {
            Log.e(TAG, "Error in opening stat files: " + e.toString());
        }
        return -1;
    }

    public static long readPidUsage(String tag, int pid) {
        String load = readProcStat(tag, pid);
        if (load == null) {
            return -1;
        }
        return Long.parseLong(load.split(" ")[13]);
    }

    private static String readProcStat(String tag, int pid) {
        try {
            RandomAccessFile myReaderApp = new RandomAccessFile("/proc/" + pid + "/stat", "r");
            myReaderApp.seek(0);
            String load = myReaderApp.readLine();
            myReaderApp.close();
            return load;
        } catch (IOException e) {
            Log.e(tag, "Error in opening stat files: " + e.toString());
        }
        return null;
    }

    public static int getPidOfAplication(String tag, Context context, String packageName) {

        ActivityManager myActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = myActivityManager.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    return runningAppProcessInfo.pid;
                }
            }
        } else {
            Log.e(tag, "Error in getting running apps");
        }
        return -1;
    }

    public static boolean isServiceRunning(Context context, String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myRunningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        if (myRunningServiceInfos == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : myRunningServiceInfos) {
            if (packageName.equals(service.process)) {
                return true;
            }
        }
        return false;
    }

    public static void sendScenarioByEmail(Context context, SparseArray<Interval> intervals,
                                           SparseArray<Process> processes, List<ProcessPair> processPairs) {
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.fromFile(convertProcessesPairsToFile(processPairs, CSV_PAIRS_ALL)));
//        uris.add(Uri.fromFile(convertIntervalsToFile(intervals, CSV_INTERVALS_ALL)));
        uris.add(Uri.fromFile(convertProcessesToFile(processes, CSV_PROCESSES_ALL)));
        sendEmail(context, uris);
    }

    public static File convertProcessesPairsToFile(List<ProcessPair> processPairs, int option) {
        File file = new File(Environment.getExternalStorageDirectory(), "/processes_pairs.csv");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(printProcessPairs(processPairs, option).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File convertProcessesToFile(SparseArray<Process> processes, int option) {
        File file = new File(Environment.getExternalStorageDirectory(), "/processes.csv");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(printProcesses(processes, option).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File convertIntervalsToFile(SparseArray<Interval> intervals, int option) {
        File file = new File(Environment.getExternalStorageDirectory(), "/intervals.csv");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(printIntervals(intervals, option).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void sendEmail(Context context, ArrayList<Uri> uris) {
        sendEmail(context, uris, "0.660162@gmail.com");
    }


    public static void sendEmail(Context context, ArrayList<Uri> uris, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {email};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[stegano] CSV data");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        context.startActivity(emailIntent);
    }

    private static String printProcessPairs(List<ProcessPair> processPairs, int option) {
        StringBuilder builder = new StringBuilder("PROCESS PAIRS\n");
        builder.append(ProcessPair.toCsvHeader(option));
        for (ProcessPair processPair : processPairs) {
            builder.append(processPair.toCsv(option)).append('\n');
        }
        return builder.toString();
    }

    private static String printIntervals(SparseArray<Interval> intervals, int option) {
        StringBuilder builder = new StringBuilder("INTERVALS\n");
        builder.append(Interval.toCsvHeader(option));
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.valueAt(i);
            builder.append(interval.toCsv(option)).append('\n');
        }
        return builder.toString();
    }

    private static String printProcesses(SparseArray<Process> processes, int option) {
        StringBuilder builder = new StringBuilder("PROCESSES\n");
        builder.append(Process.toCsvHeader(option));
        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.valueAt(i);
            builder.append(process.toCsv(option)).append('\n');
        }
        return builder.toString();
    }

    public static void playSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long setDelay(int hours, int minutes, int secs, int millis) {
        final int MILLIS_IN_HOUR = 3600000;
        final int MILLIS_IN_MINUTE = 60000;
        final int MILLIS_IN_SECOND = 1000;

        return hours * MILLIS_IN_HOUR
                + minutes * MILLIS_IN_MINUTE
                + secs * MILLIS_IN_SECOND
                + millis;
    }
}
