package com.steganomobile.receiver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.steganomobile.common.receiver.model.cc.CcMessage;
import com.steganomobile.common.receiver.model.cc.CcReceiverItem;
import com.steganomobile.common.receiver.model.cc.CcTime;
import com.steganomobile.common.receiver.model.nsd.NsdItem;
import com.steganomobile.common.receiver.model.nsd.NsdSocket;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

import java.util.ArrayList;
import java.util.List;

public class ReceiverDatabase {
    private static final String TAG = ReceiverDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "stegano.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database;
    private SteganoSQLiteHelper dbHelper;
    private String[] ccColumns = {
            CcReceiverItem._ID,
            CcMessage.CORRECT,
            CcMessage.DATA,
            CcTime.DURATION,
            CcTime.FINISH,
            CcInfo.INTERVAL,
            CcInfo.ITERATIONS,
            CcInfo.NAME,
            CcMessage.SIZE,
            CcTime.START,
            CcInfo.SYNC,
            CcInfo.TYPE
    };
    private String[] nsdColumns = {
            NsdItem._ID,
            NsdSocket.HOST,
            NsdItem.IS_PRESENT,
            NsdSocket.PORT,
            NsdItem.SERVICE_NAME
    };

    public ReceiverDatabase(Context context) {
        dbHelper = new SteganoSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addCcItem(CcReceiverItem item) {
        ContentValues values = new ContentValues();

        open();
        values.put(CcMessage.CORRECT, item.getMessage().getCorrect());
        values.put(CcMessage.DATA, item.getMessage().getData());
        values.put(CcTime.DURATION, item.getTime().getDuration());
        values.put(CcTime.FINISH, item.getTime().getFinish());
        values.put(CcInfo.INTERVAL, item.getInfo().getInterval());
        values.put(CcInfo.ITERATIONS, item.getInfo().getIterations());
        values.put(CcInfo.NAME, item.getInfo().getName().getValue());
        values.put(CcMessage.SIZE, item.getMessage().getSize());
        values.put(CcTime.START, item.getTime().getStart());
        values.put(CcInfo.SYNC, item.getInfo().getSync().getValue());
        values.put(CcInfo.TYPE, item.getInfo().getType().getValue());
        long id = database.insert(CcReceiverItem.TABLE_NAME, null, values);
        close();

        return id;
    }


    public long addNsdItem(NsdItem item) {
        ContentValues values = new ContentValues();
        open();
        values.put(NsdSocket.HOST, item.getSocket().getHost());
        values.put(NsdItem.IS_PRESENT, item.isPresent() ? 1 : 0);
        values.put(NsdSocket.PORT, item.getSocket().getPort());
        values.put(NsdItem.SERVICE_NAME, item.getServiceName());
        long id = database.insert(NsdItem.TABLE_NAME, null, values);
        close();

        return id;
    }

    public void deleteCcItem(long id) {
        open();
        database.delete(NsdItem.TABLE_NAME, NsdItem.ID + " = " + id, null);
        close();
    }

    public void deleteNsdItem(long id) {
        open();
        database.delete(CcReceiverItem.TABLE_NAME, CcReceiverItem.ID + " = " + id, null);
        close();
    }

    public void deleteNsdItems() {
        open();
        Cursor cursor = database.query(NsdItem.TABLE_NAME, nsdColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NsdItem nsdItem = cursorToNsdItem(cursor);
            database.delete(NsdItem.TABLE_NAME, NsdItem.ID + " = " + nsdItem.getId(), null);
            cursor.moveToNext();
        }
        cursor.close();
        close();
    }

    public void deleteCcItems() {
        open();
        Cursor cursor = database.query(CcReceiverItem.TABLE_NAME, ccColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CcReceiverItem ccReceiverItem = cursorToCcItem(cursor);
            database.delete(CcReceiverItem.TABLE_NAME, CcReceiverItem.ID + " = " + ccReceiverItem.getId(), null);
            cursor.moveToNext();
        }
        cursor.close();
        close();
    }

    public List<CcReceiverItem> getCcItems() {
        List<CcReceiverItem> ccReceiverItems = new ArrayList<CcReceiverItem>();
        open();
        Cursor cursor = database.query(CcReceiverItem.TABLE_NAME, ccColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CcReceiverItem ccReceiverItem = cursorToCcItem(cursor);
            ccReceiverItems.add(ccReceiverItem);
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return ccReceiverItems;
    }

    public List<NsdItem> getNsdItems() {
        List<NsdItem> nsdItems = new ArrayList<NsdItem>();
        open();
        Cursor cursor = database.query(NsdItem.TABLE_NAME, nsdColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NsdItem nsdItem = cursorToNsdItem(cursor);
            nsdItems.add(nsdItem);
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return nsdItems;
    }

    private CcReceiverItem cursorToCcItem(Cursor cursor) {
        long id = cursor.getLong(0);
        long correct = cursor.getLong(1);
        String data = cursor.getString(2);
        long duration = cursor.getLong(3);
        String finish = cursor.getString(4);
        int interval = cursor.getInt(5);
        int iterations = cursor.getInt(6);
        int name = cursor.getInt(7);
        long size = cursor.getLong(8);
        String start = cursor.getString(9);
        int sync = cursor.getInt(10);
        int type = cursor.getInt(11);

        CcMessage message = new CcMessage(size, data, correct);
        CcTime time = new CcTime(finish, start, duration);
        CcInfo info = new CcInfo(CcStatus.NO_VALUE, CcMethod.getFromInt(name), iterations,
                CcType.getFromInt(type), interval, CcSync.getFromInt(sync));
        return new CcReceiverItem(id, message, time, info);
    }

    private NsdItem cursorToNsdItem(Cursor cursor) {
        NsdSocket socket = new NsdSocket(cursor.getString(1), cursor.getInt(3));
        long id = cursor.getLong(0);
        boolean isPresent = cursor.getInt(2) != 0;
        String name = cursor.getString(4);

        return new NsdItem(id, socket, isPresent, name);
    }

    private static class SteganoSQLiteHelper extends SQLiteOpenHelper {

        private static final String NSD_TABLE_CREATE = "CREATE TABLE "
                + NsdItem.TABLE_NAME + " ("
                + NsdItem.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NsdSocket.HOST + " VARCHAR(15),"
                + NsdItem.IS_PRESENT + " INTEGER,"
                + NsdSocket.PORT + " INTEGER,"
                + NsdItem.SERVICE_NAME + " VARCHAR(40)"
                + ");";

        private static final String CC_TABLE_CREATE = "CREATE TABLE "
                + CcReceiverItem.TABLE_NAME + " ("
                + CcReceiverItem.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CcMessage.CORRECT + " INTEGER,"
                + CcMessage.DATA + " LONGTEXT,"
                + CcTime.DURATION + " INTEGER,"
                + CcTime.FINISH + " VARCHAR(17),"
                + CcInfo.INTERVAL + " INTEGER,"
                + CcInfo.ITERATIONS + " INTEGER,"
                + CcInfo.NAME + " INTEGER,"
                + CcMessage.SIZE + " INTEGER,"
                + CcTime.START + " VARCHAR(17),"
                + CcInfo.SYNC + " INTEGER,"
                + CcInfo.TYPE + " INTEGER"
                + ");";

        SteganoSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(NSD_TABLE_CREATE);
            db.execSQL(CC_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + CcReceiverItem.TABLE_NAME);
            onCreate(db);
        }
    }
}
