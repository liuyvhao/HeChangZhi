package com.example.hcz.pojo;

/**
 * 污染源类
 * Created by Administrator on 2017/11/27.
 */
public class Pollution {
    private String id;                 //编号
    private String name;            //名称
    private String position;        //地点
    private String type;            //类型
    private String Lat;             //纬度
    private String lng;             //经度

    public Pollution(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
