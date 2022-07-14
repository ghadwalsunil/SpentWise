package com.alpha.spentwise.customDataObjects;

public class UserDetails {

    private int userID;
    private String firstName;
    private String lastName;
    private String emailID;
    private String passwordString;
    private String passwordSaltString;
    private int userStatus;
    private int securityQuestionID;
    private String securityQuestionAnswer;
    private int userLoggedIn;

    public UserDetails(int userID, String firstName, String lastName, String emailID, String passwordString, String passwordSaltString, int userStatus, int securityQuestionID, String securityQuestionAnswer, int userLoggedIn) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.passwordString = passwordString;
        this.passwordSaltString = passwordSaltString;
        this.userStatus = userStatus;
        this.securityQuestionID = securityQuestionID;
        this.securityQuestionAnswer = securityQuestionAnswer;
        this.userLoggedIn = userLoggedIn;
    }
    public UserDetails() {
        //Empty constructor
    }

    public UserDetails(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPasswordString() {
        return passwordString;
    }

    public void setPasswordString(String passwordString) {
        this.passwordString = passwordString;
    }

    public String getPasswordSaltString() {
        return passwordSaltString;
    }

    public void setPasswordSaltString(String passwordSaltString) {
        this.passwordSaltString = passwordSaltString;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getSecurityQuestionID() {
        return securityQuestionID;
    }

    public void setSecurityQuestionID(int securityQuestionID) {
        this.securityQuestionID = securityQuestionID;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public int getUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(int userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

}
