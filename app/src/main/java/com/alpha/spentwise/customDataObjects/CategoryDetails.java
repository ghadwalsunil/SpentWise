package com.alpha.spentwise.customDataObjects;

public class CategoryDetails {

    private int categoryID;
    private String categoryName;
    private int categoryStatus;
    private String categoryImageName;
    private int categoryBudget;

    public CategoryDetails(int categoryID, String categoryName, int categoryStatus, String categoryImageName, int categoryBudget) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryStatus = categoryStatus;
        this.categoryImageName = categoryImageName;
        this.categoryBudget = categoryBudget;
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

    public int getCategoryStatus() {
        return categoryStatus;
    }

    public void setCategoryStatus(int categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    public String getCategoryImageName() {
        return categoryImageName;
    }

    public void setCategoryImageName(String categoryImageName) {
        this.categoryImageName = categoryImageName;
    }

    public int getCategoryBudget() {
        return categoryBudget;
    }

    public void setCategoryBudget(int categoryBudget) {
        this.categoryBudget = categoryBudget;
    }

    @Override
    public String toString() {
        return categoryName;
    }

}
