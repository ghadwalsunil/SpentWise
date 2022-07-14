package com.alpha.spentwise.customDataObjects;

public class EntryPatternDetails {

    private String entryPatternName;
    private int entryPatternDailyInterval;
    private int entryPatternStartDateInt;
    private int entryPatternLastDateInt;
    private int entryPatternNumberOfEntries;
    private int entryPatternWeeklyInterval;
    private boolean[] entryPatternSelectedCheckBoxes;
    private int entryPatternMonthlyInterval;
    private int entryPatternMonthlyRepeatOnDay;
    private String entryPatternMonthlyDayInstance;
    private String entryPatternMonthlyDayOfWeek;
    private int entryPatternSelectedOption;
    private int entryPatternMonthlySelectedOption;

    public EntryPatternDetails(String entryPatternName, int entryPatternDailyInterval, int entryPatternStartDateInt, int entryPatternLastDateInt, int entryPatternNumberOfEntries, int entryPatternWeeklyInterval, boolean[] entryPatternSelectedCheckBoxes, int entryPatternMonthlyInterval, int entryPatternMonthlyRepeatOnDay, String entryPatternMonthlyDayInstance, String entryPatternMonthlyDayOfWeek, int entryPatternSelectedOption, int entryPatternMonthlySelectedOption) {
        this.entryPatternName = entryPatternName;
        this.entryPatternDailyInterval = entryPatternDailyInterval;
        this.entryPatternStartDateInt = entryPatternStartDateInt;
        this.entryPatternLastDateInt = entryPatternLastDateInt;
        this.entryPatternNumberOfEntries = entryPatternNumberOfEntries;
        this.entryPatternWeeklyInterval = entryPatternWeeklyInterval;
        this.entryPatternSelectedCheckBoxes = entryPatternSelectedCheckBoxes;
        this.entryPatternMonthlyInterval = entryPatternMonthlyInterval;
        this.entryPatternMonthlyRepeatOnDay = entryPatternMonthlyRepeatOnDay;
        this.entryPatternMonthlyDayInstance = entryPatternMonthlyDayInstance;
        this.entryPatternMonthlyDayOfWeek = entryPatternMonthlyDayOfWeek;
        this.entryPatternSelectedOption = entryPatternSelectedOption;
        this.entryPatternMonthlySelectedOption = entryPatternMonthlySelectedOption;
    }

    public String getEntryPatternName() {
        return entryPatternName;
    }

    public void setEntryPatternName(String entryPatternName) {
        this.entryPatternName = entryPatternName;
    }

    public int getEntryPatternDailyInterval() {
        return entryPatternDailyInterval;
    }

    public void setEntryPatternDailyInterval(int entryPatternDailyInterval) {
        this.entryPatternDailyInterval = entryPatternDailyInterval;
    }

    public int getEntryPatternStartDateInt() {
        return entryPatternStartDateInt;
    }

    public void setEntryPatternStartDateInt(int entryPatternStartDateInt) {
        this.entryPatternStartDateInt = entryPatternStartDateInt;
    }

    public int getEntryPatternLastDateInt() {
        return entryPatternLastDateInt;
    }

    public void setEntryPatternLastDateInt(int entryPatternLastDateInt) {
        this.entryPatternLastDateInt = entryPatternLastDateInt;
    }

    public int getEntryPatternNumberOfEntries() {
        return entryPatternNumberOfEntries;
    }

    public void setEntryPatternNumberOfEntries(int entryPatternNumberOfEntries) {
        this.entryPatternNumberOfEntries = entryPatternNumberOfEntries;
    }

    public int getEntryPatternWeeklyInterval() {
        return entryPatternWeeklyInterval;
    }

    public void setEntryPatternWeeklyInterval(int entryPatternWeeklyInterval) {
        this.entryPatternWeeklyInterval = entryPatternWeeklyInterval;
    }

    public boolean[] getEntryPatternSelectedCheckBoxes() {
        return entryPatternSelectedCheckBoxes;
    }

    public void setEntryPatternSelectedCheckBoxes(boolean[] entryPatternSelectedCheckBoxes) {
        this.entryPatternSelectedCheckBoxes = entryPatternSelectedCheckBoxes;
    }

    public int getEntryPatternMonthlyInterval() {
        return entryPatternMonthlyInterval;
    }

    public void setEntryPatternMonthlyInterval(int entryPatternMonthlyInterval) {
        this.entryPatternMonthlyInterval = entryPatternMonthlyInterval;
    }

    public int getEntryPatternMonthlyRepeatOnDay() {
        return entryPatternMonthlyRepeatOnDay;
    }

    public void setEntryPatternMonthlyRepeatOnDay(int entryPatternMonthlyRepeatOnDay) {
        this.entryPatternMonthlyRepeatOnDay = entryPatternMonthlyRepeatOnDay;
    }

    public String getEntryPatternMonthlyDayInstance() {
        return entryPatternMonthlyDayInstance;
    }

    public void setEntryPatternMonthlyDayInstance(String entryPatternMonthlyDayInstance) {
        this.entryPatternMonthlyDayInstance = entryPatternMonthlyDayInstance;
    }

    public String getEntryPatternMonthlyDayOfWeek() {
        return entryPatternMonthlyDayOfWeek;
    }

    public void setEntryPatternMonthlyDayOfWeek(String entryPatternMonthlyDayOfWeek) {
        this.entryPatternMonthlyDayOfWeek = entryPatternMonthlyDayOfWeek;
    }

    public int getEntryPatternSelectedOption() {
        return entryPatternSelectedOption;
    }

    public void setEntryPatternSelectedOption(int entryPatternSelectedOption) {
        this.entryPatternSelectedOption = entryPatternSelectedOption;
    }

    public int getEntryPatternMonthlySelectedOption() {
        return entryPatternMonthlySelectedOption;
    }

    public void setEntryPatternMonthlySelectedOption(int entryPatternMonthlySelectedOption) {
        this.entryPatternMonthlySelectedOption = entryPatternMonthlySelectedOption;
    }
}
