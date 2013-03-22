
package com.dianxinos.demo.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;

import com.dianxinos.demo.storage.StorageDatabase;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Temp {
    private static final String TAG = "Util";

    public void getTop10(Context context) {
        long start = System.currentTimeMillis();
        Cursor cursor = StorageDatabase.getInstance(context).getTop10();
        long duration = System.currentTimeMillis() - start;
        Log.i(TAG, "get top 10 duration:" + duration);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        do {
            String number = cursor.getString(1);
            int label = cursor.getInt(2);
            int count = cursor.getInt(3);
            Log.i(TAG, number + ":" + label + ":" + count);
        } while (cursor.moveToNext());
    }

    public void readLine(Context context) {
        try {
            InputStream in = context.getAssets().open("line.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String sb = null;
            Log.i("test", "begin");
            long start = System.currentTimeMillis();
            while ((sb = reader.readLine()) != null) {
                String[] spli = sb.split(",");
                Log.i("test", spli[0] + ":" + spli[1] + ":" + spli[2]);
                StorageDatabase.getInstance(context).insertValues(spli[0],
                        Integer.parseInt(spli[1]),
                        Integer.parseInt(spli[2]));
            }
            long duratin = System.currentTimeMillis() - start;
            Log.i("test", "duration:" + duratin);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void json(Context context) {
        InputStream in = null;
        try {
            in = context.getAssets().open("json_40.txt");
            byte[] buffer = null;
            buffer = new byte[in.available()];
            while (in.read(buffer) != -1)
                ;
            String content = new String(buffer);
            JSONArray array = new JSONArray(content);
            Log.i("test", "start");
            long start = System.currentTimeMillis();
            // StorageDatabase.getInstance(context).insert(array);
            for (int i = 0; i < array.length(); i++) {
                StringBuilder sb = new StringBuilder();
                JSONArray a = array.getJSONArray(i);
                int hot = a.getInt(2);
                String phone = a.getString(0);
                String tag = a.getString(1);
            }

            long duration = System.currentTimeMillis() - start;
            Log.i("test", "duration:" + duration);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void text(EditText editText) {
        editText.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(10),
                new InputFilter() {

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                            Spanned dest, int dstart,
                            int dend) {
                        String regEx = "^[A-Za-z0-9\u4e00-\u9fa5]+$";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(source);
                        Log.i(TAG, "source:" + source);
                        Log.i(TAG, "-start:" + start + "-end:" + end
                                + "-dstart:" + dstart + "-dend:" + dend);
                        if (!m.find()) {
                            return "";
                        }
                        return null;
                    }
                }
        });
    }

}
