
package com.doex.demo.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.doex.demo.database.DatabaseFragment;
import com.doex.demo.storage.StorageFragment;

import java.util.ArrayList;

public class FragmentPager extends FragmentActivity {
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager = new ViewPager(this);
        mViewPager.setId(20);
        mViewPager.setAdapter(new TabsAdapter(this));
        setContentView(mViewPager);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements ViewPager.OnPageChangeListener {
        private final ArrayList<String> mTabs = new ArrayList<String>();

        Fragment[] f = {
                new StorageFragment(), new DatabaseFragment()
        };

        public TabsAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return f.length;
        }

        @Override
        public Fragment getItem(int position) {
            return f[position];
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub

        }

    }
}
