package com.mooc.mchannelmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;


import com.mooc.mchannelmanager.adapter.ChannelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String JSON_FILE_NAME = "data.json";
    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initData();
        initView();
    }

    private void initData() {
        try {
            InputStream inputStream = getAssets().open(JSON_FILE_NAME);
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            String json = new String(buffer, "utf-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray userArray = jsonObject.optJSONArray("user");
            JSONArray otherArray = jsonObject.optJSONArray("other");
            for (int i = 0; i < userArray.length(); i++) {
                userList.add(userArray.optString(i));
            }
            for (int i = 0; i < otherArray.length(); i++) {
                otherList.add(otherArray.optString(i));
            }
            Log.e(TAG, "initData: " + userList.size() + ".." + otherList.size());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        ImageView iv_edit = findViewById(R.id.iv_edit);
        GridView gv_my = findViewById(R.id.gv_my);
        GridView gv_other = findViewById(R.id.gv_more);
        gv_my.setAdapter(new ChannelAdapter(mContext, userList));
        gv_other.setAdapter(new ChannelAdapter(mContext, otherList));
    }
}