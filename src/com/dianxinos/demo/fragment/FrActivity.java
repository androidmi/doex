
package com.dianxinos.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class FrActivity extends FragmentActivity implements LoaderCallbacks<String> {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        getSupportLoaderManager().destroyLoader(1);
    }

    @Override
    public Loader<String> onCreateLoader(int arg0, Bundle arg1) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> arg0, String arg1) {

    }

    @Override
    public void onLoaderReset(Loader<String> arg0) {
        // TODO Auto-generated method stub

    }
}
