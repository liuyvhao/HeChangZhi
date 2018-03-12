package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Complaints;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;

/**
 * 投诉信息适配器
 * Created by Administrator on 2017/11/23.
 */
public class ComplaintsAdapter extends BaseAdapter {
    private List<Complaints> complaintses;
    private LayoutInflater layoutInflater;
    private Context context;

    public ComplaintsAdapter(Context context, List<Complaints> complaintses) {
        this.complaintses = complaintses;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return complaintses.size();
    }

    @Override
    public Object getItem(int position) {
        return complaintses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ComplaintsViewHolder complaintsViewHolder;
//        if (convertView == null) {
            complaintsViewHolder = new ComplaintsViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_complaints, null);
            complaintsViewHolder.compImg = convertView.findViewById(R.id.compImg);
            complaintsViewHolder.riverName = convertView.findViewById(R.id.riverName);
            complaintsViewHolder.compTitle = convertView.findViewById(R.id.compTitle);
            complaintsViewHolder.compTime = convertView.findViewById(R.id.compTime);
            complaintsViewHolder.compStatus = convertView.findViewById(R.id.compStatus);
            convertView.setTag(complaintsViewHolder);
//        } else {
//            complaintsViewHolder = (ComplaintsViewHolder) convertView.getTag();
//        }

        ImageView chao = convertView.findViewById(R.id.chao);

        if (getItem(position) != null) {
            complaintsViewHolder.compImg.setImageURI(complaintses.get(position).getCompImg());
            complaintsViewHolder.riverName.setText(complaintses.get(position).getRiverName());
            complaintsViewHolder.compTitle.setText(complaintses.get(position).getCompTitle());
            complaintsViewHolder.compTime.setText(complaintses.get(position).getCompTime());
            if (complaintses.get(position).getCompStatus().equals("1")) {
                complaintsViewHolder.compStatus.setText("未处理");
                complaintsViewHolder.compStatus.setTextColor(convertView.getResources().getColor(R.color.red));
            } else if (complaintses.get(position).getCompStatus().equals("2")) {
                complaintsViewHolder.compStatus.setText("处理中");
                complaintsViewHolder.compStatus.setTextColor(convertView.getResources().getColor(R.color.o));
            } else if (complaintses.get(position).getCompStatus().equals("3")) {
                complaintsViewHolder.compStatus.setText("已处理");
                complaintsViewHolder.compStatus.setTextColor(convertView.getResources().getColor(R.color.colorrivertxt));
            } else if (complaintses.get(position).getCompStatus().equals("4")) {
                complaintsViewHolder.compStatus.setText("已结案");
                complaintsViewHolder.compStatus.setTextColor(convertView.getResources().getColor(R.color.blue));
            }
            if (complaintses.get(position).isCopyerStatus()) {
                chao.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ComplaintsViewHolder {
        SimpleDraweeView compImg;              //图片
        TextView riverName;             //河道名
        TextView compTitle;             //标题
        TextView compTime;              //时间
        TextView compStatus;            //状态
//        ImageView chao;
    }
}
