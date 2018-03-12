package com.example.hcz.pojo;

/**
 * Created by Administrator on 2017/12/6.
 * 巡检轨迹点类
 */
public class Inspection {
    private String lat;
    private String lng;
    private Number time;

    public Inspection(){}

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Number getTime() {
        return time;
    }

    public void setTime(Number time) {
        this.time = time;
    }
}
