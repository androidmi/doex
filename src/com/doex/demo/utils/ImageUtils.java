/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.doex.demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.doex.demo.model.PhoneLabel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

/**
 * @date July 24, 2010
 * @author Mark Wyszomierski (markww@gmail.com)
 */
public class ImageUtils {

    private ImageUtils() {
    }

    public static void resampleImageAndSaveToNewLocation(String pathInput, String pathOutput)
            throws Exception
    {
        Bitmap bmp = resampleImage(pathInput, 640);

        OutputStream out = new FileOutputStream(pathOutput);
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
    }

    public static Bitmap resampleImage(String path, int maxDim)
            throws Exception {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(),
                    maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk > 4) {
            int rotation = ExifUtils.getExifRotation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }

    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        }
        else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        }
        else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    private static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);

        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    public static BitmapFactory.Options getBitmapDims(String path) throws Exception {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);
        return bfo;
    }

    static boolean DEBUG = true;
    static String TAG = "sfd";

    public static PhoneLabel getServerMarkedLabel(Context context, String number) {
        DefaultHttpClient httpClient = new DefaultHttpClient(createHttpParams());
        String url = "http://tls.dxsvr.com/dxbb/2.0/report/query?p=" + number;
        Log.i(TAG, "url:" + url);
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if (DEBUG) {
                Log.i(TAG, "responseCode:" + responseCode);
            }
            if (responseCode == 200) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (DEBUG) {
                    Log.i(TAG, "result:" + result);
                }
                JSONObject obj = parseResponse(result);
                return PhoneLabel.create(obj);
            }
        } catch (ClientProtocolException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
                if (e instanceof SocketTimeoutException
                        || e instanceof ConnectTimeoutException) {
                    Log.i(TAG, "connection timeout");
                    return getServerMarkedLabel(context, number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PhoneLabel(false);
    }

    private static JSONObject parseResponse(String result) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        JSONObject jo;
        try {
            jo = new JSONObject(result);
            if (jo.has("responseHeader")) {
                JSONObject responseHeader = jo.optJSONObject("responseHeader");
                int statusCode = responseHeader.getInt("status");
                if (statusCode == 200) {
                    String hello = jo.getString("response");
                    Log.i(TAG, hello);
                    if (!TextUtils.isEmpty(jo.optString("response"))) {
                        return jo.optJSONObject("response");
                    }
                }
            }
        } catch (JSONException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static final HttpParams createHttpParams() {
        final int timeout = 2;
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, timeout * 1000);
        HttpConnectionParams.setSoTimeout(params, timeout * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        return params;
    }

}
