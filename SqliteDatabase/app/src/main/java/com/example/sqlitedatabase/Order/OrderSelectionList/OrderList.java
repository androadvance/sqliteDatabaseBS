package com.example.sqlitedatabase.Order.OrderSelectionList;

public class OrderList {

    String image,styleno,itemname,categoryname,categorygroup,pcsperbox,sizes,pcsperbundle;

    public OrderList(String image, String styleno, String itemname, String categoryname, String categorygroup, String pcsperbox, String sizes, String pcsperbundle) {
        this.image = image;
        this.styleno = styleno;
        this.itemname = itemname;
        this.categoryname = categoryname;
        this.categorygroup = categorygroup;
        this.pcsperbox = pcsperbox;
        this.sizes = sizes;
        this.pcsperbundle = pcsperbundle;
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

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getPcsperbundle() {
        return pcsperbundle;
    }

    public void setPcsperbundle(String pcsperbundle) {
        this.pcsperbundle = pcsperbundle;
    }
}
