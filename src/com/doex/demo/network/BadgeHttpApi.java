
package com.doex.demo.network;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class BadgeHttpApi {
    private static final String TAG = "BadgeHttpApi";
    private static final int TIMEOUT = 60;
    private static DefaultHttpClient mHttpClient;
    private static BadgeHttpApi sInstance;
    private Context mContext;

    public static synchronized BadgeHttpApi getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BadgeHttpApi(context.getApplicationContext());
        }
        return sInstance;
    }

    private BadgeHttpApi(Context context) {
        mContext = context;
        mHttpClient = new DefaultHttpClient(createHttpParams());
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs),
                HTTP.UTF_8);
        Log.i(TAG, "query=" + query);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        return httpGet;
    }

    public synchronized String doHttpRequest(HttpRequestBase httpRequest) {
        try {
            if (!NetworkUtils.isNetworkAvaialble(mContext)) {
                return null;
            }
            HttpResponse response = mHttpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.i(TAG, "response:" + result);
                return result;
            } else {
                Log.e(TAG, "server error :" + statusCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connect error:" + e.getMessage());
        }
        return null;
    }

    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                params.add(param);
            }
        }
        return params;
    }

    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }
}
