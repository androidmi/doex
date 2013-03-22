
package com.dianxinos.demo.fragment;

import android.R.anim;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class FragmentInstance extends FragmentActivity {

    public static final String BUNDLE = "bundle";
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fragment = getIntent().getStringExtra(BUNDLE);
        Fragment frag;
        try {
            frag = (Fragment) Class.forName(fragment)
                    .newInstance();
            fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(anim.slide_in_left, anim.slide_out_right)
                    .replace(android.R.id.content, frag)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // fm.beginTransaction().
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
