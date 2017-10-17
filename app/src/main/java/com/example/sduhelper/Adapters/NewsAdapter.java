package com.example.sduhelper.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sduhelper.Activities.NewsDetailActivity;
import com.example.sduhelper.Activities.WebViewBrowserActivity;
import com.example.sduhelper.Items.NewsItem;
import com.example.sduhelper.R;

import java.util.List;

/**
 * This is sduer
 * Created by qidi on 2017/7/18.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private  List<NewsItem> mList;
    private String api;

    static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView newsTitle;
        TextView newsTime;
        TextView newsBlock;
        View newsView;
        public ViewHolder(View v){
            super(v);
            this.newsTitle = (TextView)v.findViewById(R.id.news_title);
            this.newsTime = (TextView)v.findViewById(R.id.news_time);
            this.newsBlock = (TextView)v.findViewById(R.id.news_block);
            this.newsView = v;
        }
    }

    public NewsAdapter(List<NewsItem> newsItemList,String api){mList = newsItemList;this.api = api;}

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                NewsItem newsItem = mList.get(position);
                Intent intent = new Intent(parent.getContext(), NewsDetailActivity.class);
                intent.putExtra("url", newsItem.getUrl());
                intent.putExtra("id",newsItem.getId());
                intent.putExtra("api",api);
                parent.getContext().startActivity(intent);
//                Toast.makeText(parent.getContext(), "手势可缩放页面", Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsItem newsItem = mList.get(position);
        holder.newsTitle.setText(newsItem.getTitle());
        holder.newsTime.setText(newsItem.getTime());
        holder.newsBlock.setText(newsItem.getBlock());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
