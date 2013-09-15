
package com.doex.demo.phone;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PhoneFragment extends ListFragment {
    private static final String TAG = "PhoneFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get a cursor with all phones
        Cursor c = getActivity().getContentResolver().query(Phone.CONTENT_URI,
                PHONE_PROJECTION, null, null, null);
        getActivity().startManagingCursor(c);
        if (c != null && c.getCount() > 0) {
            Log.i(TAG, "has permission");
        } else {
            Log.i(TAG, "hi gay there is no permission");
        }
        Log.i(TAG, "count:" + c.getCount());
        // Map Cursor columns to views defined in simple_list_item_2.xml
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, c,
                new String[] {
                        Phone.TYPE,
                        Phone.NUMBER
                },
                new int[] {
                        android.R.id.text1, android.R.id.text2
                });
        // Used to display a readable string for the phone type
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                // Let the adapter handle the binding if the column is not TYPE
                if (columnIndex != COLUMN_TYPE) {
                    return false;
                }
                int type = cursor.getInt(COLUMN_TYPE);
                String label = null;
                // Custom type? Then get the custom label
                if (type == Phone.TYPE_CUSTOM) {
                    label = cursor.getString(COLUMN_LABEL);
                }
                // Get the readable string
                String text = (String) Phone.getTypeLabel(getResources(), type, label);
                // Set text
                ((TextView) view).setText(text);
                return true;
            }
        });
        setListAdapter(adapter);
    }

    private static final String[] PHONE_PROJECTION = new String[] {
            Phone._ID,
            Phone.TYPE,
            Phone.LABEL,
            Phone.NUMBER
    };

    private static final int COLUMN_TYPE = 1;;
    private static final int COLUMN_LABEL = 2;
}
