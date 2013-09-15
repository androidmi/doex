
package com.doex.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doex.demo.activity.IntentActivity;
import com.doex.demo.activity.MenuAct;
import com.doex.demo.animation.AnimationActivity;
import com.doex.demo.chart.ChartActivity;
import com.doex.demo.database.DatabaseFragment;
import com.doex.demo.fragment.FragmentInstance;
import com.doex.demo.guide.GuidePager;
import com.doex.demo.image.ImageFragment;
import com.doex.demo.loader.ContactLoader;
import com.doex.demo.model.PhoneLabel;
import com.doex.demo.model.UserModel;
import com.doex.demo.network.NetWorkFragment;
import com.doex.demo.notification.NotificationUtil;
import com.doex.demo.phone.ContactActivity;
import com.doex.demo.utils.ImageUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOnClickListener(R.id.database);
        setOnClickListener(R.id.storage);
        setOnClickListener(R.id.guide);
        setOnClickListener(R.id.activity);
        setOnClickListener(R.id.image);
        setOnClickListener(R.id.loader);
        setOnClickListener(R.id.data_store);
        setOnClickListener(R.id.notification);
        setOnClickListener(R.id.parcelable);
        setOnClickListener(R.id.chart);
        setOnClickListener(R.id.grphics);
        setOnClickListener(R.id.network);
        setOnClickListener(R.id.contact);
        setOnClickListener(R.id.animation);
    }

    @Override
    public void onClick(View v) {
        final Activity activity = this;

        switch (v.getId()) {
            case R.id.database:
                String number = PhoneNumberUtils.stripSeparators(" 123 -oo *&^%$#@!+_)(334-990");
                Log.i("hello", "number:" + number);
                number = PhoneNumberUtils.stripSeparators("+86 123--3-4-4-634-9 90");
                Log.i("hello", "number:" + number);
                number = PhoneNumberUtils.stripSeparators("+86  100sdsdff3334-990");
                Log.i("hello", "number:" + number);
                startActivity(DatabaseFragment.class.getName());
                break;
            case R.id.storage:
                Build b = new Build();
                String device = Build.DEVICE;
                String product = Build.PRODUCT;
                Log.i("hello", "device:" + device);
                String s = new String();
                s += "device:" + device;
                s += "\n";
                s += "product:" + product;
                Log.i("hello", "product:" + product);
                String manufacturer = Build.MANUFACTURER;
                Log.i("hello", "manufacturer:" + manufacturer);
                String m = Build.MODEL;
                Log.i("hello", "model:" + m);
                s += "\n";
                s += "manufacturer:" + manufacturer;
                s += "\n";
                s += "model:" + m;
                String incremental = Build.VERSION.INCREMENTAL;
                s += "\n";
                s += "incremental:" + incremental;
                Toast.makeText(activity, s, Toast.LENGTH_LONG).show();

                // startActivity(StorageFragment.class.getName());
                break;
            case R.id.guide:
                startActivity(GuidePager.class);
                break;
            case R.id.activity:
                Intent acivity = new Intent(this, IntentActivity.class);
                acivity.putExtra(IntentActivity.EXTRA_NAME, "jack");
                startActivity(acivity);
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
                        new task(getApplicationContext()).execute();
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
                startActivity(new Intent(this, ChartActivity.class));
                break;
            case R.id.grphics:
                // startActivity(GraphicsFragment.class.getName());
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(Insert.PHONE, "223344556");
                startActivityForResult(intent, 10);
                break;
            case R.id.network:
                startActivity(NetWorkFragment.class.getName());
                break;
            case R.id.contact:
                Intent i = new Intent(this, ContactActivity.class);
                startActivity(i);
                break;
            case R.id.animation:
                // startActivity(AnimationActivity.class);
                Log.i("test", "begin");
               
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("hello", requestCode + ":" + resultCode);
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

    public static void startActivity(Context context, String name) {
        Intent intent = new Intent(context, FragmentInstance.class);
        intent.putExtra(FragmentInstance.BUNDLE, name);
        context.startActivity(intent);
    }

    private void startActivity(String name) {
        Intent intent = new Intent(this, FragmentInstance.class);
        intent.putExtra(FragmentInstance.BUNDLE, name);
        startActivity(intent);
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    private void setOnClickListener(int viewId) {
        findViewById(viewId).setOnClickListener(this);
    }

}
