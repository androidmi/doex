
package com.doex.demo.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static JSONArray getData(Context context) {
        String json = FileUtils.getAssetFileContent(context, "ringonce_tag.json");
        JSONObject obj;
        try {
            obj = new JSONObject(json);
            return obj.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
