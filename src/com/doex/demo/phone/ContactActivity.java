
package com.doex.demo.phone;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.doex.demo.MainFragment;
import com.doex.demo.R;

public class ContactActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = "ContactActivity";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.phone_view);
        findViewById(R.id.phone).setOnClickListener(this);
        findViewById(R.id.contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone:


                MainFragment.startActivity(this, PhoneFragment.class.getName());
                break;
            case R.id.contact:
                MainFragment.startActivity(this, ContactFragment.class.getName());
                break;
        }
    }
}
