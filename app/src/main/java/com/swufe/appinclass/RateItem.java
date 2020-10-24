package com.swufe.appinclass;

import android.provider.Telephony;

//用于映射数据库中表的数据
public class RateItem {

    private int id;
    private String curName;
    private Double curRate;

    public RateItem(){
        super();
        curName = "";
        curRate = 0.0d;
    }

    public RateItem(String curName,Double curRate){
        super();
        this.curName = curName;
        this.curRate = curRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public Double getCurRate() {
        return curRate;
    }

    public void setCurRate(Double curRate) {
        this.curRate = curRate;
    }
}
