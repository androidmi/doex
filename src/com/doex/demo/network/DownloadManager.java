
package com.doex.demo.network;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadManager {
    private static final String TAG = "DownloadManager";
    public static void download() {
        new DownloadTask().execute();
    }

    static class DownloadTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            fileDownload();
            return null;
        }
    }

    public static void fileDownload() {
        String url = "http://img4.chinaface.com/original/2129WcMqoRfWyWMkLqeb47zDsa0WA.jpg";
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator
                + "download" + File.separator + "hello.jpg";
        OutputStream os = null;
        HttpGet getReq;
        getReq = new HttpGet(url);
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            os = new FileOutputStream(path);
            HttpResponse response = client.execute(getReq);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[128];
                int read = -1;
                int totle = 480 * 1024;
                int download = 0;
                while ((read = in.read(buffer)) != -1) {
                    download += read;
                    int percent = download * 100 / totle;
                    System.out.println(download + ":" + percent);
                    os.write(buffer, 0, read);
                }
            } else {
                System.out.println("error:" + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            // NullPointerException may be thrown in extreme situation, dut
            // to bugs of apache's DefaultHttpClient.
            t.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class thread extends Thread {

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static final int TIMEOUT = 1;

    private final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }

}
