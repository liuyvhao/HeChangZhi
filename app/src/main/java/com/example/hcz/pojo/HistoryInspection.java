package com.example.hcz.pojo;

/**
 * 历史轨迹类
 * Created by Administrator on 2017/11/28.
 */
public class HistoryInspection {
    private String id;
    private String beginTime;
    private String endTime;
    private String time;

    public HistoryInspection() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
