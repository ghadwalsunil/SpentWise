package com.alpha.spentwise.customDataObjects;

import java.util.Objects;

public class TransactionModeDetails {

    private int transactionModeID;
    private String transactionModeName;
    private int transactionModeStatus;

    public TransactionModeDetails(int transactionModeID, String transactionModeName, int transactionModeStatus) {
        this.transactionModeID = transactionModeID;
        this.transactionModeName = transactionModeName;
        this.transactionModeStatus = transactionModeStatus;
    }

    public int getTransactionModeID() {
        return transactionModeID;
    }

    public void setTransactionModeID(int transactionModeID) {
        this.transactionModeID = transactionModeID;
    }

    public String getTransactionModeName() {
        return transactionModeName;
    }

    public void setTransactionModeName(String transactionModeName) {
        this.transactionModeName = transactionModeName;
    }

    public int getTransactionModeStatus() {
        return transactionModeStatus;
    }

    public void setTransactionModeStatus(int transactionModeStatus) {
        this.transactionModeStatus = transactionModeStatus;
    }

    @Override
    public String toString() {
        return transactionModeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionModeDetails that = (TransactionModeDetails) o;
        return transactionModeID == that.transactionModeID &&
                transactionModeStatus == that.transactionModeStatus &&
                Objects.equals(transactionModeName, that.transactionModeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionModeID, transactionModeName, transactionModeStatus);
    }
}
