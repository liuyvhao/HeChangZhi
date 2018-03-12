package com.example.hcz.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.History;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.MyGridView;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */
public class HistoryAdapter extends BaseAdapter {
    private List<History> histories;
    private Context context;
    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, List<History> histories) {
        this.histories = histories;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HistoryViewHolder holder;
        if (convertView == null) {
            holder = new HistoryViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_history, null);
            holder.id = convertView.findViewById(R.id.id);
            holder.type = convertView.findViewById(R.id.type);
            holder.riverName = convertView.findViewById(R.id.riverName);
            holder.position = convertView.findViewById(R.id.position);
            holder.content = convertView.findViewById(R.id.content);
            holder.gridView = convertView.findViewById(R.id.gridView);
            convertView.setTag(holder);
        } else {
            holder = (HistoryViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.id.setText(histories.get(position).getId());
            holder.type.setText(histories.get(position).getType());
            holder.riverName.setText(histories.get(position).getRiverName());
            holder.position.setText(histories.get(position).getPosition());
            holder.content.setText(histories.get(position).getContent());
            ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
            for (int i = 0; i < histories.get(position).getImg().size(); i++) {
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = histories.get(position).getImg().get(i);
                photoInfo.thumbnailUrl = histories.get(position).getImg().get(i);
                photoInfos.add(photoInfo);
            }
            final FullyGridLayoutManager mLayoutManager = new FullyGridLayoutManager(context, 3);
            holder.gridView.setLayoutManager(mLayoutManager);
            holder.gridView.setNestedScrollingEnabled(false);
            PhotoWallAdapter adapter = new PhotoWallAdapter(photoInfos, new OnItemClickListener() {
                @Override
                public void onItemClick(View view, ArrayList photos, int position) {
                    PictureBrowse.newBuilder(context)
                            .setLayoutManager(mLayoutManager)
                            .setPhotoList(photos)
                            .setCurrentPosition(position)
                            .enabledAnimation(true)
                            .toggleLongClick(false)
                            .start();
                }
            });
            holder.gridView.setAdapter(adapter);
        }

        return convertView;
    }

    class HistoryViewHolder {
        TextView id;
        TextView type;
        TextView riverName;
        TextView position;
        TextView content;
        RecyclerView gridView;
    }
}
