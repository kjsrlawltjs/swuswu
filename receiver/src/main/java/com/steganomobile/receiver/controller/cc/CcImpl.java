package com.steganomobile.receiver.controller.cc;

import com.steganomobile.receiver.controller.DataCollector;

public abstract class CcImpl implements Cc {
    private DataCollector collector;

    protected CcImpl(DataCollector collector) {
        this.collector = collector;
    }

    public DataCollector getCollector() {
        return collector;
    }

    @Override
    public void clearCc() {
    }
}