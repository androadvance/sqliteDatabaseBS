package com.example.sqlitedatabase.Order.OrderSelectionList;

public class SizeList {

    String size,qty;

    public SizeList(String size, String qty) {
        this.size = size;
        this.qty = qty;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
