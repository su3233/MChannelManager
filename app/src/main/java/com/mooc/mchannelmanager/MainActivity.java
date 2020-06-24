package com.mooc.mchannelmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mooc.mchannelmanager.adapter.ChannelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private static final String JSON_FILE_NAME = "data.json";
    private static final int ANIM_DURATION = 300;
    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();
    private Context mContext;
    private TextView tvMore;
    private GridView gv_user;
    private GridView gv_other;
    private ChannelAdapter userAdapter;
    private ChannelAdapter otherAdapter;


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
        tvMore = findViewById(R.id.tv_more);
        gv_user = findViewById(R.id.gv_my);
        gv_other = findViewById(R.id.gv_more);
        userAdapter = new ChannelAdapter(mContext, userList, true);
        otherAdapter = new ChannelAdapter(mContext, otherList, false);

        gv_user.setAdapter(userAdapter);
        gv_other.setAdapter(otherAdapter);
        gv_user.setOnItemClickListener(this);
        gv_other.setOnItemClickListener(this);

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                toogleEditState();
                ((ImageView) view).setImageResource(ChannelAdapter.isEditStatus ?
                        R.drawable.ic_icon_edit_ok : R.drawable.ic_icon_edit);
            }
        });
    }

    /**
     * 切换编辑状态
     */
    private void toogleEditState() {
        boolean isEdit = ChannelAdapter.isEidt();
        ChannelAdapter.setEdit(!isEdit);

        tvMore.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        gv_other.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        userAdapter.notifyDataSetChanged();
        otherAdapter.notifyDataSetChanged();
    }

    /**
     * 给View添加移动动画
     *
     * @param moveView
     * @param startPos
     * @param endPos
     * @param duration
     */
    private void moveAnimation(final View moveView, int[] startPos, int[] endPos, int duration) {
        TranslateAnimation translateAnimation = new TranslateAnimation(startPos[0], endPos[0], startPos[1], endPos[1]);
        translateAnimation.setDuration(duration);
        translateAnimation.setFillAfter(false);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束移除cloneView
                ((ViewGroup) moveView.getParent()).removeView(moveView);
                resetAdapter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        moveView.startAnimation(translateAnimation);
    }

    /**
     * 操作后重置状态
     */
    private void resetAdapter() {
        userAdapter.setTranslating(false);
        otherAdapter.setTranslating(false);
        userAdapter.remove();
        otherAdapter.remove();
    }

    /**
     * 克隆view实现动画
     *
     * @param view
     * @return
     */
    private ImageView getCloneView(View view) {
//        view.destroyDrawingCache();
//        view.setDrawingCacheEnabled(true);
//        Bitmap cache=Bitmap.createBitmap(view.getDrawingCache());
//        ImageView imageView=new ImageView(this);
//        imageView.setImageBitmap(cache);

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        ImageView imageView = new ImageView(this);
        return imageView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //如果是编辑状态处理删除和增加操作
        //不是编辑状态弹出Toast，事记使用中可以换成频道详情页跳转
        if (ChannelAdapter.isEditStatus) {
            GridView currentView;
            final GridView anotherView;
            if (adapterView == gv_user) {
                currentView = gv_user;
                anotherView = gv_other;
            } else {
                currentView = gv_other;
                anotherView = gv_user;
            }
            //计算起点
            final int[] startPos = new int[2];
            final int[] endPos = new int[2];
            view.getLocationInWindow(startPos);

            ChannelAdapter currnetAdapter = (ChannelAdapter) currentView.getAdapter();
            ChannelAdapter anotherAdapter = (ChannelAdapter) anotherView.getAdapter();
            //添加在动画之前，删除在动画之后
            anotherAdapter.setTranslating(true);
            anotherAdapter.add(currnetAdapter.setRemove(position));
            final ImageView cloneView = getCloneView(view);
            ((ViewGroup) getWindow().getDecorView()).addView(cloneView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            currentView.post(new Runnable() {
                @Override
                public void run() {
                    View lastView = anotherView.getChildAt(anotherView.getCount() - 1);
                    lastView.getLocationInWindow(endPos);
                    moveAnimation(cloneView, startPos, endPos, ANIM_DURATION);
                }
            });
        } else {
            Toast.makeText(mContext, userList.get(position), Toast.LENGTH_SHORT).show();
        }
    }


}