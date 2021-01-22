package com.example.sqlitedatabase.MyCart;

public class CartList {

    String image;
    String styleno;
    String itemname;
    String categoryname;
    String categorygroup;
    String pcsperbox;
    String qty;
    String amount;
    String pcsperbundle;
    String remove;
    String rupee;

    public CartList(String image, String styleno, String itemname, String categoryname, String categorygroup, String pcsperbox, String qty, String amount, String pcsperbundle, String remove, String rupee) {
        this.image = image;
        this.styleno = styleno;
        this.itemname = itemname;
        this.categoryname = categoryname;
        this.categorygroup = categorygroup;
        this.pcsperbox = pcsperbox;
        this.qty = qty;
        this.amount = amount;
        this.pcsperbundle = pcsperbundle;
        this.remove = remove;
        this.rupee = rupee;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStyleno() {
        return styleno;
    }

    public void setStyleno(String styleno) {
        this.styleno = styleno;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategorygroup() {
        return categorygroup;
    }

    public void setCategorygroup(String categorygroup) {
        this.categorygroup = categorygroup;
    }

    public String getPcsperbox() {
        return pcsperbox;
    }

    public void setPcsperbox(String pcsperbox) {
        this.pcsperbox = pcsperbox;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPcsperbundle() {
        return pcsperbundle;
    }

    public void setPcsperbundle(String pcsperbundle) {
        this.pcsperbundle = pcsperbundle;
    }

    public String getRemove() {
        return remove;
    }

    public void setRemove(String remove) {
        this.remove = remove;
    }

    public String getRupee() {
        return rupee;
    }

    public void setRupee(String rupee) {
        this.rupee = rupee;
    }
}
