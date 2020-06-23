package com.mooc.mchannelmanager.adapter;

import android.content.Context;
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
    private Context context;
    private List<String> datas;

    public ChannelAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
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
    public View getView(int position, View content, ViewGroup viewGroup) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_channel, null);
        TextView tv_item = layout.findViewById(R.id.tv_item);
        ImageView iv_item = layout.findViewById(R.id.iv_item_edit);
        tv_item.setText(datas.get(position));

        if (position % 2 == 0) {
            iv_item.setImageResource(R.drawable.ic_icon_add);
        } else {
            iv_item.setImageResource(R.drawable.ic_icon_delete);
        }
        return layout;
    }
}
