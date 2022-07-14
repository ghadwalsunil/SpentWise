package com.alpha.spentwise.dataManager;

import android.app.Application;

import com.alpha.spentwise.customDataObjects.AppliedFilterDetails;
import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.ContactDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.EntryDetails;
import com.alpha.spentwise.customDataObjects.EntryPatternDetails;
import com.alpha.spentwise.customDataObjects.FilterDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.customDataObjects.TransactionTypeDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProjectDataHolder extends Application {

    private UserDetails loggedInUser;
    private ProjectDetails selectedProject;
    private CurrencyDetails selectedCurrencyDetails;
    private CurrencyDetails newCurrencyDetails;
    private CategoryDetails selectedCategoryDetails;
    private EntryDetails entryDetails;
    private TransactionModeDetails transactionModeDetails;
    private TransactionTypeDetails transactionTypeDetails;
    private List<Integer> entryRepeatDateList;
    private EntryPatternDetails entryPatternDetails;
    private String previousActivity;
    private AppliedFilterDetails appliedFilterDetails;
    private ContactDetails selectedContactDetails;

    public EntryPatternDetails getEntryPatternDetails() {
        return entryPatternDetails;
    }

    public void setEntryPatternDetails(EntryPatternDetails entryPatternDetails) {
        this.entryPatternDetails = entryPatternDetails;
    }

    public void resetEntryPatternDetails() {
        this.entryPatternDetails = new EntryPatternDetails(Constants.repeatType[0],1,0,0,2,1,new boolean[]{false, false, false, false, false, false, false},1,1,Constants.repeatDayInstance[0],Constants.daysOfWeek[0],0,0);
    }

    public List<Integer> getEntryRepeatDateList() {
        return entryRepeatDateList;
    }

    public void setEntryRepeatDateList(List<Integer> entryRepeatDateList) {
        this.entryRepeatDateList = entryRepeatDateList;
    }

    public void resetEntryRepeatDateList(){
        this.entryRepeatDateList = null;
    }

    public EntryDetails getEntryDetails() {
        return entryDetails;
    }

    public void setEntryDetails(EntryDetails entryDetails) {
        this.entryDetails = entryDetails;
    }

    public void resetEntryDetails() {
        this.entryDetails = new EntryDetails(-1,-1,-1,0,-1,-1,"",0,1);
    }

    public CategoryDetails getSelectedCategoryDetails() {
        return selectedCategoryDetails;
    }

    public void setSelectedCategoryDetails(CategoryDetails selectedCategoryDetails) {
        this.selectedCategoryDetails = selectedCategoryDetails;
    }

    public void resetSelectedCategoryDetails() {
        this.selectedCategoryDetails = new CategoryDetails(-1, "", 1, "ic_baseline_category_24",0);;
    }

    public CurrencyDetails getSelectedCurrencyDetails() {
        return selectedCurrencyDetails;
    }

    public void setSelectedCurrencyDetails(CurrencyDetails selectedCurrencyDetails) {
        this.selectedCurrencyDetails = selectedCurrencyDetails;
    }
    public void resetSelectedCurrencyDetails() {
        this.selectedCurrencyDetails = null;
    }

    public CurrencyDetails getNewCurrencyDetails() {
        return newCurrencyDetails;
    }

    public void setNewCurrencyDetails(CurrencyDetails newCurrencyDetails) {
        this.newCurrencyDetails = newCurrencyDetails;
    }

    public void resetNewCurrencyDetails() {
        this.newCurrencyDetails = null;
    }

    public ProjectDetails getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(ProjectDetails selectedProject) {
        this.selectedProject = selectedProject;
    }

    public void resetSelectedProject() {
        this.selectedProject = new ProjectDetails(-1,-1,"", Constants.imagesList[0],0,"",0,0,0);;
    }

    public UserDetails getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserDetails loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void resetLoggedInUser() {
        this.loggedInUser = new UserDetails(-1);;
    }

    public TransactionModeDetails getTransactionModeDetails() {
        return transactionModeDetails;
    }

    public void setTransactionModeDetails(TransactionModeDetails transactionModeDetails) {
        this.transactionModeDetails = transactionModeDetails;
    }

    public void resetTransactionModeDetails() {
        this.transactionModeDetails = new TransactionModeDetails(-1,"",1);;
    }

    public TransactionTypeDetails getTransactionTypeDetails() {
        return transactionTypeDetails;
    }

    public void setTransactionTypeDetails(TransactionTypeDetails transactionTypeDetails) {
        this.transactionTypeDetails = transactionTypeDetails;
    }

    public void resetTransactionTypeDetails() {
        this.transactionTypeDetails = new TransactionTypeDetails(-1,"");;
    }

    public String getPreviousActivity() {
        return previousActivity;
    }

    public void setPreviousActivity(String previousActivity) {
        this.previousActivity = previousActivity;
    }

    public void resetPreviousActivity() {
        this.previousActivity = "";
    }

    public AppliedFilterDetails getAppliedFilterDetails() {
        return appliedFilterDetails;
    }

    public void setAppliedFilterDetails(AppliedFilterDetails appliedFilterDetails) {
        this.appliedFilterDetails = appliedFilterDetails;
    }

    public void resetAppliedFilterDetails() {
        this.appliedFilterDetails = new AppliedFilterDetails(new ArrayList<>(Arrays.asList(-1)), new ArrayList<>(Arrays.asList(-1)), new ArrayList<>(Arrays.asList(-1)),new int[]{-1,-1},new int[]{-1,-1},0);
    }

    public ContactDetails getSelectedContactDetails() {
        return selectedContactDetails;
    }

    public void setSelectedContactDetails(ContactDetails selectedContactDetails) {
        this.selectedContactDetails = selectedContactDetails;
    }

    public void resetSelectedContactDetails() {
        this.selectedContactDetails = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loggedInUser = new UserDetails(-1);
        selectedProject = new ProjectDetails(-1,-1,"", Constants.imagesList[0],0,"",0,0,0);
        selectedCurrencyDetails = null;
        newCurrencyDetails = null;
        selectedCategoryDetails = new CategoryDetails(-1, "", 1, "ic_baseline_category_24",0);
        entryDetails = new EntryDetails(-1,-1,-1,0,-1,-1,"",0,1);
        transactionModeDetails = new TransactionModeDetails(-1,"",1);
        transactionTypeDetails = new TransactionTypeDetails(-1,"");
        entryRepeatDateList = null;
        entryPatternDetails = new EntryPatternDetails(Constants.repeatType[0],1,0,0,2,1,new boolean[]{false, false, false, false, false, false, false},1,1,Constants.repeatDayInstance[0],Constants.daysOfWeek[0],0,0);
        previousActivity = "";
        appliedFilterDetails = new AppliedFilterDetails(new ArrayList<>(Arrays.asList(-1)), new ArrayList<>(Arrays.asList(-1)), new ArrayList<>(Arrays.asList(-1)),new int[]{-1,-1},new int[]{-1,-1},0);
        selectedContactDetails = null;
    }
}
