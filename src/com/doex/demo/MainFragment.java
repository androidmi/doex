
package com.doex.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.doex.demo.activity.MenuAct;
import com.doex.demo.chart.MultipleTemperatureChart;
import com.doex.demo.database.DatabaseFragment;
import com.doex.demo.fragment.FragmentInstance;
import com.doex.demo.guide.GuidePager;
import com.doex.demo.image.ImageFragment;
import com.doex.demo.loader.ContactLoader;
import com.doex.demo.model.UserModel;
import com.doex.demo.notification.NotificationUtil;
import com.doex.demo.storage.StorageFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment implements OnClickListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setOnClickListener(R.id.database);
        setOnClickListener(R.id.storage);
        setOnClickListener(R.id.guide);
        setOnClickListener(R.id.menu);
        setOnClickListener(R.id.image);
        setOnClickListener(R.id.loader);
        setOnClickListener(R.id.data_store);
        setOnClickListener(R.id.notification);
        setOnClickListener(R.id.parcelable);
        setOnClickListener(R.id.chart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, null, true);
    }

    @Override
    public void onClick(View v) {
        final Activity activity = getActivity();

        switch (v.getId()) {
            case R.id.database:
                startActivity(DatabaseFragment.class.getName());
                break;
            case R.id.storage:
                startActivity(StorageFragment.class.getName());
                break;
            case R.id.guide:
                startActivity(GuidePager.class);
                break;
            case R.id.menu:
                startActivity(MenuAct.class);
                break;
            case R.id.image:
                startActivity(ImageFragment.class.getName());
                break;
            case R.id.loader:
                startActivity(ContactLoader.class.getName());
                break;
            case R.id.notification:
                Timer t = new Timer();
                t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        new task(getActivity().getApplicationContext()).execute();
                    }
                }, 2000, 10000);
                break;
            case R.id.parcelable:
                Intent in = new Intent(activity, MenuAct.class);
                Bundle data = new Bundle();
                UserModel model = new UserModel();
                model.setName("javk");
                model.setBirthTime(123455);
                model.setAge(14);
                model.setAddress("new york");
                in.putExtra("key", model);
                startActivity(in);
                break;
            case R.id.chart:
                startActivity(new MultipleTemperatureChart().execute(getActivity()));
                break;
        }
    }

    static class task extends AsyncTask<Void, Void, Boolean> {
        Context mContext;

        private task(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Random r = new Random();
            NotificationUtil.showNotification(mContext, ":");
        }

    }

    private void startActivity(String name) {
        Intent intent = new Intent(getActivity(), FragmentInstance.class);
        intent.putExtra(FragmentInstance.BUNDLE, name);
        startActivity(intent);
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    private void setOnClickListener(int viewId) {
        getActivity().findViewById(viewId).setOnClickListener(this);
    }

}
