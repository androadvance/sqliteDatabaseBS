package com.example.sqlitedatabase;

public class VersionList {

    String version,remarks,description,bulletpoint;

    public VersionList(String version, String remarks, String description, String bulletpoint) {
        this.version = version;
        this.remarks = remarks;
        this.description = description;
        this.bulletpoint = bulletpoint;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBulletpoint() {
        return bulletpoint;
    }

    public void setBulletpoint(String bulletpoint) {
        this.bulletpoint = bulletpoint;
    }
}
