
package com.dianxinos.demo.storage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dianxinos.demo.R;
import com.dianxinos.demo.temp.PhoneLabelDataStroe;
import com.dianxinos.demo.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;

public class StorageFragment extends Fragment {

    private static final String TAG = "StorageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.storage_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String text = FileUtils.getAssetFileContent(getActivity(), "json_40.txt");
        try {
            Log.i(TAG, "start");
            JSONArray array = new JSONArray(text);
            PhoneLabelDataStroe.init(array);
            Log.i(TAG, "end");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
