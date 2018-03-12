package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Jinsperction;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class JinsperctionAdapter extends BaseAdapter {
    private List<Jinsperction> jinsperctions;
    private LayoutInflater layoutInflater;
    private Context context;

    public JinsperctionAdapter(Context context, List<Jinsperction> jinsperctions) {
        this.jinsperctions = jinsperctions;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return jinsperctions.size();
    }

    @Override
    public Object getItem(int position) {
        return jinsperctions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JinperctionViewHolder holder;
        if (convertView == null) {
            holder = new JinperctionViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_jinsperction, null);
            holder.logTitle = convertView.findViewById(R.id.logTitle);
            holder.logTime = convertView.findViewById(R.id.logTime);
            convertView.setTag(holder);
        } else {
            holder = (JinperctionViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.logTitle.setText(jinsperctions.get(position).getChiefName());
            holder.logTime.setText(jinsperctions.get(position).getTime());
        }

        return convertView;
    }

    class JinperctionViewHolder {
        TextView logTitle;      //标题
        TextView logTime;       //时间
    }
}
