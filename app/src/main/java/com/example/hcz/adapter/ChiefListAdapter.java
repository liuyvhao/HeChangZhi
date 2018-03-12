package com.example.hcz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.ChiefList;

import java.util.List;

/**
 * Created by Administrator on 2018/1/29.
 */

public class ChiefListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ChiefList> chiefLists;

    public ChiefListAdapter(Context context, List<ChiefList> chiefLists) {
        this.context = context;
        this.chiefLists = chiefLists;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chiefLists.size();
    }

    @Override
    public Object getItem(int position) {
        return chiefLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChiefViewHolder holder;
        if (convertView == null) {
            holder = new ChiefViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_baseinfo, null);
            holder.tel1 = convertView.findViewById(R.id.tel1);
            holder.tel2 = convertView.findViewById(R.id.tel2);
            holder.tel3 = convertView.findViewById(R.id.tel3);
            holder.riverName = convertView.findViewById(R.id.riverName);
            holder.riverContact = convertView.findViewById(R.id.riverContact);
            holder.riverLevel = convertView.findViewById(R.id.riverLevel);
            holder.contactDepartment = convertView.findViewById(R.id.contactDepartment);
            convertView.setTag(holder);
        } else {
            holder = (ChiefViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.riverName.setText(chiefLists.get(position).getChiefName());
            holder.riverContact.setText(chiefLists.get(position).getLinkman());
            holder.riverLevel.setText(chiefLists.get(position).getJob());
            holder.contactDepartment.setText(chiefLists.get(position).getDepartment());
            holder.tel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + chiefLists.get(position).getPhone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            holder.tel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + chiefLists.get(position).getLinkmanPhone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            holder.tel3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + chiefLists.get(position).getDepartmentphone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    class ChiefViewHolder {
        ImageView tel1;
        ImageView tel2;
        ImageView tel3;
        TextView riverName;
        TextView riverContact;
        TextView riverLevel;
        TextView contactDepartment;
    }
}
