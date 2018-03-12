package com.example.hcz.pojo;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 * 投诉详情类
 */
public class ComplaintsInfo {
    private String disposeId;
    private String compScheduleTitle;
    private String compTime;
    private String dContent;
    private List<String> dPath;
    private String status;

    public ComplaintsInfo(){}

    public String getDisposeId() {
        return disposeId;
    }

    public void setDisposeId(String disposeId) {
        this.disposeId = disposeId;
    }

    public String getCompScheduleTitle() {
        return compScheduleTitle;
    }

    public void setCompScheduleTitle(String compScheduleTitle) {
        this.compScheduleTitle = compScheduleTitle;
    }

    public String getCompTime() {
        return compTime;
    }

    public void setCompTime(String compTime) {
        this.compTime = compTime;
    }

    public String getdContent() {
        return dContent;
    }

    public void setdContent(String dContent) {
        this.dContent = dContent;
    }

    public List<String> getdPath() {
        return dPath;
    }

    public void setdPath(List<String> dPath) {
        this.dPath = dPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
