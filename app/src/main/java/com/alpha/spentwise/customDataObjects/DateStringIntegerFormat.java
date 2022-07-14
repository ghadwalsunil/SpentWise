package com.alpha.spentwise.customDataObjects;

public class DateStringIntegerFormat {

    private String dateCustomString;
    private int dateCustomInt;

    public DateStringIntegerFormat(String dateCustomString, int dateCustomInt) {
        this.dateCustomString = dateCustomString;
        this.dateCustomInt = dateCustomInt;
    }

    public String getDateCustomString() {
        return dateCustomString;
    }

    public void setDateCustomString(String dateCustomString) {
        this.dateCustomString = dateCustomString;
    }

    public int getDateCustomInt() {
        return dateCustomInt;
    }

    public void setDateCustomInt(int dateCustomInt) {
        this.dateCustomInt = dateCustomInt;
    }

    @Override
    public String toString() {
        return dateCustomString;
    }
}
