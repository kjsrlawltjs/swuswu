package com.steganomobile.common.analyser.model;

import android.util.SparseArray;

import java.util.List;

import static com.steganomobile.common.Const.CSV_EMPTY;
import static com.steganomobile.common.Const.CSV_INTERVALS_ALL;

public class Interval {
    private static final String CSV_HEADER_ALL = "\n";
    private long duration;
    private long usage;
    private long delta = 0;
    private SparseArray<Process> processes = new SparseArray<Process>();
    private int id;

    public Interval(int id, long usage, long duration) {
        this.duration = duration;
        this.usage = usage;
        this.id = id;
    }

    public static String toCsvHeader(int option) {
        if (option == CSV_INTERVALS_ALL) {
            return CSV_HEADER_ALL;
        } else {
            return CSV_EMPTY;
        }
    }

    public long getUsage() {
        return usage;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public SparseArray<Process> getProcesses() {
        return processes;
    }

    public void countSimplePairs(List<ProcessPair> processPairs, int id, int border) {
        for (int i = 0; i < processes.size(); i++) {
            Process processA = processes.valueAt(i);
            if (processA.getDelta(id) <= border) continue;
            for (int j = i + 1; j < processes.size(); j++) {
                Process processB = processes.valueAt(j);
                if (processB.getDelta(id) <= border) continue;
                ProcessPair processPair = new ProcessPair(processA, processB);
                int pairIndex = processPairs.indexOf(processPair);
                if (pairIndex > 0) {
                    ProcessPair foundedPair = processPairs.get(pairIndex);
                    foundedPair.increaseSimpleCounter();
                }
            }
        }
    }

    public void countAveragePairs(List<ProcessPair> processPairs, int id) {
        for (int i = 0; i < processes.size(); i++) {
            Process processA = processes.valueAt(i);
            double averageDeltaA = processA.countAverageDelta();
            if (processA.getDelta(id) <= averageDeltaA) continue;
            for (int j = i + 1; j < processes.size(); j++) {
                Process processB = processes.valueAt(j);
                double averageDeltaB = processB.countAverageDelta();
                if (processB.getDelta(id) <= averageDeltaB) continue;
                ProcessPair processPair = new ProcessPair(processA, processB);
                int pairIndex = processPairs.indexOf(processPair);
                if (pairIndex > 0) {
                    ProcessPair foundedPair = processPairs.get(pairIndex);
                    foundedPair.increaseAverageCounter();
                }
            }
        }
    }

    public void countAverageNPairs(List<ProcessPair> processPairs, int id, int n) {
        for (int i = 0; i < processes.size(); i++) {
            Process processA = processes.valueAt(i);
            double averageDeltaA = processA.countAverageNDelta(id, n);
            if (processA.getDelta(id) <= averageDeltaA) continue;
            for (int j = i + 1; j < processes.size(); j++) {
                Process processB = processes.valueAt(j);
                double averageDeltaB = processB.countAverageNDelta(id, n);
                if (processB.getDelta(id) <= averageDeltaB) continue;
                ProcessPair processPair = new ProcessPair(processA, processB);
                int pairIndex = processPairs.indexOf(processPair);
                if (pairIndex > 0) {
                    ProcessPair foundedPair = processPairs.get(pairIndex);
                    foundedPair.increaseAverageNCounter();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[" + id + ": " + delta + "]\n");
        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.valueAt(i);
            builder.append(process.toString()).append('\n');
        }
        return builder.toString();
    }

    public String toCsv(int option) {
        StringBuilder b = new StringBuilder();

        if (option == CSV_INTERVALS_ALL) {
            b.append("[").append(id).append(": ").append(delta).append("]\n");
            for (int i = 0; i < processes.size(); i++) {
                Process process = processes.valueAt(i);
                b.append(process.toCsv(option)).append('\n');
            }
        } else {
            b.append(CSV_EMPTY);
        }
        return b.toString();
    }

    public int getId() {
        return id;
    }
}
