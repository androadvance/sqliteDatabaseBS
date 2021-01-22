package com.example.sqlitedatabase.Notification;

public class InboxViewList {

    String date,msgcontent,time;

    public InboxViewList(String date, String msgcontent, String time) {
        this.date = date;
        this.msgcontent = msgcontent;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsgcontent() {
        return msgcontent;
    }

    public void setMsgcontent(String msgcontent) {
        this.msgcontent = msgcontent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
