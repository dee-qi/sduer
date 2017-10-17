package com.example.sduhelper.Items;

/**
 * This is sduer
 * Created by qidi on 2017/7/18.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class NewsItem {
    private String title;//标题
    private String time;//时间
    private String block;//栏目
    private String url;
    private int id;



    public NewsItem(String t, String time, String block, String url, int id){
        this.title = t;
        this.time = time;
        this.block = block;
        this.url = url;
        this.id = id;
    }
    public int getId(){return id;}
    public void setId(int id){this.id = id;}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
