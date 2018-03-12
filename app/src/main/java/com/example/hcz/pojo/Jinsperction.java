package com.example.hcz.pojo;

/**
 * 下级巡检类
 * Created by Administrator on 2017/11/30.
 */
public class Jinsperction {
    private String id;              //编号
    private String chiefName;       //巡检人
    private String time;            //时间

    public Jinsperction(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChiefName() {
        return chiefName;
    }

    public void setChiefName(String chiefName) {
        this.chiefName = chiefName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
