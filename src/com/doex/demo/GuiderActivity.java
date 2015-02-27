package com.doex.demo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doex.demo.activity.IntentActivity;
import com.doex.demo.activity.MenuAct;
import com.doex.demo.chart.ChartActivity;
import com.doex.demo.fragment.FragmentInstance;
import com.doex.demo.guide.GuidePager;
import com.doex.demo.image.ImageFragment;
import com.doex.demo.loader.ContactLoader;
import com.doex.demo.model.UserModel;
import com.doex.demo.network.NetWorkFragment;
import com.doex.demo.notification.NotificationUtil;
import com.doex.demo.os.OSActivity;
import com.doex.demo.phone.ContactActivity;
import com.doex.demo.skin.SkinActivity;
import com.doex.demo.sms.SmsActivity;
import com.doex.demo.system.SystemActivity;
import com.doex.demo.utils.FileUtils;
import com.doex.demo.utils.ImageUtils;
import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class GuiderActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";

    Handler mHander = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View view = new View(this);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide));
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindowManager().removeView(view);

            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.flags = Window.FEATURE_NO_TITLE;
        // getWindowManager().addView(view, params);

        // mHander.sendEmptyMessageDelayed(1, 1000);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, DoexService.class);
//        startService(intent);
        setOnClickListener(R.id.sms);
        setOnClickListener(R.id.system);
        setOnClickListener(R.id.theme);
        setOnClickListener(R.id.osId);
        setOnClickListener(R.id.viewId);
        setOnClickListener(R.id.service);
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
        setOnClickListener(R.id.json);
        TextView textView = (TextView) findViewById(R.id.test);
        textView.setText("heeeeee");
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        EditText text = (EditText) findViewById(R.id.text);
        
        String cpuAbi = Build.CPU_ABI;
        String cupAbi2 = Build.CPU_ABI2;
        text.setText(cpuAbi+"  "+cupAbi2);
        text.setFilters(new InputFilter[] { new InputFilter() {
            boolean isBack = false;
            StringBuilder sb = new StringBuilder();

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                    Spanned dest, int dstart, int dend) {
//                Pattern p = Pattern.compile("[^0-9.\\s]");
//                Matcher m = p.matcher(source);
//                if (m.find()) {
//                    source = m.replaceAll("");
//                }
                // if (end > 0) {
                // if ((dstart == 2 || dstart == 7)) {
                // source = source + " ";
                // }
                // }
                Log.i("hello", "dest:" + dest);
                // 158 1040 3964
                Log.i("hello", start + ":start:" + end);
                Log.i("hello", dstart + ":dend:" + dend);
                return source;
            }
        }, new InputFilter.LengthFilter(13) });
        text.addTextChangedListener(new TextWatcher() {
            private int mLength;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                Log.i("dotest", "change:" + s + ":" + start + ":" + before + ":"
                        + count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                Log.i("maube", "before:" + s + ":" + start + ":" + after + ":"
                        + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                int length = s.length();
//                Pattern p = Pattern.compile("[^0-9.\\s]");
//                Matcher m = p.matcher(s.toString());
//                if (m.find()) {
//                    String re = m.replaceAll("");
//                }
//                if (mLength < length) {
//                    if ((length == 3 || length == 8)) {
//                        s.append(" ");
//                    }
//                }
//                mLength = length;
            }
        });
    }

    @Override
    public void onClick(View v) {
        final Activity activity = this;

        switch (v.getId()) {
        case R.id.database:
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//            intent.setClassName("com.android.phone", "com.android.phone.Settings");  
            startActivity(intent);   
//            String number = PhoneNumberUtils
//                    .stripSeparators(" 123 -oo *&^%$#@!+_)(334-990");
//            Log.i("hello", "number:" + number);
//            number = PhoneNumberUtils
//                    .stripSeparators("+86 123--3-4-4-634-9 90");
//            Log.i("hello", "number:" + number);
//            number = PhoneNumberUtils.stripSeparators("+86  100sdsdff3334-990");
//            Log.i("hello", "number:" + number);
//            startActivity(DatabaseFragment.class.getName());
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
//            Intent intent = new Intent(Intent.ACTION_INSERT);
//            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//            intent.putExtra(Insert.PHONE, "223344556");
//            startActivityForResult(intent, 10);
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
            new Thread(new Runnable() {

                @Override
                public void run() {
                    InputStream in = null;
                    BufferedInputStream bufferedInputStream = null;
                    BufferedReader reader = null;
                    PrintWriter writer = null;
                    try {
                        in = getAssets().open("top_500_phone.txt");
                        bufferedInputStream = new BufferedInputStream(in);
                        reader = new BufferedReader(new InputStreamReader(
                                bufferedInputStream));
                        ArrayList<String> numberArr = new ArrayList<String>();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String[] array = line.split("\t");
                            String number = array[0];
                            numberArr.add(number);
                        }
                        File outFile = new File(
                                Environment.getExternalStorageDirectory()
                                        + "do.txt");
                        // writer = new PrintWriter(outFile);
                        String[] nu = { "4001011893", "666555", "65666",
                                "15216765207", "15216765207" };
                        for (int i = 0; i < numberArr.size(); i++) {
                            String n = numberArr.get(i);
                            Log.i("hello", "number:" + n);
                           /* PhoneLabel label = ImageUtils.getServerMarkedLabel(
                                    getApplicationContext(), n);
                            if (label.isOK()) {
                                Log.i("test", "number:" + n + "\t label:"
                                        + label.getLabel() + "\t count:"
                                        + label.getCount() + "\t company:"
                                        + label.getCompanyName() + "\t index:"
                                        + label.getLabelIndex());
                            } else {
                                Log.i("test", "number:" + n);
                            }*/
                        }
                        // writer.flush();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (writer != null) {
                                writer.close();
                            }
                            if (reader != null) {
                                reader.close();
                            }
                            if (bufferedInputStream != null) {
                                bufferedInputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
            break;
        case R.id.json:
            json2();
            break;
        case R.id.service:
            Intent ser = new Intent(this, DoexService.class);
            ser.setAction(DoexService.EXTRA_SEND);
            startService(ser);
            break;
        case R.id.viewId:
//            startActivity(ViewActivity.class);
            break;
        case R.id.osId:
            startActivity(OSActivity.class);
            break;
        case R.id.theme:
            startActivity(SkinActivity.class);
            break;
        case R.id.system:
            startActivity(SystemActivity.class);
            break;
        case R.id.sms:
            startActivity(SmsActivity.class);
            break;
        }
    }

    private void json2() {
        String regex = FileUtils.getAssetFileContent(this, "regex");
        try {
            JSONObject obj = new JSONObject(regex);
            JSONObject response = obj.getJSONObject("response");
            JSONObject data = response.getJSONObject("data");
            JSONArray arr = data.getJSONArray("arr");
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getString(i));
            }

            String template = data.getString("temp");
            String message = "优惠总和?00.00分钟(时长);优惠剩余?00.00分钟(时长)";

            HashMap<String, String> valueMap = new HashMap<String, String>();

            Pattern p = Pattern.compile(data.getString("regex"));

            Matcher matcherParse = p.matcher(message);
            while (matcherParse.find()) {
                System.out.println(matcherParse.group() + "0000000000");
                for (int i = 0; i < list.size(); i++) {
                    String key = list.get(i);
                    System.out.println(key + ":key");
                    try {
                        String value = null;
                        if ((value = matcherParse.group(key)) != null) {
                            valueMap.put(key, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            p = Pattern.compile(TextUtils.join("|", list));
            matcherParse = p.matcher(template);
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (matcherParse.find()) {
                matcherParse.appendReplacement(sb, valueMap.get(list.get(i)));
                i++;
            }
            matcherParse.appendTail(sb);
            System.out.println(sb);
            System.out.println(valueMap.toString());
            System.out.println("----------------------------");
            Button btn = (Button) findViewById(R.id.json);
            btn.setText(Html.fromHtml(sb.toString()));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void json() {
        String regex = FileUtils.getAssetFileContent(this, "regex");
        try {
            JSONObject obj = new JSONObject(regex);
            JSONObject response = obj.getJSONObject("response");
            JSONObject data = response.getJSONObject("data");
            JSONArray arr = data.getJSONArray("arr");

            String t = "优惠总和?00.00分钟(时长);优惠剩余?00.00分钟(时长)";
            String template = data.getString("temp");
            ArrayList<String> list = new ArrayList<String>();
            list.add("yhzh");
            list.add("kyye");

            HashMap<String, String> valueMap = new HashMap<String, String>();
            Pattern p = Pattern
                    .compile("优惠总和??<yhzh>[0-9\\.]+)分钟[\\s\\S]时长[\\s\\S];优惠剩余??<kyye>[\\-0-9\\.]+)分钟[\\s\\S]时长[\\s\\S]");
            Matcher matcherParse = p.matcher(t);
            while (matcherParse.find()) {
                System.out.println(matcherParse.group() + "0000000000");
                for (int i = 0; i < list.size(); i++) {
                    String key = list.get(i);
                    try {
                        String value = null;
                        if ((value = matcherParse.group(key)) != null) {
                            valueMap.put(key, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            p = Pattern.compile("yhzh|kyye");
            matcherParse = p.matcher(template);
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (matcherParse.find()) {
                matcherParse.appendReplacement(sb, valueMap.get(list.get(i)));
                i++;
            }
            matcherParse.appendTail(sb);
            System.out.println(sb);
            System.out.println(valueMap.toString());
            System.out.println("----------------------------");
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

}
