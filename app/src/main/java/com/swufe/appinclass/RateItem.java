package com.swufe.appinclass;

//用于映射数据库中表的数据
public class RateItem {

    private int id;
    private String curName;
    private Double curRate;

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
