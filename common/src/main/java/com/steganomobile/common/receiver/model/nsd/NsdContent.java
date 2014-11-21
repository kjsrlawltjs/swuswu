package com.steganomobile.common.receiver.model.nsd;

import java.util.ArrayList;
import java.util.List;

public class NsdContent {
    public static List<NsdItem> ITEMS = new ArrayList<NsdItem>();

    public static void addItem(NsdItem item) {
        ITEMS.add(item);
    }
}
