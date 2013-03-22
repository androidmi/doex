
package com.dianxinos.demo.network;

import android.os.AsyncTask;
import android.os.Environment;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadManager {

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
}
