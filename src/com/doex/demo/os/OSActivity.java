package com.doex.demo.os;

import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.doex.demo.R;

public class OSActivity extends FragmentActivity {

    private static final String TAG = "OSActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.o, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_o, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAirMode();
                }
            });
            isSimCardReady();
            devicesInfo();
            getSlotId();

        }
        private void devicesInfo() {
            TextView textView = (TextView) getActivity().findViewById(R.id.text);
            StringBuilder sb = new StringBuilder();
            String model = Build.MODEL;
            sb.append("model:" + model);
            sb.append("\n");
            String manufacturer = Build.MANUFACTURER;
            sb.append("manufacturer:" + manufacturer);
            sb.append("\n");
            String devices = Build.DEVICE;
            sb.append("devices:" + devices);
            sb.append("\n");
            String board = Build.BOARD;
            sb.append("board:" + board);
            sb.append("\n");
            String brand = Build.BRAND;
            sb.append("brand:" + brand);
            sb.append("\n");
            String display = Build.DISPLAY;
            sb.append("display:" + display);
            sb.append("\n");
            String hardware = Build.HARDWARE;
            sb.append("hardware:" + hardware);
            sb.append("\n");
            String bootloader = Build.BOOTLOADER;
            sb.append("bootloader:" + bootloader);
            sb.append("\n");
            String fingerprint = Build.FINGERPRINT;
            sb.append("fingerprint:" + fingerprint);
            sb.append("\n");
            textView.setText(sb);
        }

        private void isAirMode() {
            boolean isAirplaneMode = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
            Toast.makeText(getActivity(), "isAirMode:"+isAirplaneMode, Toast.LENGTH_SHORT).show();
        }

        private void isSimCardReady() {
            TelephonyManager tm = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            int state = tm.getSimState();
            Log.i(TAG, "state:" + state);
        }

        private void getSlotId() {
            TelephonyManager tm = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            Log.i(TAG, "deviceId:"+deviceId);
        }

        private void power() {
            PowerManager pm =
                    (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "StartingAlertService");
            wakeLock.setReferenceCounted(false);
        }
    }
}
