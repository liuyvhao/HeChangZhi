package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;

/**
 * Created by Administrator on 2017/12/5.
 */

public class CopyerAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private String[] copy_list;

    public CopyerAdapter(Context context, String[] copy_list) {
        this.context = context;
        this.copy_list = copy_list;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return copy_list.length;
    }

    @Override
    public Object getItem(int position) {
        return copy_list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CopyerViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_cpoer, null);
            holder = new CopyerViewHolder();
            holder.name_c = convertView.findViewById(R.id.name_c);
            convertView.setTag(holder);
        } else {
            holder = (CopyerViewHolder) convertView.getTag();
        }

        if (getItem(position) != null)
            holder.name_c.setText(copy_list[position]);

        return convertView;
    }

    class CopyerViewHolder {
        TextView name_c;
    }
}
