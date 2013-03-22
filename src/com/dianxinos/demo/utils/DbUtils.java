
package com.dianxinos.demo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbUtils {

    public static interface CursorProvider {
        Cursor getCursor() throws SQLException;
    }

    public static interface DbCallable<T> {
        T call(Cursor cursor) throws SQLException;
    }

    public static interface DbRunnable {
        void run() throws SQLException;
    }

    private static final String[] CONTACT_PROJECTION = {
            PhoneLookup._ID
    };

    private static final int CONTACT_ID = 0;

    public static void dbRun(DbRunnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            // TODO: report exception?
            e.printStackTrace();
        }
    }

    public static <T> T dbCall(DbCallable<T> callable, CursorProvider cursorProvider) {
        Cursor cursor = null;
        try {
            cursor = cursorProvider.getCursor();
            return callable.call(cursor);
        } catch (RuntimeException e) {
            // TODO: report exception?
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static long getContactId(final Context context, final String number) {
        final CursorProvider phoneCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(number));
                final String[] projection = CONTACT_PROJECTION;
                final String selection = null;
                final String[] selectionArgs = null;
                final String sortOrder = null;
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<Long> phoneCallable = new DbCallable<Long>() {

            @Override
            public Long call(Cursor cursor) throws SQLException {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getLong(CONTACT_ID);
                }
                return null;
            }

        };
        Long id = dbCall(phoneCallable, phoneCursorProvider);
        if (id == null) {
            return -1L;
        }
        return id;
    }

    public static Integer getContactsCount(final Context context) {
        final CursorProvider phoneCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = ContactsContract.Contacts.CONTENT_URI;
                final String[] projection = {
                        ContactsContract.Contacts.DISPLAY_NAME
                };
                final String selection = null;
                final String[] selectionArgs = null;
                final String sortOrder = null;
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<Integer> phoneCallable = new DbCallable<Integer>() {

            @Override
            public Integer call(Cursor cursor) throws SQLException {
                int count = 0;
                if (cursor != null) {
                    count = cursor.getCount();
                    cursor.close();
                }
                return count;
            }

        };
        return dbCall(phoneCallable, phoneCursorProvider);
    }

    public static List<Integer> getMissedCallIds(final Context context) {
        final CursorProvider callCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = CallLog.Calls.CONTENT_URI;
                final String[] projection = {
                        CallLog.Calls._ID
                };
                final String selection = String.format("%s = 1 AND %s = %s",
                        CallLog.Calls.NEW, CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
                final String[] selectionArgs = null;
                final String sortOrder = null;
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<List<Integer>> callCallable = new DbCallable<List<Integer>>() {

            @Override
            public List<Integer> call(Cursor cursor) throws SQLException {
                List<Integer> missedCallIds = new ArrayList<Integer>();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        missedCallIds.add(cursor.getInt(0));
                    } while (cursor.moveToNext());
                }
                return missedCallIds;
            }

        };
        return dbCall(callCallable, callCursorProvider);
    }

    public static Map<String, Long> getLastCalls(final Context context) {
        final CursorProvider callCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = CallLog.Calls.CONTENT_URI;
                final String[] projection = {
                        CallLog.Calls._ID, CallLog.Calls.NUMBER
                };
                final String selection = null;
                final String[] selectionArgs = null;
                final String sortOrder = "date DESC LIMIT 1";
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<Map<String, Long>> callCallable = new DbCallable<Map<String, Long>>() {

            @Override
            public Map<String, Long> call(Cursor cursor) throws SQLException {
                Map<String, Long> map = new HashMap<String, Long>();
                if (cursor != null && cursor.moveToFirst()) {
                    map.put(cursor.getString(1), cursor.getLong(0));
                }
                return map;
            }

        };
        return dbCall(callCallable, callCursorProvider);
    }

    public static boolean isMissedCall(final Context context, final String number, final Long date) {
        final CursorProvider callCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = CallLog.Calls.CONTENT_URI;
                final String[] projection = {
                        CallLog.Calls.NUMBER, CallLog.Calls.TYPE
                };

                final String selection = String.format("%s < ?",
                        CallLog.Calls.DATE);
                final String[] selectionArgs = {
                        date.toString()
                };
                final String sortOrder = CallLog.Calls.DATE + " desc";
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<Boolean> callCallable = new DbCallable<Boolean>() {

            @Override
            public Boolean call(Cursor cursor) throws SQLException {
                if (cursor != null && cursor.moveToFirst()) {
                    String missCallNumber = cursor.getString(0);
                    int type = cursor.getInt(1);
                    if (missCallNumber.equals(number) && type == CallLog.Calls.MISSED_TYPE) {
                        return true;
                    }
                }
                return false;
            }

        };
        return dbCall(callCallable, callCursorProvider);
    }

    public static ArrayList<Long> getFavContactIds(final Context context, final int limitSize) {
        final CursorProvider callCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                final Uri uri = ContactsContract.Contacts.CONTENT_URI;
                final String[] projection = {
                        ContactsContract.Contacts._ID
                };
                final String selection = null;
                final String[] selectionArgs = null;
                final String sortOrder = ContactsContract.Contacts.TIMES_CONTACTED
                        + " desc limit " + limitSize;
                return context.getContentResolver().query(uri, projection, selection,
                        selectionArgs, sortOrder);
            }

        };
        final DbCallable<ArrayList<Long>> callCallable = new DbCallable<ArrayList<Long>>() {

            @Override
            public ArrayList<Long> call(Cursor cursor) throws SQLException {
                ArrayList<Long> contactIds = new ArrayList<Long>();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        contactIds.add(cursor.getLong(0));
                    } while (cursor.moveToNext());
                }
                return contactIds;
            }
        };
        return dbCall(callCallable, callCursorProvider);
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
