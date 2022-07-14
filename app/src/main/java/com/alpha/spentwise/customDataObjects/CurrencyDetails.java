package com.alpha.spentwise.customDataObjects;

public class CurrencyDetails implements Comparable<CurrencyDetails>{

    //Do not rename any of the variables related to this class as this may cause that api to not work
    private String id;
    private String currencyName;
    private String currencySymbol;

    public CurrencyDetails(String id, String currencyName, String currencySymbol) {
        this.id = id;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
    }

    public CurrencyDetails() {
    }

    @Override
    public String toString() {
        if(currencySymbol == null)
            return id + " - " + currencyName;
        return id + " - " + currencyName + " ( " + currencySymbol + " )";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    @Override
    public int compareTo(CurrencyDetails o) {
        return this.getId().compareTo(o.id);
    }
}
