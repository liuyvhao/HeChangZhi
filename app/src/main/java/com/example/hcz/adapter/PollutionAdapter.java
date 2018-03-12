package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Pollution;

import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */
public class PollutionAdapter extends BaseAdapter {
    private List<Pollution> pollutions;
    private LayoutInflater layoutInflater;
    private Context context;

    public PollutionAdapter(Context context, List<Pollution> pollutions) {
        this.layoutInflater = layoutInflater.from(context);
        this.pollutions = pollutions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pollutions.size();
    }

    @Override
    public Object getItem(int position) {
        return pollutions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PollutionViewHolder pollutionViewHolder;
        if (convertView == null) {
            pollutionViewHolder = new PollutionViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_pollution, null);
            pollutionViewHolder.name = convertView.findViewById(R.id.name);
            pollutionViewHolder.position = convertView.findViewById(R.id.position);
            pollutionViewHolder.type = convertView.findViewById(R.id.type);
            convertView.setTag(pollutionViewHolder);
        } else {
            pollutionViewHolder = (PollutionViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            pollutionViewHolder.name.setText(pollutions.get(position).getName());
            pollutionViewHolder.position.setText(pollutions.get(position).getPosition());
            pollutionViewHolder.type.setText(pollutions.get(position).getType());
        }

        return convertView;
    }

    class PollutionViewHolder {
        TextView name;          //名称
        TextView position;      //地点
        TextView type;          //类型
    }
}
