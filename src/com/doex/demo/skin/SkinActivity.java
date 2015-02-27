package com.doex.demo.skin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doex.demo.R;

import java.lang.reflect.Method;

public class SkinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.skin, menu);
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
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener{

        private TextView mTextView;
        private ImageView mImageView;
        private Button mButton;
        private Button mChangeButton;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_skin, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mTextView = (TextView) getActivity().findViewById(R.id.text);
            mImageView = (ImageView) getActivity().findViewById(R.id.imageview);
            mButton = (Button) getActivity().findViewById(R.id.btn);
            mChangeButton = (Button) getActivity().findViewById(R.id.changeBtn);
            mChangeButton.setOnClickListener(this);
        }

        private void loadJar() {
            AssetManager assetManager = null;
            try {
                String dexPath = "/sdcard/test/app-debug.apk";
                assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, dexPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Resources superRes = super.getResources();
            Resources res = new Resources(assetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            Resources.Theme theme = res.newTheme();
            theme.setTo(super.getActivity().getTheme());

            int tColorId = res.getIdentifier("text1", "color", "com.dx.skin");
            int tColor = res.getColor(tColorId);
        }

        private Context getSharedContext() {
            Context c = null;
            try {
                c = getActivity().createPackageContext("com.dx.skin", Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return c;
        }

        private Resources getSharedResources() {
            return getSharedContext().getResources();
        }

        private int getColor(String name) {
            return getSharedResources().getIdentifier(name, "color", "com.dx.skin");
        }

        private int getDrawableId(String name) {
            return getSharedResources().getIdentifier(name, "drawable", "com.dx.skin");
        }

        private Drawable getDrawable(String name) {
            return getSharedResources().getDrawable(getDrawableId(name));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.changeBtn:
                    mTextView.setTextColor(getColor("textColor"));
                    mButton.setBackgroundDrawable(getDrawable("btn_bkg"));
                    mImageView.setBackgroundDrawable(getDrawable("jr_icon"));
                    break;
            }
        }
    }
}
