package com.alpha.spentwise.customDataObjects;

import java.util.Objects;

public class TransactionTypeDetails {

    private int transactionTypeID;
    private String transactionTypeName;

    public TransactionTypeDetails(int transactionTypeID, String transactionTypeName) {
        this.transactionTypeID = transactionTypeID;
        this.transactionTypeName = transactionTypeName;
    }

    public int getTransactionTypeID() {
        return transactionTypeID;
    }

    public void setTransactionTypeID(int transactionTypeID) {
        this.transactionTypeID = transactionTypeID;
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }

    @Override
    public String toString() {
        return transactionTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTypeDetails that = (TransactionTypeDetails) o;
        return transactionTypeID == that.transactionTypeID &&
                Objects.equals(transactionTypeName, that.transactionTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionTypeID, transactionTypeName);
    }
}
