package com.example.sqlitedatabase.MyCart;

public class U_CartItemList {

    String size,qty,rate,amount;

    public U_CartItemList(String size, String qty, String rate, String amount) {
        this.size = size;
        this.qty = qty;
        this.rate = rate;
        this.amount = amount;
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

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
