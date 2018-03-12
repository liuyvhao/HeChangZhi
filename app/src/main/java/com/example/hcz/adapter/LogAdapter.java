package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Log;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 */
public class LogAdapter extends BaseAdapter {
    private List<Log> logs;
    private LayoutInflater layoutInflater;
    private Context context;

    public LogAdapter(Context context, List<Log> logs) {
        this.layoutInflater = layoutInflater.from(context);
        this.logs = logs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int position) {
        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogViewHolder logViewHolder;
        if (convertView == null) {
            logViewHolder = new LogViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_log, null);
            logViewHolder.logTitle = (TextView) convertView.findViewById(R.id.logTitle);
            logViewHolder.logTime = (TextView) convertView.findViewById(R.id.logTime);
            convertView.setTag(logViewHolder);
        } else {
            logViewHolder = (LogViewHolder) convertView.getTag();
        }

        //绑定数据
        if (getItem(position) != null) {
            logViewHolder.logTitle.setText(logs.get(position).getLogTitle());
            logViewHolder.logTime.setText(logs.get(position).getLogTime());
        }

        return convertView;
    }

    class LogViewHolder {
        TextView logTitle;      //日志标题
        TextView logTime;       //日志时间
    }
}
