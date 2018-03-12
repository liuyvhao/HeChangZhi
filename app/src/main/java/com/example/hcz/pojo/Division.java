package com.example.hcz.pojo;

/**
 * Created by Administrator on 2017/12/7.
 */

public class Division {
    private String name;
    private boolean check;

    public Division(String name, boolean check) {
        this.name = name;
        this.check = check;
    }

    public Division(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
