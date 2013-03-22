
package com.dianxinos.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dianxinos.demo.R;
import com.dianxinos.demo.model.UserModel;

public class MenuAct extends Activity {

    private static final String TAG = "MenuAct";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent in = getIntent();
        UserModel model = in.getParcelableExtra("key");
        Log.i(TAG, "name:" + model.getName());
        Log.i(TAG, "age:" + model.getAge());
        Log.i(TAG, "birth:" + model.getBirthTime());
        Log.i(TAG, "address:" + model.getAddress());
        setMenuBackground();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String info = "";
        switch (item.getItemId()) {
            case R.id.menu_add:
                info = "Add";
                break;
            case R.id.menu_delete:
                info = "Delete";
                break;
            case R.id.menu_home:
                info = "Home";
                break;
            case R.id.menu_help:
                info = "Help";
                break;
            default:
                info = "NULL";
                break;
        }
        Toast toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        toast.show();
        return super.onOptionsItemSelected(item);
    }

    // 关键代码为重写Layout.Factory.onCreateView()方法自定义布局
    protected void setMenuBackground() {
        getLayoutInflater().setFactory(new android.view.LayoutInflater.Factory() {
            /**
             * name - Tag name to be inflated.<br/>
             * context - The context the view is being created in.<br/>
             * attrs - Inflation attributes as specified in XML file.<br/>
             */
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                Log.i(TAG, "name:" + name);
                // 指定自定义inflate的对象
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
                    try {
                        LayoutInflater f = LayoutInflater.from(context);
                        final View view = f.createView(name, null, attrs);
                        new Handler().post(new Runnable() {
                            public void run() {
                                // 设置背景图片
                                // view.setBackgroundResource(R.drawable.menu_background);
                                view.setBackgroundColor(Color.RED);
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
    }
}
