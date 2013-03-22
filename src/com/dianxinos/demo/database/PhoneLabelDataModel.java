
package com.dianxinos.demo.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhoneLabelDataModel {
    private JSONArray mData;
    private JSONObject mLabelMapping;
    private int mCount;

    public static final int NUMBER = 0;
    public static final int LABEL = 1;
    public static final int COUNT = 2;

    private PhoneLabelDataModel() {
    }

    public static PhoneLabelDataModel fromJson(JSONObject obj) {
        PhoneLabelDataModel model = null;
        try {
            model = new PhoneLabelDataModel();
            model.mData = obj.getJSONArray("data");
            model.mLabelMapping = obj.getJSONObject("tag");
            model.mCount = obj.getInt("count");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return model;
    }

    public JSONArray getData() {
        return mData;
    }

    public JSONObject getMapping() {
        return mLabelMapping;
    }

    public int getCount() {
        return mCount;
    }
}
