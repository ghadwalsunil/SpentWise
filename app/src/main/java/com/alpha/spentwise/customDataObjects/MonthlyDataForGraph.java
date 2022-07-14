package com.alpha.spentwise.customDataObjects;

public class MonthlyDataForGraph {

    private String month;
    private int amount;

    public MonthlyDataForGraph(String month, int amount) {
        this.month = month;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

