package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.River;

import java.util.List;

/**
 * Created by Administrator on 2018/1/23.
 */
public class RiverAdapter extends BaseAdapter {
    private Context context;
    private List<River> riverList;
    private LayoutInflater layoutInflater;

    public RiverAdapter(Context context, List<River> riverList) {
        this.context = context;
        this.riverList = riverList;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return riverList.size();
    }

    @Override
    public Object getItem(int position) {
        return riverList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RiverViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_river, null);
            holder = new RiverViewHolder();
            holder.river = convertView.findViewById(R.id.river);
            convertView.setTag(holder);
        } else {
            holder = (RiverViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.river.setText(riverList.get(position).getName());
        }
        return convertView;
    }

    class RiverViewHolder {
        TextView river;
    }
}
