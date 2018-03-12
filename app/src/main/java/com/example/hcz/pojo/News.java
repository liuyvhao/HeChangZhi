package com.example.hcz.pojo;

/**
 * 新闻类
 * Created by Administrator on 2017/11/22.
 */
public class News {
    private int id;                 //编号
    private String newsImg;         //新闻图片
    private String newsTitle;         //新闻标题
    private String newsTime;         //新闻时间
    private String newSource;       //来源

    public News(){}

    public News(int id, String newsImg, String newsTitle, String newsTime) {
        this.id = id;
        this.newsImg = newsImg;
        this.newsTitle = newsTitle;
        this.newsTime = newsTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(String newsImg) {
        this.newsImg = newsImg;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewSource() {
        return newSource;
    }

    public void setNewSource(String newSource) {
        this.newSource = newSource;
    }
}
