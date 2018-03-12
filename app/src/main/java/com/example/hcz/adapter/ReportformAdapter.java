package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Reportform;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */
public class ReportformAdapter extends BaseAdapter {
    private List<Reportform> reportforms;
    private Context context;
    private LayoutInflater layoutInflater;

    public ReportformAdapter(List<Reportform> reportforms, Context context) {
        this.reportforms = reportforms;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reportforms.size();
    }

    @Override
    public Object getItem(int position) {
        return reportforms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReportformHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_reportfrom, null);
            holder = new ReportformHolder();
            holder.userImg = convertView.findViewById(R.id.userImg);
            holder.chiefName = convertView.findViewById(R.id.chiefName);
            holder.chiefTypeName = convertView.findViewById(R.id.chiefTypeName);
            holder.pidInspectionCount = convertView.findViewById(R.id.pidInspectionCount);
            holder.pidInspectionSum = convertView.findViewById(R.id.pidInspectionSum);
            holder.inspectionRate = convertView.findViewById(R.id.inspectionRate);
            holder.pidDaily = convertView.findViewById(R.id.pidDaily);

            convertView.setTag(holder);
        }else {
            holder= (ReportformHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.userImg.setImageURI(reportforms.get(position).getAvatarUrl());
            holder.chiefName.setText(reportforms.get(position).getChiefName());
            holder.chiefTypeName.setText(reportforms.get(position).getChiefTypeName());
            holder.pidInspectionCount.setText(reportforms.get(position).getPidInspectionCount());
            holder.pidInspectionSum.setText(reportforms.get(position).getPidInspectionSum());
            holder.inspectionRate.setText(reportforms.get(position).getInspectionRate());
            holder.pidDaily.setText(reportforms.get(position).getPidDaily());
        }

        return convertView;
    }

    class ReportformHolder {
        SimpleDraweeView userImg;
        TextView chiefName;
        TextView chiefTypeName;
        TextView pidInspectionCount;
        TextView pidInspectionSum;
        TextView inspectionRate;
        TextView pidDaily;
    }
}
