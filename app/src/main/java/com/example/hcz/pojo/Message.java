package com.example.hcz.pojo;

/**
 * Created by Administrator on 2017/12/29.
 */
public class Message {
    private String messageId;
    private String type;
    private String complaintRiver;
    private String responeRiver;
    private String compStatus;
    private String compCon;
    private String recipient;
    private String uninspection;
    private String time;

    public Message() {
    }

    public String getResponeRiver() {
        return responeRiver;
    }

    public void setResponeRiver(String responeRiver) {
        this.responeRiver = responeRiver;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComplaintRiver() {
        return complaintRiver;
    }

    public void setComplaintRiver(String complaintRiver) {
        this.complaintRiver = complaintRiver;
    }

    public String getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(String compStatus) {
        this.compStatus = compStatus;
    }

    public String getCompCon() {
        return compCon;
    }

    public void setCompCon(String compCon) {
        this.compCon = compCon;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getUninspection() {
        return uninspection;
    }

    public void setUninspection(String uninspection) {
        this.uninspection = uninspection;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
