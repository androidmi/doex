package com.doex.demo.sms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.doex.demo.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SmsActivity extends Activity {

    private static final String TAG = "SmsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
//        DataReceiver.register(getApplicationContext());
        Set<String> numbers = new HashSet<String>(1);
        numbers.add("10086");
        long id = getOrCreateThreadId(getApplicationContext(), numbers);
        getOutgoingServiceCenter(id);

    }

    private void sendSms() {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = smsManager.divideMessage("卢卡库");
        int messageCount = messages.size();
        Log.i(TAG, "count:" + messageCount);
        for (int i = 0; i < messageCount; i++) {
            if (i == messageCount - 1) {
                Log.i(TAG, "change data value");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sms, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            try {
                Object o = ClassLoader.class.getClassLoader().loadClass("android.telephony.SmsManager");
                Field[] files = SmsManager.class.getDeclaredFields();
                for (Field f : files) {
                    String name = f.getName();
                    Type type = f.getGenericType();
                    Log.i(TAG, "type:" + type.toString());
                    if (type.toString().equals("int")) {
                        int value = f.getInt(o);
                        Log.i(TAG, name + ":" + value);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String[] SERVICE_CENTER_PROJECTION = new String[]{
            Telephony.Sms.Conversations.REPLY_PATH_PRESENT,
            Telephony.Sms.Conversations.SERVICE_CENTER,
    };

    private static final int COLUMN_REPLY_PATH_PRESENT = 0;
    private static final int COLUMN_SERVICE_CENTER = 1;

    /**
     * Get the service center to use for a reply.
     * <p/>
     * The rule from TS 23.040 D.6 is that we send reply messages to
     * the service center of the message to which we're replying, but
     * only if we haven't already replied to that message and only if
     * <code>TP-Reply-Path</code> was set in that message.
     * <p/>
     * Therefore, return the service center from the most recent
     * message in the conversation, but only if it is a message from
     * the other party, and only if <code>TP-Reply-Path</code> is set.
     * Otherwise, return null.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getOutgoingServiceCenter(long threadId) {
        Cursor cursor = null;
        try {
            cursor = SqliteWrapper.query(getApplicationContext(), getApplicationContext().getContentResolver(),
                    Telephony.Sms.Inbox.CONTENT_URI, SERVICE_CENTER_PROJECTION,
                    "thread_id = " + threadId, null, "date DESC");

            if ((cursor == null) || !cursor.moveToFirst()) {
                Log.i(TAG, "getOutgoingServiceCenter null");
                return null;
            }
            String center = cursor.getString(COLUMN_SERVICE_CENTER);
            int replyP = cursor.getInt(COLUMN_REPLY_PATH_PRESENT);
            Log.i(TAG, center + ":" + replyP);
            boolean replyPathPresent = (1 == replyP);
            return replyPathPresent ? center : null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static final Uri THREAD_ID_CONTENT_URI = Uri.parse(
            "content://mms-sms/threadID");

    private static final String[] ID_PROJECTION = {BaseColumns._ID};

    /**
     * Given the recipients list and subject of an unsaved message,
     * return its thread ID.  If the message starts a new thread,
     * allocate a new thread ID.  Otherwise, use the appropriate
     * existing thread ID.
     * <p/>
     * <p>Find the thread ID of the same set of recipients (in any order,
     * without any additions). If one is found, return it. Otherwise,
     * return a unique thread ID.</p>
     *
     * @hide
     */
    public static long getOrCreateThreadId(
            Context context, Set<String> recipients) {
        Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();

        for (String recipient : recipients) {
            uriBuilder.appendQueryParameter("recipient", recipient);
        }

        Uri uri = uriBuilder.build();
        //if (DEBUG) Rlog.v(TAG, "getOrCreateThreadId uri: " + uri);

        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
                uri, ID_PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0);
                } else {
                    Log.e(TAG, "getOrCreateThreadId returned no rows!");
                }
            } finally {
                cursor.close();
            }
        }

        Log.e(TAG, "getOrCreateThreadId failed with uri " + uri.toString());
        throw new IllegalArgumentException("Unable to find or allocate a thread ID.");
    }
}
