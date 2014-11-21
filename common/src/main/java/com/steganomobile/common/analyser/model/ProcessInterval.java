package com.steganomobile.common.analyser.model;

import static com.steganomobile.common.Const.CSV_EMPTY;
import static com.steganomobile.common.Const.CSV_INTERVALS_ALL;
import static com.steganomobile.common.Const.CSV_PROCESSES_ALL;

public class ProcessInterval {
    private static final String CSV_HEADER_ALL = "Id,Delta,Usage\n";
    private final Process process;
    private long id;
    private long usage;
    private long delta;

    public ProcessInterval(Process process, long id, long usage) {
        this.process = process;
        this.id = id;
        this.usage = usage;
        this.delta = 0;
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

    public long getUsage() {
        return usage;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public double countPercentageUsage() {
        if (process.getUsage() != 0) {
            return (double) usage / process.getUsage();
        } else {
            return 0;
        }
    }

    public double countPercentageDelta() {
        if (process.getDelta() != 0) {
            return (double) usage / process.getDelta();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "[" + id + " = " + delta + "]";
    }

    public String toCsv(int option) {
        StringBuilder b = new StringBuilder();
        if (option == CSV_PROCESSES_ALL) {
//            b.append("[").append(process.getNameId()).append("],");
            b.append(id).append(',').append(delta).append(',').append(usage);
        } else if (option == CSV_INTERVALS_ALL) {
//            b.append("[").append(process.getNameId()).append("],");
            b.append(id).append(',').append(delta).append(',').append(usage);
        }
        return b.toString();
    }
}
