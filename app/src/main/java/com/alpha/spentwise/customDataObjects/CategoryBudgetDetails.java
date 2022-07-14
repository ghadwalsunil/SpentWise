package com.alpha.spentwise.customDataObjects;

public class CategoryBudgetDetails {

    private int categoryID;
    private String categoryName;
    private String categoryImageName;
    private int categoryExpenseThisMonth;
    private int categoryBudget;
    private int categoryNotificationDate;

    public CategoryBudgetDetails(int categoryID, String categoryName, String categoryImageName, int categoryExpenseThisMonth, int categoryBudget, int categoryNotificationDate) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryImageName = categoryImageName;
        this.categoryExpenseThisMonth = categoryExpenseThisMonth;
        this.categoryBudget = categoryBudget;
        this.categoryNotificationDate = categoryNotificationDate;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImageName() {
        return categoryImageName;
    }

    public void setCategoryImageName(String categoryImageName) {
        this.categoryImageName = categoryImageName;
    }

    public int getCategoryExpenseThisMonth() {
        return categoryExpenseThisMonth;
    }

    public void setCategoryExpenseThisMonth(int categoryExpenseThisMonth) {
        this.categoryExpenseThisMonth = categoryExpenseThisMonth;
    }

    public int getCategoryBudget() {
        return categoryBudget;
    }

    public void setCategoryBudget(int categoryBudget) {
        this.categoryBudget = categoryBudget;
    }

    public int getCategoryNotificationDate() {
        return categoryNotificationDate;
    }

    public void setCategoryNotificationDate(int categoryNotificationDate) {
        this.categoryNotificationDate = categoryNotificationDate;
    }
}
