package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.WadiInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 河道信息适配器
 * Created by Administrator on 2017/11/17.
 */

public class WadiInfoAdapter extends BaseAdapter {
    private List<WadiInfo> wadiInfos;
    private LayoutInflater layoutInflater;
    private Context context;

    public WadiInfoAdapter(Context context, List<WadiInfo> wadiInfos) {
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
        WadiInfoListViewHolder wadiInfoListViewHolder;
        if (convertView == null) {
            //获取组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.item_wadiinfo, null);
            wadiInfoListViewHolder = new WadiInfoListViewHolder();
            wadiInfoListViewHolder.wadiinfo_name = (TextView) convertView.findViewById(R.id.wadiinfo_name);
            wadiInfoListViewHolder.wadiinfo_img = convertView.findViewById(R.id.wadiinfo_img);
            wadiInfoListViewHolder.river_type = (TextView) convertView.findViewById(R.id.river_type);
            wadiInfoListViewHolder.wadiinfo_type = (ImageView) convertView.findViewById(R.id.wadiinfo_type);

            convertView.setTag(wadiInfoListViewHolder);
        } else {
            wadiInfoListViewHolder = (WadiInfoListViewHolder) convertView.getTag();
        }

        //绑定数据
        if (getItem(position) != null) {
            wadiInfoListViewHolder.wadiinfo_img.setImageURI(wadiInfos.get(position).getWadi_img());
            wadiInfoListViewHolder.wadiinfo_name.setText(wadiInfos.get(position).getName());
            wadiInfoListViewHolder.river_type.setText(wadiInfos.get(position).getRiver_type());

            if (wadiInfos.get(position).getType().equals("1"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water1);
            else if (wadiInfos.get(position).getType().equals("2"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water2);
            else if (wadiInfos.get(position).getType().equals("3"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water3);
            else if (wadiInfos.get(position).getType().equals("4"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water4);
            else if (wadiInfos.get(position).getType().equals("5"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water5);
            else if (wadiInfos.get(position).getType().equals("6"))
                wadiInfoListViewHolder.wadiinfo_type.setImageResource(R.drawable.water6);

        }

        return convertView;
    }

    class WadiInfoListViewHolder {
        SimpleDraweeView wadiinfo_img;      //河道图片
        TextView wadiinfo_name;     //河道名称
        TextView river_type;        //河道级别
        ImageView wadiinfo_type;    //水质类型
    }
}
