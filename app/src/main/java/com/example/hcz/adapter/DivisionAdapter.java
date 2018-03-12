package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Division;

import java.util.List;

/**
 * Created by Administrator on 2017/12/7.
 */

public class DivisionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Division> divisions;

    public DivisionAdapter(Context context, List<Division> divisions) {
        this.context = context;
        this.divisions = divisions;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return divisions.size();
    }

    @Override
    public Object getItem(int position) {
        return divisions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DivisionViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_division, null);
            holder = new DivisionViewHolder();
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (DivisionViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.textView.setText(divisions.get(position).getName());
            if (divisions.get(position).isCheck())
                holder.textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            else
                holder.textView.setTextColor(context.getResources().getColor(R.color.black));

        }

        return convertView;
    }

    class DivisionViewHolder {
        TextView textView;
    }
}


