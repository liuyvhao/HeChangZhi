package com.example.hcz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.ComplaintsInfo;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */
public class ComplaintsInfoAdapter extends BaseAdapter {
    private List<ComplaintsInfo> complaintsInfos;
    private Context context;
    private LayoutInflater layoutInflater;

    public ComplaintsInfoAdapter(Context context, List<ComplaintsInfo> complaintsInfos) {
        this.complaintsInfos = complaintsInfos;
        this.context = context;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return complaintsInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return complaintsInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CompInfoViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_compinfo, null);
            holder = new CompInfoViewHolder();
            holder.compScheduleTitle = convertView.findViewById(R.id.compScheduleTitle);
            holder.compTime = convertView.findViewById(R.id.compTime);
            holder.dContent = convertView.findViewById(R.id.dContent);
            holder.gridView = convertView.findViewById(R.id.gridView);
            holder.down = convertView.findViewById(R.id.down);
            convertView.setTag(holder);
        } else {
            holder = (CompInfoViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            holder.compScheduleTitle.setText(complaintsInfos.get(position).getCompScheduleTitle());
            holder.compTime.setText(complaintsInfos.get(position).getCompTime());
            holder.dContent.setText(complaintsInfos.get(position).getdContent());
            ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
            for (int i = 0; i < complaintsInfos.get(position).getdPath().size(); i++) {
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = complaintsInfos.get(position).getdPath().get(i);
                photoInfo.thumbnailUrl = complaintsInfos.get(position).getdPath().get(i);
                photoInfos.add(photoInfo);
            }
            final FullyGridLayoutManager mLayoutManager = new FullyGridLayoutManager(context, 3);
            holder.gridView.setLayoutManager(mLayoutManager);
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


        if (complaintsInfos.get(position).getStatus().equals("0") && position == complaintsInfos.size() - 1) {
            holder.down.setVisibility(View.GONE);
        }else
            holder.down.setVisibility(View.VISIBLE);
        return convertView;
    }

    class CompInfoViewHolder {
        TextView compScheduleTitle;
        TextView compTime;
        TextView dContent;
        RecyclerView gridView;
        View down;
    }
}
