package com.example.hcz.pojo;

/**
 * 日志类
 * Created by Administrator on 2017/11/23.
 */
public class Log {
    private int id;                     //编号
    private String logTitle;            //日志标题
    private String logTime;             //日志时间

    public Log() {
    }

    public Log(int id, String logTitle, String logTime) {
        this.id = id;
        this.logTitle = logTitle;
        this.logTime = logTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogTitle() {
        return logTitle;
    }

    public void setLogTitle(String logTitle) {
        this.logTitle = logTitle;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }
}
