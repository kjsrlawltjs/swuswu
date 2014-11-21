package com.steganomobile.common.receiver.model.cc;

import java.util.ArrayList;
import java.util.List;

public class CcContent {
    public static List<CcReceiverItem> ITEMS = new ArrayList<CcReceiverItem>();

    public static void addItem(CcReceiverItem item) {
        ITEMS.add(item);
    }
}
