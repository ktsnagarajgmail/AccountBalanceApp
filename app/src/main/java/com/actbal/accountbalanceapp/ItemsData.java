package com.actbal.accountbalanceapp;

public class ItemsData {

String type, amt, desc, date;

    public ItemsData(String type, String amt, String desc, String date) {
        this.type = type;
        this.amt = amt;
        this.desc = desc;
        this.date = date;
    }

    public ItemsData(String amt, String desc, String date) {
        this.amt = amt;
        this.desc = desc;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {        return type;    }

    public void setType(String type) {        this.type = type;    }

    public String getAmt() {        return amt;    }

    public void setAmt(String amt) {        this.amt = amt;    }

    public String getDesc() {        return desc;    }

    public void setDesc(String desc) {         this.desc = desc;    }
}
