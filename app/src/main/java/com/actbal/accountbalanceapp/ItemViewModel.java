package com.actbal.accountbalanceapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ItemViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<NewitemsFragmentSG>> stringMutableLiveData = new MutableLiveData<ArrayList<NewitemsFragmentSG>>();

    public void setData(ArrayList<NewitemsFragmentSG> arrayList){
        stringMutableLiveData.setValue(arrayList);
    }

    public LiveData<ArrayList<NewitemsFragmentSG>> getData(){
        return stringMutableLiveData;
    }
}
