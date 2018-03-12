package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.WadiInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */
public class LowRiverAdapter extends BaseAdapter {
    private List<WadiInfo> wadiInfos;
    private LayoutInflater layoutInflater;
    private Context context;

    public LowRiverAdapter(Context context, List<WadiInfo> wadiInfos) {
        this.wadiInfos = wadiInfos;
        this.layoutInflater = layoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return wadiInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return wadiInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LowRiverViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new LowRiverViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_lowriver, null);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.river_type = convertView.findViewById(R.id.river_type);
            viewHolder.right = convertView.findViewById(R.id.right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LowRiverViewHolder) convertView.getTag();
        }

        //绑定数据
        if (getItem(position) != null) {
            viewHolder.name.setText(wadiInfos.get(position).getName());
            viewHolder.river_type.setText(wadiInfos.get(position).getRiver_type());
        }

        if (position % 2 != 0)
            viewHolder.right.setVisibility(View.GONE);

        return convertView;
    }

    class LowRiverViewHolder {
        TextView name;               //河道名称
        TextView river_type;        //河道级别
        View right;
    }
}
