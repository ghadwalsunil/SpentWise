package com.alpha.spentwise.customDataObjects;

public class ExportEntryDetails {

    private int entryId;
    private String entryCategoryName;
    private String entryTypeName;
    private int entryAmount;
    private int entryDate;
    private String entryModeName;
    private String entryComment;

    public ExportEntryDetails(int entryId, String entryCategoryName, String entryTypeName, int entryAmount, int entryDate, String entryModeName, String entryComment) {
        this.entryId = entryId;
        this.entryCategoryName = entryCategoryName;
        this.entryTypeName = entryTypeName;
        this.entryAmount = entryAmount;
        this.entryDate = entryDate;
        this.entryModeName = entryModeName;
        this.entryComment = entryComment;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getEntryCategoryName() {
        return entryCategoryName;
    }

    public void setEntryCategoryName(String entryCategoryName) {
        this.entryCategoryName = entryCategoryName;
    }

    public String getEntryTypeName() {
        return entryTypeName;
    }

    public void setEntryTypeName(String entryTypeName) {
        this.entryTypeName = entryTypeName;
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

    public String getEntryModeName() {
        return entryModeName;
    }

    public void setEntryModeName(String entryModeName) {
        this.entryModeName = entryModeName;
    }

    public String getEntryComment() {
        return entryComment;
    }

    public void setEntryComment(String entryComment) {
        this.entryComment = entryComment;
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(entryId), entryCategoryName, entryTypeName, String.valueOf(entryAmount), String.valueOf(entryDate), entryModeName, entryComment};
    }
}
