package com.alpha.spentwise.customDataObjects;

import java.util.ArrayList;

public class AppliedFilterDetails {

    private ArrayList<Integer> filterCategories;
    private ArrayList<Integer> filterTransactionModes;
    private ArrayList<Integer> filterTransactionTypes;
    private int[] filterDateRange;
    private int[] filterAmountRange;
    private int filterStatus;

    public AppliedFilterDetails(ArrayList<Integer> filterCategories, ArrayList<Integer> filterTransactionModes, ArrayList<Integer> filterTransactionTypes, int[] filterDateRange, int[] filterAmountRange, int filterStatus) {
        this.filterCategories = filterCategories;
        this.filterTransactionModes = filterTransactionModes;
        this.filterTransactionTypes = filterTransactionTypes;
        this.filterDateRange = filterDateRange;
        this.filterAmountRange = filterAmountRange;
        this.filterStatus = filterStatus;
    }

    public ArrayList<Integer> getFilterCategories() {
        return filterCategories;
    }

    public void setFilterCategories(ArrayList<Integer> filterCategories) {
        this.filterCategories = filterCategories;
    }

    public ArrayList<Integer> getFilterTransactionModes() {
        return filterTransactionModes;
    }

    public void setFilterTransactionModes(ArrayList<Integer> filterTransactionModes) {
        this.filterTransactionModes = filterTransactionModes;
    }

    public ArrayList<Integer> getFilterTransactionTypes() {
        return filterTransactionTypes;
    }

    public void setFilterTransactionTypes(ArrayList<Integer> filterTransactionTypes) {
        this.filterTransactionTypes = filterTransactionTypes;
    }

    public int[] getFilterDateRange() {
        return filterDateRange;
    }

    public void setFilterDateRange(int[] filterDateRange) {
        this.filterDateRange = filterDateRange;
    }

    public int[] getFilterAmountRange() {
        return filterAmountRange;
    }

    public void setFilterAmountRange(int[] filterAmountRange) {
        this.filterAmountRange = filterAmountRange;
    }

    public int getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(int filterStatus) {
        this.filterStatus = filterStatus;
    }
}
