
package com.doex.demo.phone;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ListFragment;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class ContactFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get a cursor with all people
        Cursor c = getActivity().getContentResolver().query(Contacts.CONTENT_URI,
                CONTACT_PROJECTION, null, null, null);
        getActivity().startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
                // Use a template that displays a text view
                android.R.layout.simple_list_item_1,
                // Give the cursor to the list adatper
                c,
                // Map the NAME column in the people database to...
                new String[] {
                        Contacts.DISPLAY_NAME
                },
                // The "text1" view defined in the XML template
                new int[] {
                        android.R.id.text1
                });
        setListAdapter(adapter);
    }

    private static final String[] CONTACT_PROJECTION = new String[] {
            Contacts._ID,
            Contacts.DISPLAY_NAME
    };
}
