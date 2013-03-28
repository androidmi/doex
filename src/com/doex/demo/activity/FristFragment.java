
package com.doex.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doex.demo.R;
import com.doex.demo.utils.Logger;

public class FristFragment extends Fragment {
    private static final String TAG = "FristFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getActivity().getIntent();
        String name = intent.getStringExtra(IntentActivity.EXTRA_NAME);
        testIntents();
        intent.putExtra(IntentActivity.EXTRA_NAME, "goto");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frist_fragment_view, null, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.i(TAG, "onDetach");
        testIntents();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy");
        testIntents();
        getActivity().getIntent().putExtra(IntentActivity.EXTRA_NAME, "onDestroy");
    }

    private void testIntents() {
        String extraName = getActivity().getIntent().getStringExtra(IntentActivity.EXTRA_NAME);
        Logger.i(TAG, "extraName:" + extraName);
    }

}
