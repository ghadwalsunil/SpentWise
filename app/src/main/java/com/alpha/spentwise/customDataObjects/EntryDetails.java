package com.alpha.spentwise.customDataObjects;

public class EntryDetails {

    private int entryNumber;
    private int entryCategoryID;
    private int entryType;
    private int entryAmount;
    private int entryDate;
    private int entryMode;
    private String entryComment;
    private int entryRepeat;
    private int entryStatus;

    public EntryDetails(int entryNumber, int entryCategoryID, int entryType, int entryAmount, int entryDate, int entryMode, String entryComment, int entryRepeat, int entryStatus) {
        this.entryNumber = entryNumber;
        this.entryCategoryID = entryCategoryID;
        this.entryType = entryType;
        this.entryAmount = entryAmount;
        this.entryDate = entryDate;
        this.entryMode = entryMode;
        this.entryComment = entryComment;
        this.entryRepeat = entryRepeat;
        this.entryStatus = entryStatus;
    }

    public EntryDetails() {
        //Blank Constructor
    }

    public EntryDetails(int entryNumber) {
        this.entryNumber = entryNumber;
    }


    public int getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public int getEntryCategoryID() {
        return entryCategoryID;
    }

    public void setEntryCategoryID(int entryCategoryID) {
        this.entryCategoryID = entryCategoryID;
    }

    public int getEntryType() {
        return entryType;
    }

    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }

    public int getEntryAmount() {
        return entryAmount;
    }

    public void setEntryAmount(int entryAmount) {
        this.entryAmount = entryAmount;
    }

    public int getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(int entryDate) {
        this.entryDate = entryDate;
    }

    public int getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(int entryMode) {
        this.entryMode = entryMode;
    }

    public String getEntryComment() {
        return entryComment;
    }

    public void setEntryComment(String entryComment) {
        this.entryComment = entryComment;
    }

    public int getEntryRepeat() {
        return entryRepeat;
    }

    public void setEntryRepeat(int entryRepeat) {
        this.entryRepeat = entryRepeat;
    }

    public int getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(int entryStatus) {
        this.entryStatus = entryStatus;
    }

    @Override
    public String toString() {
        return "EntryDetails{" +
                "entryNumber=" + entryNumber +
                ", entryCategoryID=" + entryCategoryID +
                ", entryType=" + entryType +
                ", entryAmount=" + entryAmount +
                ", entryDate=" + entryDate +
                ", entryMode=" + entryMode +
                ", entryComment='" + entryComment + '\'' +
                ", entryRepeat=" + entryRepeat +
                ", entryStatus=" + entryStatus +
                '}';
    }

}
