
package com.doex.demo.database;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.doex.demo.R;

public class DatabaseFragment extends Fragment implements OnClickListener {

    private static final String TAG = "DatabaseFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.database_view, container, false);
    }

    @Override
    public void onClick(View v) {

    }

}
