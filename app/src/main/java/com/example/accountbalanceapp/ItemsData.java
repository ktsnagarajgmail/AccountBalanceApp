package com.example.accountbalanceapp;

public class ItemsData {

String type, amt, desc;

    public ItemsData(String type, String amt, String desc) {
        this.type = type;
        this.amt = amt;
        this.desc = desc;
    }

    public String getType() {        return type;    }

    public void setType(String type) {        this.type = type;    }

    public String getAmt() {        return amt;    }

    public void setAmt(String amt) {        this.amt = amt;    }

    public String getDesc() {        return desc;    }

    public void setDesc(String desc) {         this.desc = desc;    }
}
