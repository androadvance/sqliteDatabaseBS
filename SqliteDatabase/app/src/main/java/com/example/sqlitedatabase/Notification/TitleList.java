package com.example.sqlitedatabase.Notification;

public class TitleList {

    public String Title,Content,date,isread,counts;

    public TitleList(String title, String content, String date, String isread, String counts) {
        Title = title;
        Content = content;
        this.date = date;
        this.isread = isread;
        this.counts = counts;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }
}
