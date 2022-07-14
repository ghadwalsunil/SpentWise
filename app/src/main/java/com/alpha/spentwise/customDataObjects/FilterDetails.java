package com.alpha.spentwise.customDataObjects;

import java.util.List;

public class FilterDetails {

    private List<CategoryDetails> categoryDetails;
    private List<TransactionModeDetails> transactionModeDetails;
    private List<TransactionTypeDetails> transactionTypeDetails;
    private int maxAmount;
    private int minAmount;
    private int maxDate;
    private int minDate;

    public FilterDetails(List<CategoryDetails> categoryDetails, List<TransactionModeDetails> transactionModeDetails, List<TransactionTypeDetails> transactionTypeDetails, int maxAmount, int minAmount, int maxDate, int minDate) {
        this.categoryDetails = categoryDetails;
        this.transactionModeDetails = transactionModeDetails;
        this.transactionTypeDetails = transactionTypeDetails;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    public List<CategoryDetails> getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(List<CategoryDetails> categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public List<TransactionModeDetails> getTransactionModeDetails() {
        return transactionModeDetails;
    }

    public void setTransactionModeDetails(List<TransactionModeDetails> transactionModeDetails) {
        this.transactionModeDetails = transactionModeDetails;
    }

    public List<TransactionTypeDetails> getTransactionTypeDetails() {
        return transactionTypeDetails;
    }

    public void setTransactionTypeDetails(List<TransactionTypeDetails> transactionTypeDetails) {
        this.transactionTypeDetails = transactionTypeDetails;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(int maxDate) {
        this.maxDate = maxDate;
    }

    public int getMinDate() {
        return minDate;
    }

    public void setMinDate(int minDate) {
        this.minDate = minDate;
    }
}
