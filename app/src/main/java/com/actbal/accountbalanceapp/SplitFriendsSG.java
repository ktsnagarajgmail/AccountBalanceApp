package com.actbal.accountbalanceapp;

import java.util.ArrayList;

public class SplitFriendsSG {

    String name, amt, friendsUID;
    ArrayList<ItemsDetailSG> itemDetail = new ArrayList();

    public String getFriendsUID() {
        return friendsUID;
    }

    public void setFriendsUID(String friendsUID) {
        this.friendsUID = friendsUID;
    }

    public SplitFriendsSG(String name, String amt, ArrayList<ItemsDetailSG> itemDetail, String friendsUID) {
        this.name = name;
        this.amt = amt;
        this.itemDetail = itemDetail;
        this.friendsUID = friendsUID;
    }

    public SplitFriendsSG(String name, String amt, String friendsUID) {
        this.name = name;
        this.amt = amt;
        this.friendsUID = friendsUID;
    }

    public SplitFriendsSG(String name, String amt) {
        this.name = name;
        this.amt = amt;
    }

    public ArrayList<ItemsDetailSG> getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(ArrayList<ItemsDetailSG> itemDetail) {
        this.itemDetail = itemDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}


