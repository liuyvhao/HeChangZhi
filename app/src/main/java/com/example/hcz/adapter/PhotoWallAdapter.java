package com.example.hcz.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hcz.hechangzhi.R;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;

import java.util.ArrayList;

public class PhotoWallAdapter extends RecyclerView.Adapter<PhotoWallAdapter.PhotoViewHolder> {
    private View view;
    private ArrayList<PhotoInfo> mPhotos;
    private OnItemClickListener mOnItemClickListener;
    private boolean status = false;

    public PhotoWallAdapter(ArrayList<PhotoInfo> photos, OnItemClickListener onItemClickListener) {
        this.mPhotos = photos;
        this.mOnItemClickListener = onItemClickListener;
    }

    public PhotoWallAdapter(ArrayList<PhotoInfo> photos, OnItemClickListener onItemClickListener, boolean status) {
        this.mPhotos = photos;
        this.mOnItemClickListener = onItemClickListener;
        this.status = status;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        //利用反射将item的布局加载出来
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_wall_item, null);

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (mPhotos.get(position).originalUrl.indexOf("http") != -1)
            holder.iv_thumbnail.setImageURI(mPhotos.get(position).originalUrl);
        else
            holder.iv_thumbnail.setImageURI("file://" + mPhotos.get(position).originalUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, mPhotos, holder.getAdapterPosition());
                }
            }
        });
        if (status) {
            holder.del.setVisibility(View.VISIBLE);
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotos.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView iv_thumbnail;
        ImageView del;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            del = itemView.findViewById(R.id.del);
        }
    }
}
