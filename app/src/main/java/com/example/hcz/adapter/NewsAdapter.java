package com.example.hcz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.News;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;

/**
 * 新闻适配器
 * Created by Administrator on 2017/11/22.
 */
public class NewsAdapter extends BaseAdapter {
    private List<News> newses;
    private LayoutInflater layoutInflater;
    private Context context;

    public NewsAdapter(Context context, List<News> newses) {
        this.newses = newses;
        this.layoutInflater = layoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return newses.size();
    }

    @Override
    public Object getItem(int position) {
        return newses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsViewHolder newsViewHolder;
        if (convertView == null) {
            newsViewHolder = new NewsViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_news, null);
            newsViewHolder.newsImg = convertView.findViewById(R.id.newsImg);
            newsViewHolder.newsTitle = convertView.findViewById(R.id.newsTitle);
            newsViewHolder.newsTime = convertView.findViewById(R.id.newsTime);
            newsViewHolder.newSource = convertView.findViewById(R.id.newSource);
            convertView.setTag(newsViewHolder);
        } else {
            newsViewHolder = (NewsViewHolder) convertView.getTag();
        }

        //绑定数据
        if (getItem(position) != null) {
            newsViewHolder.newsImg.setImageURI(newses.get(position).getNewsImg());
            newsViewHolder.newsTitle.setText(newses.get(position).getNewsTitle());
            newsViewHolder.newsTime.setText(newses.get(position).getNewsTime());
            newsViewHolder.newSource.setText(newses.get(position).getNewSource());
        }

        return convertView;
    }

    class NewsViewHolder {
        SimpleDraweeView newsImg;          //新闻图片
        TextView newsTitle;         //新闻标题
        TextView newsTime;          //新闻时间
        TextView newSource;         //新闻来源
    }
}
