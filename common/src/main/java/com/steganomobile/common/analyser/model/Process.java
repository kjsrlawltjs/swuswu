package com.steganomobile.common.analyser.model;

import android.util.SparseArray;

import static com.steganomobile.common.Const.CSV_EMPTY;
import static com.steganomobile.common.Const.CSV_INTERVALS_ALL;
import static com.steganomobile.common.Const.CSV_PROCESSES_ALL;

public class Process {
    private static final String CSV_HEADER_ALL = "Process\n";
    private String name;
    private int pid;
    private SparseArray<ProcessInterval> processorIntervals;
    private long usage;
    private long delta;

    public Process(String name, int pid) {
        this.name = name;
        this.pid = pid;
        processorIntervals = new SparseArray<ProcessInterval>();
    }

    public static String toCsvHeader(int option) {
        if (option == CSV_PROCESSES_ALL) {
            return CSV_HEADER_ALL;
        } else if (option == CSV_INTERVALS_ALL) {
            return CSV_HEADER_ALL;
        } else {
            return CSV_EMPTY;
        }
    }

    public SparseArray<ProcessInterval> getProcessorIntervals() {
        return processorIntervals;
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public long getUsage() {
        return usage;
    }

    public double countAverageUsage() {
        if (processorIntervals.size() > 0) {
            return (double) usage / processorIntervals.size();
        } else {
            return 0;
        }
    }

    public double countAverageDelta() {
        if (processorIntervals.size() > 0) {
            return (double) delta / processorIntervals.size();
        } else {
            return 0;
        }
    }

    public double countAverageNDelta(int id, int n) {
        ProcessInterval processInterval = processorIntervals.get(id);
        int end = processorIntervals.indexOfKey(id);
        if (processInterval == null) return 0;
        int start = n > end ? 0 : end - n;
        int deltaN = 0;

        for (int i = start; i < end; i++) {
            ProcessInterval iterationInterval = processorIntervals.valueAt(i);
            deltaN += iterationInterval.getDelta();
        }

        if (n > 0) return (double) deltaN / n;
        return 0;
    }

    public int countTotalTicks(long border) {
        int counter = 0;
        for (int i = 0; i < processorIntervals.size(); i++) {
            ProcessInterval processInterval = processorIntervals.valueAt(i);
            if (processInterval.getDelta() > border) {
                counter++;
            }
        }
        return counter;
    }

    public int countTotalTicks(double border) {
        int counter = 0;
        for (int i = 0; i < processorIntervals.size(); i++) {
            ProcessInterval processInterval = processorIntervals.valueAt(i);
            if (processInterval.getDelta() > border) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public String toString() {
        return "[" + getName() + "]";
    }

    public String toCsv(int option) {
        StringBuilder b = new StringBuilder();
        if (option == CSV_PROCESSES_ALL) {
            b.append("[").append(getName()).append("]\n");
            b.append(ProcessInterval.toCsvHeader(option));
            for (int i = 0; i < processorIntervals.size(); i++) {
                ProcessInterval processInterval = processorIntervals.valueAt(i);
                b.append(processInterval.toCsv(option)).append('\n');
            }
        } else if (option == CSV_INTERVALS_ALL) {
            b.append("[").append(getName()).append("]\n");
            b.append(ProcessInterval.toCsvHeader(option));
            for (int i = 0; i < processorIntervals.size(); i++) {
                ProcessInterval processInterval = processorIntervals.valueAt(i);
                b.append(processInterval.toCsv(option)).append('\n');
            }
        } else {
            b.append(CSV_EMPTY);
        }
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Process)) return false;
        Process process = (Process) o;
        return pid == process.pid;
    }

    @Override
    public int hashCode() {
        int result;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + pid;
        result = 31 * result + (processorIntervals != null ? processorIntervals.hashCode() : 0);
        result = 31 * result + (int) (usage ^ (usage >>> 32));
        result = 31 * result + (int) (delta ^ (delta >>> 32));
        return result;
    }

    public long getDelta(int intervalId) {
        ProcessInterval processInterval = processorIntervals.get(intervalId);
        return processInterval.getDelta();
    }
}
