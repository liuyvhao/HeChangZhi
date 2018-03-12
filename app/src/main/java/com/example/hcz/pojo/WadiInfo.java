package com.example.hcz.pojo;

/**
 * 河道信息-大体
 * Created by Administrator on 2017/11/17.
 */
public class WadiInfo {
    private int id;                       //编号
    private String name;                    //河道名称
    private String wadi_img;                  //河道图片
    private String type;                    //水质级别（1、2、3、4、5、6）
    private String river_type;             //河道级别
    private String rz;                      //水位

    public WadiInfo(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWadi_img() {
        return wadi_img;
    }

    public void setWadi_img(String wadi_img) {
        this.wadi_img = wadi_img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRiver_type() {
        return river_type;
    }

    public void setRiver_type(String river_type) {
        this.river_type = river_type;
    }

    public String getRz() {
        return rz;
    }

    public void setRz(String rz) {
        this.rz = rz;
    }
}
