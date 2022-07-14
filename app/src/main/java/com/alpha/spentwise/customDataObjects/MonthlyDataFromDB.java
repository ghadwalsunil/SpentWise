package com.alpha.spentwise.customDataObjects;

public class MonthlyDataFromDB {
    private int month;
    private String monthName;
    private int amount;

    public MonthlyDataFromDB(int month, String monthName, int amount) {
        this.month = month;
        this.monthName = monthName;
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
