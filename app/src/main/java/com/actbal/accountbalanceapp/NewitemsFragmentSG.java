package com.actbal.accountbalanceapp;

public class NewitemsFragmentSG {

    String UID, name;
    double amount, splitAmount;

    public NewitemsFragmentSG(String UID, String name, double amount, double splitAmount) {
        this.UID = UID;
        this.name = name;
        this.amount = amount;
        this.splitAmount = splitAmount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSplitAmount() { return splitAmount; }

    public void setSplitAmount(double splitAmount) {
        this.splitAmount = splitAmount;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
