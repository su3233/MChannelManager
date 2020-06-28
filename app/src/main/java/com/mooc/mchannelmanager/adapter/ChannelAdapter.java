package com.mooc.mchannelmanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mooc.mchannelmanager.R;

import java.util.List;

/**
 * @author SuTs
 * @create 2020/6/23 16:59
 * @Describe
 */
public class ChannelAdapter extends BaseAdapter {
    private static final String TAG = "ChannelAdapter";
    private Context context;
    private List<String> datas;
    /**
     * 是否可编辑
     */
    public static boolean isEditStatus;
    public boolean isUser;
    /**
     * 标记删除位置
     */
    private int readyRemove = -1;
    private AnimState animState = AnimState.EDIL;

    /**
     * 定义动画状态，空闲，移动中
     */
    enum AnimState {
        EDIL,
        TRANSTING
    }

    /**
     * @param context
     * @param datas
     * @param isUser  判断是那个grideView
     */
    public ChannelAdapter(Context context, List<String> datas, boolean isUser) {
        this.context = context;
        this.datas = datas;
        this.isUser = isUser;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        if (contentView == null) {
            contentView = LayoutInflater.from(context).inflate(R.layout.item_channel, null);
        }
        ViewHolder viewHolder = (ViewHolder) contentView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        }

        viewHolder.tv_item.setText(datas.get(position));
//根据当前是否为编辑状态，显示操作类型
        if (isEditStatus) {
            Log.e(TAG, "isUser: " + isUser);
            viewHolder.iv_item.setVisibility(View.VISIBLE);
            viewHolder.iv_item.setImageResource(isUser ? R.drawable.ic_icon_delete : R.drawable.ic_icon_add);
        } else {
            viewHolder.iv_item.setVisibility(View.INVISIBLE);
        }
        //判断currentView的状态
//        处理anotherView状态
        if (readyRemove == position || animState == AnimState.TRANSTING && position == getCount() - 1) {
            viewHolder.tv_item.setText("");
            viewHolder.tv_item.setSelected(true);
            viewHolder.iv_item.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tv_item.setText(datas.get(position));
            viewHolder.tv_item.setSelected(false);
        }
        return contentView;
    }

    /**
     * 添加数据
     *
     * @param name
     */
    public void add(String name) {
        datas.add(name);
        notifyDataSetChanged();
    }

    public void setTranslating(boolean translating) {
        animState = translating ? AnimState.TRANSTING : AnimState.EDIL;
    }

    /**
     * 添加删除标记
     *
     * @param index
     */
    public String setRemove(int index) {
        readyRemove = index;
        notifyDataSetChanged();
        return datas.get(index);
    }

    public void remove() {
        remove(readyRemove);
        readyRemove = -1;
    }

    /**
     * 删除某一个
     *
     * @param index
     */
    public void remove(int index) {
        if (index >= 0 && index < datas.size()) {
            datas.remove(index);
        }
        notifyDataSetChanged();
    }

    public static void setEdit(boolean isEdit) {
        isEditStatus = isEdit;
    }

    public static boolean isEidt() {
        return isEditStatus;
    }

    private class ViewHolder {
        TextView tv_item;
        ImageView iv_item;

        public ViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);
            iv_item = view.findViewById(R.id.iv_item_edit);
        }
    }


}
