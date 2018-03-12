package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.HistoryInspection;

import java.util.List;

/**
 * 历史轨迹适配器
 * Created by Administrator on 2017/11/28.
 */
public class HistoryInspectionAdapter extends BaseAdapter {
    private List<HistoryInspection> historyInspections;
    private Context context;
    private LayoutInflater layoutInflater;

    public HistoryInspectionAdapter(Context context, List<HistoryInspection> historyInspections) {
        this.historyInspections = historyInspections;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return historyInspections.size();
    }

    @Override
    public Object getItem(int position) {
        return historyInspections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryInsViewHolder holder;
        if (convertView == null) {
            holder = new HistoryInsViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_history_inspection, null);
            holder.beginTime = convertView.findViewById(R.id.beginTime);
            holder.endTime = convertView.findViewById(R.id.endTime);
            holder.time = convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (HistoryInsViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.beginTime.setText(historyInspections.get(position).getBeginTime());
            holder.endTime.setText(historyInspections.get(position).getEndTime());
        }

        // 当前字母
        String currentStr = historyInspections.get(position).getTime();
        // 前面的字母
        String previewStr = (position - 1) >= 0 ? historyInspections.get(position - 1).getTime() : " ";

        if (!previewStr.equals(currentStr)) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(currentStr);
        } else {
            holder.time.setVisibility(View.GONE);
        }

        return convertView;
    }

    class HistoryInsViewHolder {
        TextView beginTime;         //开始时间
        TextView endTime;           //结束时间
        TextView time;
    }
}
