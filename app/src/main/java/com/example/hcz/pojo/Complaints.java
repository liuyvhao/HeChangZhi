package com.example.hcz.pojo;

/**
 * 投诉信息类
 * Created by Administrator on 2017/11/23.
 */
public class Complaints {
    private String id;                   //编号
    private String compImg;             //投诉图片
    private String riverName;           //河道名称
    private String compTitle;           //标题
    private String compTime;            //事件
    private String compStatus;          //状态
    private boolean copyerStatus;          //状态

    public Complaints() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompImg() {
        return compImg;
    }

    public void setCompImg(String compImg) {
        this.compImg = compImg;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public String getCompTitle() {
        return compTitle;
    }

    public void setCompTitle(String compTitle) {
        this.compTitle = compTitle;
    }

    public String getCompTime() {
        return compTime;
    }

    public void setCompTime(String compTime) {
        this.compTime = compTime;
    }

    public String getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(String compStatus) {
        this.compStatus = compStatus;
    }

    public boolean isCopyerStatus() {
        return copyerStatus;
    }

    public void setCopyerStatus(boolean copyerStatus) {
        this.copyerStatus = copyerStatus;
    }
}
