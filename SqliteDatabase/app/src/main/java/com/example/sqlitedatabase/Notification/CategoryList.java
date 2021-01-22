package com.example.sqlitedatabase.Notification;

public class CategoryList {

    String category,time,message,messagecount,isread;

    public CategoryList(String category, String time, String message, String messagecount, String isread) {
        this.category = category;
        this.time = time;
        this.message = message;
        this.messagecount = messagecount;
        this.isread = isread;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(String messagecount) {
        this.messagecount = messagecount;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }
}
