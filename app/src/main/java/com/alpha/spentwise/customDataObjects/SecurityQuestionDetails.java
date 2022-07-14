package com.alpha.spentwise.customDataObjects;

public class SecurityQuestionDetails {

    private int questionID;
    private String question;

    public SecurityQuestionDetails(int questionID, String question) {
        this.questionID = questionID;
        this.question = question;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return question;
    }
}
