
package com.dianxinos.demo.temp;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class PhoneLabelDataStroe {
    private static final PhoneLabelDataStroe INST = new PhoneLabelDataStroe();

    private Map<String, LabelModel> date = new HashMap<String, PhoneLabelDataStroe.LabelModel>();

    static class LabelModel {
        private int count;
        private String number;
        private String label;

        public String getNumber() {
            return number;
        }

        private LabelModel() {
        }

        public static LabelModel create(JSONArray array) {
            try {
                int hot = array.getInt(2);
                String phone = array.getString(0);
                String tag = array.getString(1);
                LabelModel model = new LabelModel();
                model.count = hot;
                model.number = phone;
                model.label = tag;
                return model;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public static void init(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONArray a = array.getJSONArray(i);
                LabelModel model = LabelModel.create(a);
                INST.date.put(model.getNumber(), model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
