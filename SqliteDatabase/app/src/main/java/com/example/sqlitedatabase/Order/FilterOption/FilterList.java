package com.example.sqlitedatabase.Order.FilterOption;

public class FilterList {

    String subtype;

    public FilterList(String subtype) {
        this.subtype = subtype;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
}
