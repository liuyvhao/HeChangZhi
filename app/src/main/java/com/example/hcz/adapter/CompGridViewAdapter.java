package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.hcz.hechangzhi.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * GridView适配器
 * Created by Administrator on 2017/11/29.
 */
public class CompGridViewAdapter extends BaseAdapter {
    private List<String> imgs;
    private LayoutInflater layoutInflater;
    private Context context;

    public CompGridViewAdapter(Context context, List<String> imgs) {
        this.imgs = imgs;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CompGridViewHolder gridViewHolder;
        if (convertView == null) {
            gridViewHolder = new CompGridViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_comp_grid, null);
            gridViewHolder.img = convertView.findViewById(R.id.img);
            convertView.setTag(gridViewHolder);
        } else {
            gridViewHolder = (CompGridViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            gridViewHolder.img.setImageURI(imgs.get(position));
        }

        return convertView;
    }

    class CompGridViewHolder {
        SimpleDraweeView img;
    }
}
