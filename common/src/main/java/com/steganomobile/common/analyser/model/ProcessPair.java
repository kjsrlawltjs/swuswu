package com.steganomobile.common.analyser.model;

import static com.steganomobile.common.Const.CSV_EMPTY;
import static com.steganomobile.common.Const.CSV_PAIRS_ALL;

public class ProcessPair {

    private static final String TAG = "ProcessPair";
    private static final String CSV_HEADER_ALL = "Pair,Simple,Total Simple,Average,Total Average,AverageN,Total AverageN\n";
    private Process processA;
    private Process processB;
    private int simpleCounter = 0;
    private int averageCounter = 0;
    private int averageNCounter = 0;

    public ProcessPair(Process processA, Process processB) {
        this.processA = processA;
        this.processB = processB;
    }

    public static String toCsvHeader(int option) {
        if (option == CSV_PAIRS_ALL) {
            return CSV_HEADER_ALL;
        } else {
            return CSV_EMPTY;
        }
    }

    private int countTotalTicks(double borderA, double borderB) {
        return processA.countTotalTicks(borderA) + processB.countTotalTicks(borderB);
    }

    public int countTotalTicks(long borderA, long borderB) {
        return processA.countTotalTicks(borderA) + processB.countTotalTicks(borderB);
    }

    @Override
    public String toString() {
        String simple = simpleCounter + "/" + countTotalTicks(1, 1);
        String average = averageCounter + "/" + countTotalTicks(processA.countAverageDelta(), processB.countAverageDelta());
        String averageN = averageNCounter + "/" + countTotalTicks(processA.countAverageDelta(), processB.countAverageDelta());

        return "[" + processA.toString() + processB.toString() + "] = "
                + simple + " " + average + " " + averageN;
    }

    public String toCsv(int option) {
        StringBuilder b = new StringBuilder();
        String simple = simpleCounter + "," + countTotalTicks(1, 1);
        String average = averageCounter + "," + countTotalTicks(processA.countAverageDelta(), processB.countAverageDelta());
        String averageN = averageNCounter + "," + countTotalTicks(processA.countAverageDelta(), processB.countAverageDelta());

        if (option == CSV_PAIRS_ALL) {
            b.append("[").append(processA.toString()).append(processB.toString()).append("],");
            b.append(simple).append(",").append(average).append(",").append(averageN);
        } else {
            b.append(CSV_EMPTY);
        }
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessPair)) return false;

        ProcessPair that = (ProcessPair) o;

        return processA.equals(that.processA) && processB.equals(that.processB)
                || processA.equals(that.processB) && processB.equals(that.processA);

    }

    @Override
    public int hashCode() {
        int result = processA.hashCode();
        result += processB.hashCode();
        return result;
    }

    public void increaseSimpleCounter() {
        simpleCounter++;
    }

    public void increaseAverageCounter() {
        averageCounter++;
    }

    public void increaseAverageNCounter() {
        averageNCounter++;
    }
}
