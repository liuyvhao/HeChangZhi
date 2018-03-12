package com.example.hcz.pojo;

import java.util.List;

/**
 * 历史事件类
 * Created by Administrator on 2017/12/1.
 */
public class History {
    private String id;              //编号
    private String type;            //事件类型
    private String riverName;       //关联河道
    private String position;        //事件地点
    private String content;         //事件详情
    private List<String> img;       //图片组

    public History(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }
}
