package com.example.hcz.pojo;

/**
 * 通讯录
 * Created by Administrator on 2017/11/24.
 */
public class Addressbook {
    private int id;                 //编号
    private String avatarUrl;       //头像
    private String name;            //姓名
    private String pin;             //拼音
    private String description;     //职务
    private String phone;           //电话

    public Addressbook(){}

    public Addressbook(int id, String name, String description, String phone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
