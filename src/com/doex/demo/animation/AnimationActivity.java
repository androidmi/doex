
package com.doex.demo.animation;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.doex.demo.R;

public class AnimationActivity extends Activity {
    AnimationView view;
    Handler h = new Handler() {
        public void handleMessage(android.os.Message msg) {
            view.invalidate();
        };
    };
    private AnimationLayoutView layoutView;
    private AnimationLayoutView layoutView2;
    private AnimationLayoutView layoutView3;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        final Uri uri = CallLog.Calls.CONTENT_URI;
        final String[] projection = null;

        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = CallLog.Calls.DATE + " desc";
        Cursor cursor = getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);

        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        do {
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                int index = cursor.getColumnIndex(columnName);
                String value = cursor.getString(index);
                Log.i("helloss", columnName + ":" + value);
            }
            Log.i("helloss", "--------------------------");
        } while (cursor.moveToNext());
        String where = "_id = 3";
        String[] columns = null;
        Uri u = Uri.withAppendedPath(uri, "3");
        long di = getContentResolver().delete(uri, where, columns);
        Log.i("dotest", "where:" + di);
        setContentView(R.layout.animation_sho_view);
        // layoutView = (AnimationLayoutView) findViewById(R.id.anmi);
        // layoutView2 = (AnimationLayoutView) findViewById(R.id.anmi2);
        // layoutView3 = (AnimationLayoutView) findViewById(R.id.anmi3);
        // findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // layoutView.setText("+009");
        // layoutView2.setText("+002");
        // layoutView3.setText("+005");
        // }
        // });
    }
}
