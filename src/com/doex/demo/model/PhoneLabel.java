
package com.doex.demo.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PhoneLabel implements Serializable {
    private static final long serialVersionUID = -7858219028182511744L;
    private static final boolean DEBUG = true;

    private String mNumber;
    private int mCount;
    private int mLabelIndex;
    private String mLabel;
    private String mCompanyName;
    private boolean mMarkedByUser;

    private boolean mIsOk;

    /**
     * 号码标记构造方法
     * 
     * @param number 电话号码
     * @param label 号码标记
     * @param count 标记次数
     * @param labelIndex 标记对应的索引
     * @param markedByUser 是否为用户手动添加的标记，true，为用户主动添加的，反之不是
     */
    public PhoneLabel(String number, String label, int count, int labelIndex, boolean markedByUser) {
        mNumber = number;
        mCount = count;
        mLabelIndex = labelIndex;
        mLabel = label;
        mMarkedByUser = markedByUser;
    };

    public PhoneLabel(boolean isOk) {
        mIsOk = isOk;
    };

    /**
     * 号码标记构造方法
     * 
     * @param number 电话号码
     * @param label 号码标记
     * @param count 标记次数
     * @param labelIndex 标记对应的索引
     * @param companyName 号码对应的云端标记
     */
    public PhoneLabel(String number, String label, int count, int labelIndex, String companyName) {
        mNumber = number;
        mCount = count;
        mLabelIndex = labelIndex;
        mLabel = label;
        mCompanyName = companyName;
    };

    public static PhoneLabel fromString(String phoneLabel) {
        // 4008688888|0|2584
        if (TextUtils.isEmpty(phoneLabel)) {
            return null;
        }
        try {
            String[] splits = phoneLabel.split("\\|");
            PhoneLabel model = new PhoneLabel();
            model.mNumber = splits[0];
            model.mLabelIndex = Integer.parseInt(splits[1]);
            model.mCount = Integer.parseInt(splits[2]);
            return model;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PhoneLabel fromJson(JSONObject item) {
        if (item == null) {
            return null;
        }
        PhoneLabel model = new PhoneLabel();
        model.mNumber = item.optString("phone");
        model.mLabel = item.optString("tag");
        model.mLabelIndex = item.optInt("tagId");
        model.mCount = item.optInt("count");
        return model;
    }

    public static PhoneLabel create(JSONObject obj) {
        if (obj == null) {
            return new PhoneLabel(false);
        }
        PhoneLabel model = new PhoneLabel();
        try {
            if (obj.has("company")) {
                JSONObject o = obj.getJSONObject("company");
                model.mCompanyName = o.getString("name");
            } else if (obj.has("type")) {
                JSONObject o = obj.getJSONObject("type");
                model.mCount = o.getInt("count");
                model.mLabelIndex = o.getInt("id");
                String label = "";
                if (TextUtils.isEmpty(label)) {
                    label = o.getString("name");
                }
                model.mLabel = label;
            } else {
                model.mIsOk = false;
                return model;
            }
            model.mIsOk = true;
            model.mNumber = obj.getString("phone");
        } catch (JSONException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            model.mIsOk = false;
        }
        return model;
    }

    private PhoneLabel() {
    }

    public int getLabelIndex() {
        return mLabelIndex;
    }

    /**
     * 获取号码标记
     * 
     * @param context android上下文
     * @return 号码标记
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * 获取号码
     * 
     * @return 电话号码
     */
    public String getNumber() {
        return mNumber;
    }

    /**
     * 获取标记次数
     * 
     * @return 标记次数
     */
    public int getCount() {
        return mCount;
    }

    /**
     * 获取号码对应的单位名称
     * 
     * @return 单位名称
     */
    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    /**
     * 判断是否为用户手动添加的标记
     * 
     * @return true，用户手动添加的标记，反之不是
     */
    public boolean isMarkedByUser() {
        return mMarkedByUser;
    }

    /**
     * 设置是否为手动添加的标记
     * 
     * @param markedByUser true，用户手动添加的标记，反之不是
     */
    public void setMarkedByUser(boolean markedByUser) {
        this.mMarkedByUser = markedByUser;
    }

    public boolean isOK() {
        return mIsOk;
    }

    public String toString() {
        return "PhoneLabelMappingModel:[mLabelNum=" + mLabelIndex + ",mNumber=" + mNumber
                + ",mCount=" + mCount + "]";
    }

}
