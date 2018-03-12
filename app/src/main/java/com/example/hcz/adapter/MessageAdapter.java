package com.example.hcz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hcz.hechangzhi.ComplaintsActivity;
import com.example.hcz.hechangzhi.InspectionActivity;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Message;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Message> messages;
    private String loginName, userInfo;

    public MessageAdapter(Context context, List<Message> messages, String loginName, String userInfo) {
        this.context = context;
        this.messages = messages;
        this.inflater = inflater.from(context);
        this.loginName = loginName;
        this.userInfo = userInfo;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (messages.size() != 0) {
            if (messages.get(position).getType().equals("抄送")) {
                MessageHolder holder = new MessageHolder();
                convertView = inflater.inflate(R.layout.item_message_cs, null);
                holder.time = convertView.findViewById(R.id.time);
                holder.river = convertView.findViewById(R.id.river);
                holder.user = convertView.findViewById(R.id.user);
                holder.con = convertView.findViewById(R.id.con);
                holder.status = convertView.findViewById(R.id.status);
                holder.main = convertView.findViewById(R.id.main);

                holder.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ComplaintsActivity.class)
                                .putExtra("loginName", loginName)
                                .putExtra("userInfo", userInfo));
                    }
                });

                holder.time.setText(messages.get(position).getTime());
                holder.river.setText(messages.get(position).getComplaintRiver());
                holder.user.setText(messages.get(position).getResponeRiver());
                holder.con.setText(messages.get(position).getCompCon());
                if (messages.get(position).getCompStatus() != null)
                    if (messages.get(position).getCompStatus().equals("1")) {
                        holder.status.setText("未处理");
                        holder.status.setTextColor(R.color.red);
                    } else if (messages.get(position).getCompStatus().equals("2")) {
                        holder.status.setText("处理中");
                        holder.status.setTextColor(R.color.o);
                    } else if (messages.get(position).getCompStatus().equals("3")) {
                        holder.status.setText("已处理");
                        holder.status.setTextColor(R.color.colorrivertxt);
                    } else if (messages.get(position).getCompStatus().equals("4")) {
                        holder.status.setText("已结案");
                        holder.status.setTextColor(R.color.blue);
                    }
            } else if (messages.get(position).getType().equals("投诉")) {
                MessageHolder holder = new MessageHolder();
                convertView = inflater.inflate(R.layout.item_message_ts, null);
                holder.time = convertView.findViewById(R.id.time);
                holder.river = convertView.findViewById(R.id.river);
                holder.con = convertView.findViewById(R.id.con);
                holder.main = convertView.findViewById(R.id.main);

                holder.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ComplaintsActivity.class)
                                .putExtra("loginName", loginName)
                                .putExtra("userInfo", userInfo));
                    }
                });

                holder.status = convertView.findViewById(R.id.status);
                holder.time.setText(messages.get(position).getTime());
                holder.river.setText(messages.get(position).getComplaintRiver());
                holder.con.setText(messages.get(position).getCompCon());
                if (messages.get(position).getCompStatus() != null)
                    if (messages.get(position).getCompStatus().equals("1")) {
                        holder.status.setText("未处理");
                        holder.status.setTextColor(R.color.red);
                    } else if (messages.get(position).getCompStatus().equals("2")) {
                        holder.status.setText("处理中");
                        holder.status.setTextColor(R.color.o);
                    } else if (messages.get(position).getCompStatus().equals("3")) {
                        holder.status.setText("已处理");
                        holder.status.setTextColor(R.color.colorrivertxt);
                    } else if (messages.get(position).getCompStatus().equals("4")) {
                        holder.status.setText("已结案");
                        holder.status.setTextColor(R.color.blue);
                    }
            } else if (messages.get(position).getType().equals("巡检")) {
                MessageHolder holder = new MessageHolder();
                convertView = inflater.inflate(R.layout.item_message_xj, null);
                holder.main = convertView.findViewById(R.id.main);
                holder.time = convertView.findViewById(R.id.time);

                holder.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, InspectionActivity.class)
                                .putExtra("loginName", loginName)
                                .putExtra("userInfo", userInfo));
                    }
                });

                holder.uninspection = convertView.findViewById(R.id.unInspection);
                holder.time.setText(messages.get(position).getTime());
                holder.uninspection.setText(messages.get(position).getUninspection());
            }

        }
        return convertView;
    }

    class MessageHolder {
        TextView time;
        TextView river;
        TextView user;
        TextView con;
        TextView status;
        TextView uninspection;
        LinearLayout main;
    }
}
