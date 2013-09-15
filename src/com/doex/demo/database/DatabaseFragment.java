
package com.doex.demo.database;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.doex.demo.R;
import com.doex.demo.database.model.LabelModel;
import com.doex.demo.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DatabaseFragment extends Fragment implements OnClickListener {

    private static final String TAG = "DatabaseFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        activity.findViewById(R.id.test_insert_performance).setOnClickListener(this);
        activity.findViewById(R.id.test_load_performance).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.database_view, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_insert_performance:
                new loadTask().execute();
                break;
            case R.id.test_load_performance:
                String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
                String[] selectionArgs = {
                        "1144555336"
                };
                Cursor cursor = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PEOPLE_PROJECTION, selection, selectionArgs, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return;
                }
                do {
                    long id = cursor.getLong(0);
                    String number = cursor.getString(1);
                    String name = cursor.getString(2);
                    Log.i(TAG, id + "-" + number + "-" + name);
                } while (cursor.moveToNext());
                break;
            default:
                break;
        }
    }

    private static final String[] PEOPLE_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            // ContactsContract.CommonDataKinds.Email.DATA,
            ContactsContract.Contacts.DISPLAY_NAME
    };

    class loadTask extends AsyncTask<Void, Void, Boolean> {

        public loadTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Activity activity = getActivity();
            try {
                InputStream in = getActivity().getAssets().open("line_90000.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                int count = 0;
                ArrayList<LabelModel> dataList = new ArrayList<LabelModel>();
                long s = System.currentTimeMillis();
                while ((line = reader.readLine()) != null) {
                    LabelModel model = LabelModel.create(line);
                    dataList.add(model);
                    if (count % 3000 == 0) {
                        long start = System.currentTimeMillis();
                        PhoneLabelDatabase.getInstance(activity).insert(dataList);
                        dataList.clear();
                        long duration = System.currentTimeMillis() - start;
                        Logger.i(TAG, "insert duration:" + duration);
                    }
                    count++;
                }
                long d = System.currentTimeMillis() - s;
                Logger.i(TAG, "totle time:" + d);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }

    }

}
