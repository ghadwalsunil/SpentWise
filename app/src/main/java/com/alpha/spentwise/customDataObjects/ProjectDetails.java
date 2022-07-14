package com.alpha.spentwise.customDataObjects;

public class ProjectDetails {

    private int userID;
    private int projectID;
    private String projectName;
    private String projectImageName;
    private int projectStatus;
    private String projectCurrencyID;
    private int projectStartAmount;
    private int projectMonthlyBudget;
    private int projectNotifyDate;

    public ProjectDetails(int userID, int projectID, String projectName, String projectImageName, int projectStatus, String projectCurrencyID, int projectStartAmount, int projectMonthlyBudget, int projectNotifyDate) {
        this.userID = userID;
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectImageName = projectImageName;
        this.projectStatus = projectStatus;
        this.projectCurrencyID = projectCurrencyID;
        this.projectStartAmount = projectStartAmount;
        this.projectMonthlyBudget = projectMonthlyBudget;
        this.projectNotifyDate = projectNotifyDate;
    }

    public ProjectDetails() {
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectImageName() {
        return projectImageName;
    }

    public void setProjectImageName(String projectImageName) {
        this.projectImageName = projectImageName;
    }

    public int getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(int projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectCurrencyID() {
        return projectCurrencyID;
    }

    public void setProjectCurrencyID(String projectCurrencyID) {
        this.projectCurrencyID = projectCurrencyID;
    }

    public int getProjectStartAmount() {
        return projectStartAmount;
    }

    public void setProjectStartAmount(int projectStartAmount) {
        this.projectStartAmount = projectStartAmount;
    }

    public int getProjectMonthlyBudget() {
        return projectMonthlyBudget;
    }

    public void setProjectMonthlyBudget(int projectMonthlyBudget) {
        this.projectMonthlyBudget = projectMonthlyBudget;
    }

    public int getProjectNotifyDate() {
        return projectNotifyDate;
    }

    public void setProjectNotifyDate(int projectNotifyDate) {
        this.projectNotifyDate = projectNotifyDate;
    }
}
