
package com.doex.demo.network;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.doex.demo.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class NetWorkFragment extends Fragment implements OnClickListener {
    private static final int TIMEOUT = 1;
    private static final String TAG = "NetWorkFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.network_view, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.query).setOnClickListener(this);
    }

    private final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        return params;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query:
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        DefaultHttpClient mHttpClient = new DefaultHttpClient(createHttpParams());
                        String url = "http://t1.tira.cn:8125/dxbb/report/query/queryByPhone?phone=02195511";
                        HttpGet httpGet = new HttpGet(url);
                        try {
                            HttpResponse response = mHttpClient.execute(httpGet);
                            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                            Log.i(TAG, "result:" + result);
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                            Log.i(TAG, "time out");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "IOException");
                            if (e instanceof SocketTimeoutException
                                    || e instanceof ConnectTimeoutException) {
                                Log.i(TAG, "time out");
                            }
                        }
                    }
                }).start();
                break;
        }
    }
}
