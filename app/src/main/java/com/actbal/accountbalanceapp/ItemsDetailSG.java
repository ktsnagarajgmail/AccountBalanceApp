package com.actbal.accountbalanceapp;

import java.io.Serializable;

public class ItemsDetailSG implements Serializable {
    String paidBy, splitAmt, TotalAmt, desc, date;
        public ItemsDetailSG(String paidBy, String splitAmt, String totalAmt, String desc, String date) {
        this.paidBy = paidBy;
        this.splitAmt = splitAmt;
        this.TotalAmt = totalAmt;
        this.desc = desc;
        this.date = date;
    }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPaidBy() {
        return paidBy;
    }
    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }
    public String getSplitAmt() {
        return splitAmt;
    }
    public void setSplitAmt(String splitAmt) {
        this.splitAmt = splitAmt;
    }
    public String getTotalAmt() {
        return TotalAmt;
    }
    public void setTotalAmt(String totalAmt) {
        TotalAmt = totalAmt;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
