package com.example.hcz.pojo;

/**
 * 河道信息-详细
 * Created by Administrator on 2017/11/22.
 */
public class RiverInfo {
    private Number id;              //编号
    private String name;            //河道名称
    private String riverCode;       //河道编码
    private String riverLength;     //河道长度
    private String river_type;      //河道等级
    private String area;             //责任主体
    private String riverStart;      //河道起点
    private String riverEnd;        //河道终点
    private String riverDuty;        //河长职责

    public RiverInfo(Number id, String name, String riverCode, String riverLength, String river_type, String area, String riverStart, String riverEnd, String riverDuty) {
        this.id = id;
        this.name = name;
        this.riverCode = riverCode;
        this.riverLength = riverLength;
        this.river_type = river_type;
        this.area = area;
        this.riverStart = riverStart;
        this.riverEnd = riverEnd;
        this.riverDuty = riverDuty;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRiverCode() {
        return riverCode;
    }

    public void setRiverCode(String riverCode) {
        this.riverCode = riverCode;
    }

    public String getRiverLength() {
        return riverLength;
    }

    public void setRiverLength(String riverLength) {
        this.riverLength = riverLength;
    }

    public String getRiver_type() {
        return river_type;
    }

    public void setRiver_type(String river_type) {
        this.river_type = river_type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRiverStart() {
        return riverStart;
    }

    public void setRiverStart(String riverStart) {
        this.riverStart = riverStart;
    }

    public String getRiverEnd() {
        return riverEnd;
    }

    public void setRiverEnd(String riverEnd) {
        this.riverEnd = riverEnd;
    }

    public String getRiverDuty() {
        return riverDuty;
    }

    public void setRiverDuty(String riverDuty) {
        this.riverDuty = riverDuty;
    }
}
