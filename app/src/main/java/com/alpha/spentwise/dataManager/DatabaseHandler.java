package com.alpha.spentwise.dataManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;
import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.DateStringIntegerFormat;
import com.alpha.spentwise.customDataObjects.EntryDetails;
import com.alpha.spentwise.customDataObjects.ExportEntryDetails;
import com.alpha.spentwise.customDataObjects.FilterDetails;
import com.alpha.spentwise.customDataObjects.MonthlyDataFromDB;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.customDataObjects.SecurityQuestionDetails;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.customDataObjects.TransactionTypeDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.customDateFormat;
import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.initialCategories;
import static com.alpha.spentwise.dataManager.Constants.initialCategoryImages;
import static com.alpha.spentwise.dataManager.Constants.initialModeOfTransactions;
import static com.alpha.spentwise.dataManager.Constants.initialTypeOfTransactions;
import static com.alpha.spentwise.dataManager.Constants.moneyLendingAndBorrowing;
import static com.alpha.spentwise.dataManager.Constants.securityQuestions;
import static com.alpha.spentwise.utils.CustomFunctions.getCustomMonthYear;
import static com.alpha.spentwise.utils.CustomFunctions.initialCleanupForImport;
import static com.alpha.spentwise.utils.CustomFunctions.initialValidationForImport;

public class DatabaseHandler extends SQLiteOpenHelper {

    //Database, Table and Column Name constants
    private static final String DB_NAME = "spentwise.db";

    //User table name and column names
    private static final String USER_TABLE_NAME = "USERTBL";
    private static final String USER_TABLE__USERID = "USERID";
    private static final String USER_TABLE__FIRST_NAME = "FIRSTNAME";
    private static final String USER_TABLE__LAST_NAME = "LASTNAME";
    private static final String USER_TABLE__EMAILID = "EMAILID";
    private static final String USER_TABLE__PASSWORD = "PASSWORD";
    private static final String USER_TABLE__PASSWORD_SALT = "PASSWORD_SALT";
    private static final String USER_TABLE__STATUS = "STATUS";
    private static final String USER_TABLE__SECURITY_QUESTION_ID = "QUESTION";
    private static final String USER_TABLE__SECURITY_QUESTION_ANSWER = "ANSWER";
    private static final String USER_TABLE__USER_LOGGED_IN = "USER_LOGGED_IN";

    //Security question table name and column names
    private static final String SECURITY_QUESTION_TABLE_NAME = "SECURITY_QUESTIONSTBL";
    private static final String SECURITY_QUESTION__QUESTION_ID = "QUESTION_ID";
    private static final String SECURITY_QUESTION__QUESTION = "QUESTIONS";

    //Project Table name and column names
    private static final String PROJECT_TABLE_NAME = "PROJECTTBL";
    private static final String PROJECT_TABLE__USERID = "USERID";
    private static final String PROJECT_TABLE__PROJECT_ID = "PROJECTID";
    private static final String PROJECT_TABLE__PROJECT_NAME = "PROJECTNAME";
    private static final String PROJECT_TABLE__PROJECT_IMAGE_NAME = "PROJECTIMAGENAME";
    private static final String PROJECT_TABLE__PROJECT_STATUS = "PROJECTSTATUS";
    private static final String PROJECT_TABLE__PROJECT_CURRENCY_ID = "PROJECTCURRENCYID";
    private static final String PROJECT_TABLE__PROJECT_START_AMOUNT = "PROJECTSTARTAMOUNT";
    private static final String PROJECT_TABLE__PROJECT_BUDGET = "PROJECTBUDGET";
    private static final String PROJECT_TABLE__PROJECT_NOTIFY_DATE = "NOTIFY_DATE";

    //Currency Table name and column names
    private static final String CURRENCY_TABLE_NAME = "CURRENCYTBL";
    private static final String CURRENCY_TABLE__CURRENCY_ID = "CURRENCY_ID";
    private static final String CURRENCY_TABLE__CURRENCY_NAME = "CURRENCY_NAME";
    private static final String CURRENCY_TABLE__SYMBOL = "CURRENCY_SYMBOL";

    //Entry Table name and column names
    private static final String ENTRYTBL_NAME = "ENTRYTBL";
    private static final String ENTRYTBL__ENTRY_NUMBER = "ENTRY_NUMBER";
    private static final String ENTRYTBL__CATEGORYID = "CATEGORYID";
    private static final String ENTRYTBL__ENTRY_TYPE = "ENTRY_TYPE";
    private static final String ENTRYTBL__AMOUNT = "AMOUNT";
    private static final String ENTRYTBL__DATE = "DATE";
    private static final String ENTRYTBL__MODE_OF_TRANSACTION = "MODE_OF_TRANSACTION";
    private static final String ENTRYTBL__COMMENTS = "COMMENTS";
    private static final String ENTRYTBL__REPEATED = "REPEATED";
    private static final String ENTRYTBL__STATUS = "STATUS";
    private static final String ENTRYTBL__PROJECT_ID = "PROJECT_ID";

    //Transaction Mode Table name and column names
    private static final String TRANSACTIONMASTERTBL_NAME = "TRANSACTIONMASTERTBL";
    private static final String TRANSACTIONMASTERTBL__MODE_ID = "MODE_ID";
    private static final String TRANSACTIONMASTERTBL__MODE_NAME = "MODE_NAME";
    private static final String TRANSACTIONMASTERTBL__MODE_STATUS = "MODE_STATUS";
    private static final String TRANSACTIONMASTERTBL__PROJECT_ID = "PROJECT_ID";

    //Category Table name and column names
    private static final String CATEGORYTBL_NAME = "CATEGORYTBL";
    private static final String CATEGORYTBL__CATEGORYID = "CATEGORYID";
    private static final String CATEGORYTBL__CATEGORYNAME = "CATEGORYNAME";
    private static final String CATEGORYTBL__CATEGORYSTATUS = "CATEGORYSTATUS";
    private static final String CATEGORYTBL__CATEGORYIMAGENAME = "CATEGORYIMAGENAME";
    private static final String CATEGORYTBL__CATEGORYBUDGET = "CATEGORYBUDGET";
    private static final String CATEGORYTBL__PROJECT_ID = "PROJECT_ID";
    private static final String CATEGORYTBL__NOTIFY_DATE = "NOTIFY_DATE";

    //Transaction Type Table name and column names
    private static final String TRANSACTIONTYPETBL_NAME = "TRANSACTIONTYPETBL";
    private static final String TRANSACTIONTYPETBL__TYPEID = "TYPE_ID";
    private static final String TRANSACTIONTYPETBL__TYPENAME = "TYPE_NAME";

    //Temp table name and columns
    private static final String TEMP_TABLE_NAME = "TEMPTBL";
    private static final String TEMP_TABLE__ENTRY_ID = "TEMP_ID";
    private static final String TEMP_TABLE__ENTRY_CATEGORY_NAME = "TEMP_CAT_NAME";
    private static final String TEMP_TABLE__ENTRY_TYPE = "TEMP_TYPE";
    private static final String TEMP_TABLE__ENTRY_AMOUNT = "TEMP_AMOUNT";
    private static final String TEMP_TABLE__ENTRY_DATE = "TEMP_DATE";
    private static final String TEMP_TABLE__ENTRY_MODE = "TEMP_MODE";
    private static final String TEMP_TABLE__ENTRY_COMMENT = "TEMP_COMMENT";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Create_EntryTableQuery = "CREATE TABLE " + ENTRYTBL_NAME + " ( " + ENTRYTBL__ENTRY_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + ENTRYTBL__CATEGORYID + " INTEGER NOT NULL, " + ENTRYTBL__ENTRY_TYPE + " INTEGER NOT NULL, " + ENTRYTBL__AMOUNT + " INTEGER NOT NULL CHECK( " + ENTRYTBL__AMOUNT + " <= 2147483647 AND " + ENTRYTBL__AMOUNT + " > 0), " + ENTRYTBL__DATE + " INTEGER NOT NULL, " + ENTRYTBL__MODE_OF_TRANSACTION + " INTEGER NOT NULL, " + ENTRYTBL__COMMENTS + " TEXT, " + ENTRYTBL__REPEATED + " INTEGER NOT NULL, " + ENTRYTBL__STATUS + " INTEGER NOT NULL, " + ENTRYTBL__PROJECT_ID + " INTEGER NOT NULL)";
        String Create_TransactionModeTableQuery = "CREATE TABLE " + TRANSACTIONMASTERTBL_NAME + " ( " + TRANSACTIONMASTERTBL__MODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + TRANSACTIONMASTERTBL__MODE_NAME + " TEXT NOT NULL, " + TRANSACTIONMASTERTBL__MODE_STATUS + " INTEGER NOT NULL, " + TRANSACTIONMASTERTBL__PROJECT_ID + " INTEGER NOT NULL)";
        String Create_CategoryTableQuery = "CREATE TABLE " + CATEGORYTBL_NAME + " ( " + CATEGORYTBL__CATEGORYID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + CATEGORYTBL__CATEGORYNAME + " TEXT NOT NULL, " + CATEGORYTBL__CATEGORYSTATUS + " INTEGER NOT NULL, " + CATEGORYTBL__CATEGORYIMAGENAME + " TEXT NOT NULL, " + CATEGORYTBL__CATEGORYBUDGET + " INTEGER NOT NULL, " + CATEGORYTBL__PROJECT_ID + " INTEGER NOT NULL, " + CATEGORYTBL__NOTIFY_DATE + " INTEGER NOT NULL)";
        String Create_TransactionTypeTableQuery = "CREATE TABLE " + TRANSACTIONTYPETBL_NAME + " ( " + TRANSACTIONTYPETBL__TYPEID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + TRANSACTIONTYPETBL__TYPENAME + " TEXT NOT NULL)";
        String Create_UserTableQuery = "CREATE TABLE " + USER_TABLE_NAME + " ( " + USER_TABLE__USERID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + USER_TABLE__FIRST_NAME + " TEXT NOT NULL, " + USER_TABLE__LAST_NAME + " TEXT NOT NULL, " + USER_TABLE__EMAILID + " TEXT NOT NULL, " + USER_TABLE__PASSWORD + " TEXT NOT NULL, " + USER_TABLE__PASSWORD_SALT + " TEXT NOT NULL, " + USER_TABLE__STATUS + " INTEGER NOT NULL, " + USER_TABLE__SECURITY_QUESTION_ID + " INTEGER NOT NULL, " + USER_TABLE__SECURITY_QUESTION_ANSWER + " TEXT NOT NULL, " + USER_TABLE__USER_LOGGED_IN + " INTEGER NOT NULL)";
        String Create_SecurityQuestionTableQuery = "CREATE TABLE " + SECURITY_QUESTION_TABLE_NAME + " ( " + SECURITY_QUESTION__QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + SECURITY_QUESTION__QUESTION + " TEXT NOT NULL)";
        String Create_ProjectTableQuery = "CREATE TABLE " + PROJECT_TABLE_NAME + " ( " + PROJECT_TABLE__USERID + " INTEGER NOT NULL, " + PROJECT_TABLE__PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + PROJECT_TABLE__PROJECT_NAME + " TEXT NOT NULL, " + PROJECT_TABLE__PROJECT_IMAGE_NAME + " TEXT NOT NULL, " + PROJECT_TABLE__PROJECT_STATUS + " INTEGER NOT NULL, " + PROJECT_TABLE__PROJECT_CURRENCY_ID + " TEXT NOT NULL, " + PROJECT_TABLE__PROJECT_START_AMOUNT + " INTEGER NOT NULL, " + PROJECT_TABLE__PROJECT_BUDGET + " INTEGER NOT NULL, " + PROJECT_TABLE__PROJECT_NOTIFY_DATE + " INTEGER NOT NULL)";
        String Create_CurrencyTableQuery = "CREATE TABLE " + CURRENCY_TABLE_NAME + " ( " + CURRENCY_TABLE__CURRENCY_ID + " TEXT NOT NULL PRIMARY KEY, " + CURRENCY_TABLE__CURRENCY_NAME + " TEXT NOT NULL, " + CURRENCY_TABLE__SYMBOL + " TEXT)";
        String Create_TempTableQuery = "CREATE TABLE " + TEMP_TABLE_NAME + " ( " + TEMP_TABLE__ENTRY_ID + " TEXT, " + TEMP_TABLE__ENTRY_CATEGORY_NAME + " TEXT, " + TEMP_TABLE__ENTRY_TYPE + " TEXT, " + TEMP_TABLE__ENTRY_AMOUNT + " TEXT, " + TEMP_TABLE__ENTRY_DATE + " TEXT, " + TEMP_TABLE__ENTRY_MODE + " TEXT, " + TEMP_TABLE__ENTRY_COMMENT + " TEXT)";

        db.execSQL(Create_EntryTableQuery);
        db.execSQL(Create_TransactionModeTableQuery);
        db.execSQL(Create_CategoryTableQuery);
        db.execSQL(Create_TransactionTypeTableQuery);
        db.execSQL(Create_UserTableQuery);
        db.execSQL(Create_SecurityQuestionTableQuery);
        db.execSQL(Create_ProjectTableQuery);
        db.execSQL(Create_CurrencyTableQuery);
        db.execSQL(Create_TempTableQuery);


        for (int i = 0; i < initialTypeOfTransactions.length; i++){
            ContentValues cv = new ContentValues();
            cv.put(TRANSACTIONTYPETBL__TYPENAME, initialTypeOfTransactions[i]);
            db.insert(TRANSACTIONTYPETBL_NAME,null,cv);
        }

        for (int i = 0; i < securityQuestions.length; i++){
            ContentValues cv = new ContentValues();
            cv.put(SECURITY_QUESTION__QUESTION, securityQuestions[i]);
            db.insert(SECURITY_QUESTION_TABLE_NAME,null,cv);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addDefaultTransactions(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < initialModeOfTransactions.length; i++){
            ContentValues cv = new ContentValues();
            cv.put(TRANSACTIONMASTERTBL__MODE_NAME, initialModeOfTransactions[i]);
            cv.put(TRANSACTIONMASTERTBL__MODE_STATUS, 1);
            cv.put(TRANSACTIONMASTERTBL__PROJECT_ID, projectDetails.getProjectID());
            db.insert(TRANSACTIONMASTERTBL_NAME,null,cv);
        }

        db.close();
    }

    public boolean addNewCategory(CategoryDetails categoryDetails, ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CATEGORYTBL__CATEGORYNAME, categoryDetails.getCategoryName());
        cv.put(CATEGORYTBL__CATEGORYSTATUS, 1);
        cv.put(CATEGORYTBL__CATEGORYIMAGENAME, categoryDetails.getCategoryImageName());
        cv.put(CATEGORYTBL__CATEGORYBUDGET,categoryDetails.getCategoryBudget());
        cv.put(CATEGORYTBL__PROJECT_ID, projectDetails.getProjectID());
        cv.put(CATEGORYTBL__NOTIFY_DATE, 0);

        long insert = db.insert(CATEGORYTBL_NAME, null, cv);

        db.close();

        if(insert == -1)
            return false;
        else
            return true;
    }

    public boolean updateCategory(CategoryDetails categoryDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CATEGORYTBL__CATEGORYNAME, categoryDetails.getCategoryName());
        cv.put(CATEGORYTBL__CATEGORYSTATUS, 1);
        cv.put(CATEGORYTBL__CATEGORYIMAGENAME, categoryDetails.getCategoryImageName());
        cv.put(CATEGORYTBL__CATEGORYBUDGET, categoryDetails.getCategoryBudget());

        long update = db.update(CATEGORYTBL_NAME,cv,CATEGORYTBL__CATEGORYID + " = " + categoryDetails.getCategoryID(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean deleteCategory(CategoryDetails categoryDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CATEGORYTBL__CATEGORYSTATUS, 0);

        long update = db.update(CATEGORYTBL_NAME,cv,CATEGORYTBL__CATEGORYID + " = " + categoryDetails.getCategoryID(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean updateCategoryNotificationDate(int categoryID, int date){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CATEGORYTBL__NOTIFY_DATE, date);

        long update = db.update(CATEGORYTBL_NAME,cv,CATEGORYTBL__CATEGORYID + " = " + categoryID, null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean addTransactionMode(TransactionModeDetails transactionModeDetails, ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TRANSACTIONMASTERTBL__MODE_NAME, transactionModeDetails.getTransactionModeName());
        cv.put(TRANSACTIONMASTERTBL__MODE_STATUS, 1);
        cv.put(TRANSACTIONMASTERTBL__PROJECT_ID, projectDetails.getProjectID());

        long status = db.insert(TRANSACTIONMASTERTBL_NAME, null, cv);

        db.close();

        if(status == -1)
            return false;
        else
            return true;
    }

    public boolean addCategoriesAndUpdateTransactionModesOfNewProject(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TRANSACTIONMASTERTBL__PROJECT_ID, projectDetails.getProjectID());

        long update = db.update(TRANSACTIONMASTERTBL_NAME,cv,TRANSACTIONMASTERTBL__PROJECT_ID + " = -1", null);

        for (int i = 0; i < initialCategories.length; i++){
            cv = new ContentValues();
            cv.put(CATEGORYTBL__CATEGORYNAME, initialCategories[i]);
            cv.put(CATEGORYTBL__CATEGORYSTATUS, 1);
            cv.put(CATEGORYTBL__CATEGORYIMAGENAME, initialCategoryImages[i]);
            cv.put(CATEGORYTBL__CATEGORYBUDGET, 0);
            cv.put(CATEGORYTBL__PROJECT_ID, projectDetails.getProjectID());
            cv.put(CATEGORYTBL__NOTIFY_DATE, 0);
            db.insert(CATEGORYTBL_NAME,null,cv);
        }

        for (int i = 0; i < moneyLendingAndBorrowing.length; i++){
            cv = new ContentValues();
            cv.put(CATEGORYTBL__CATEGORYNAME, moneyLendingAndBorrowing[i]);
            cv.put(CATEGORYTBL__CATEGORYSTATUS, -1);
            cv.put(CATEGORYTBL__CATEGORYIMAGENAME, "ic_baseline_category_24");
            cv.put(CATEGORYTBL__CATEGORYBUDGET, 0);
            cv.put(CATEGORYTBL__PROJECT_ID, projectDetails.getProjectID());
            cv.put(CATEGORYTBL__NOTIFY_DATE, 0);
            db.insert(CATEGORYTBL_NAME,null,cv);
        }

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean deleteTransactionModeForProject(TransactionModeDetails transactionModeDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TRANSACTIONMASTERTBL__MODE_STATUS, 0);

        long update = db.update(TRANSACTIONMASTERTBL_NAME,cv,TRANSACTIONMASTERTBL__MODE_ID + " = " + transactionModeDetails.getTransactionModeID(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean deleteRedundantTransactionModes(){

        SQLiteDatabase db = this.getWritableDatabase();

        long status = db.delete(TRANSACTIONMASTERTBL_NAME,TRANSACTIONMASTERTBL__PROJECT_ID + " = -1",null);

        db.close();

        if(status == -1)
            return false;
        else
            return true;
    }

    public boolean updateEntriesWithNewCurrencies(Double[] conversionRate, ProjectDetails projectDetails){
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "UPDATE " + ENTRYTBL_NAME + " SET " + ENTRYTBL__AMOUNT + " = ROUND( " + ENTRYTBL__AMOUNT + " * " + conversionRate[0] + " ) WHERE " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        try {
            db.execSQL(queryString);
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean checkIfCategoryAlreadyExists(CategoryDetails categoryDetails, ProjectDetails projectDetails){

        String queryString = "SELECT " + CATEGORYTBL__CATEGORYNAME + " FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__CATEGORYSTATUS + " = 1 AND " + CATEGORYTBL__CATEGORYID + " <> " + categoryDetails.getCategoryID() + " AND " + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                String categoryName = cursor.getString(0);

                if(categoryName.equalsIgnoreCase(categoryDetails.getCategoryName()))
                    return true;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return false;
    }

    public List<CategoryDetails> getActiveCategories(ProjectDetails projectDetails){

        List<CategoryDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__CATEGORYSTATUS + " = 1 AND " + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int categoryId = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                String categoryImageName = cursor.getString(3);
                int categoryBudget = cursor.getInt(4);

                CategoryDetails category = new CategoryDetails(categoryId, categoryName, 1, categoryImageName,categoryBudget);
                returnList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<CategoryDetails> getSpecialCategories(ProjectDetails projectDetails){

        List<CategoryDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__CATEGORYSTATUS + " = -1 AND " + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int categoryId = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                String categoryImageName = cursor.getString(3);
                int categoryBudget = cursor.getInt(4);

                CategoryDetails category = new CategoryDetails(categoryId, categoryName, 1, categoryImageName,categoryBudget);
                returnList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<CategoryDetails> getAllCategories(ProjectDetails projectDetails){

        List<CategoryDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int categoryId = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                int categoryStatus = cursor.getInt(2);
                String categoryImageName = cursor.getString(3);
                int categoryBudget = cursor.getInt(4);

                CategoryDetails category = new CategoryDetails(categoryId, categoryName, categoryStatus, categoryImageName,categoryBudget);
                returnList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<TransactionModeDetails> getActiveTransactionModes(ProjectDetails projectDetails){

        List<TransactionModeDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TRANSACTIONMASTERTBL_NAME + " WHERE " + TRANSACTIONMASTERTBL__MODE_STATUS + " = 1 AND " + TRANSACTIONMASTERTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int transactionModeID = cursor.getInt(0);
                String transactionModeName = cursor.getString(1);

                TransactionModeDetails transactionMode = new TransactionModeDetails(transactionModeID, transactionModeName, 1);
                returnList.add(transactionMode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<TransactionModeDetails> getAllTransactionModes(ProjectDetails projectDetails){

        List<TransactionModeDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TRANSACTIONMASTERTBL_NAME + " WHERE " + TRANSACTIONMASTERTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int transactionModeID = cursor.getInt(0);
                String transactionModeName = cursor.getString(1);
                int transactionModeStatus = cursor.getInt(2);

                TransactionModeDetails transactionMode = new TransactionModeDetails(transactionModeID, transactionModeName, transactionModeStatus);
                returnList.add(transactionMode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<TransactionTypeDetails> getTransactionTypes(){

        List<TransactionTypeDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TRANSACTIONTYPETBL_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int transactionTypeID = cursor.getInt(0);
                String transactionTypeName = cursor.getString(1);

                TransactionTypeDetails transactionType = new TransactionTypeDetails(transactionTypeID, transactionTypeName);
                returnList.add(transactionType);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public boolean addNewEntry(EntryDetails entryDetails, ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ENTRYTBL__CATEGORYID,entryDetails.getEntryCategoryID());
        cv.put(ENTRYTBL__ENTRY_TYPE,entryDetails.getEntryType());
        cv.put(ENTRYTBL__AMOUNT,entryDetails.getEntryAmount());
        cv.put(ENTRYTBL__DATE,entryDetails.getEntryDate());
        cv.put(ENTRYTBL__MODE_OF_TRANSACTION,entryDetails.getEntryMode());
        cv.put(ENTRYTBL__COMMENTS,entryDetails.getEntryComment());
        cv.put(ENTRYTBL__REPEATED,entryDetails.getEntryRepeat());
        cv.put(ENTRYTBL__STATUS,entryDetails.getEntryStatus());
        cv.put(ENTRYTBL__PROJECT_ID,projectDetails.getProjectID());

        long insert = db.insert(ENTRYTBL_NAME, null, cv);

        db.close();

        if(insert == -1)
            return false;
        else
            return true;
    }

    public boolean addRepeatEntry(EntryDetails entryDetails, List<Integer> futureDateList, ProjectDetails projectDetails, int currentDate){

        SQLiteDatabase db = this.getWritableDatabase();
        int insertStatus = 1;
        for(int i=0; i< futureDateList.size(); i++){
            ContentValues cv = new ContentValues();
            cv.put(ENTRYTBL__CATEGORYID,entryDetails.getEntryCategoryID());
            cv.put(ENTRYTBL__ENTRY_TYPE,entryDetails.getEntryType());
            cv.put(ENTRYTBL__AMOUNT,entryDetails.getEntryAmount());
            cv.put(ENTRYTBL__DATE,futureDateList.get(i));
            cv.put(ENTRYTBL__MODE_OF_TRANSACTION,entryDetails.getEntryMode());
            cv.put(ENTRYTBL__COMMENTS,entryDetails.getEntryComment());
            cv.put(ENTRYTBL__REPEATED,entryDetails.getEntryRepeat());
            if(futureDateList.get(i) > currentDate){
                cv.put(ENTRYTBL__STATUS,2);
            } else {
                cv.put(ENTRYTBL__STATUS,1);
            }
            cv.put(ENTRYTBL__PROJECT_ID,projectDetails.getProjectID());
            long insert = db.insert(ENTRYTBL_NAME, null, cv);
            if(insert == -1)
                insertStatus = -1;
        }
        db.close();

        if(insertStatus == -1)
            return false;
        else
            return true;
    }

    public boolean deleteEntry(EntryDetails entryDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ENTRYTBL__STATUS, 0);

        long update = db.update(ENTRYTBL_NAME,cv,ENTRYTBL__ENTRY_NUMBER + " = " + entryDetails.getEntryNumber(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public int updateRepeatEntryStatus(ProjectDetails projectDetails, int currentDate){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ENTRYTBL__STATUS, 1);

        db.update(ENTRYTBL_NAME,cv,ENTRYTBL__STATUS + " = 2 AND " + ENTRYTBL__DATE + " <= " + currentDate + " AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID(), null);

        Cursor cursor = db.rawQuery("SELECT CHANGES() as CHANGES",null);

        int rowsUpdated = 0;
        if(cursor.moveToFirst()){
            do{
                rowsUpdated = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return rowsUpdated;

    }

    public List<EntryDetails> getActiveEntries(ProjectDetails projectDetails){

        List<EntryDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " ORDER BY " + ENTRYTBL__ENTRY_NUMBER + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int temp_entryID = cursor.getInt(0);
                int temp_entryCategoryID = cursor.getInt(1);
                int temp_entryTypeID = cursor.getInt(2);
                int temp_entryAmount = cursor.getInt(3);
                int temp_entryDate = cursor.getInt(4);
                int temp_entryMode = cursor.getInt(5);
                String temp_entryComment = cursor.getString(6);
                int temp_entryRepeat = cursor.getInt(7);
                int temp_entryStatus = cursor.getInt(8);

                EntryDetails temp_entry = new EntryDetails(temp_entryID,temp_entryCategoryID,temp_entryTypeID,temp_entryAmount,temp_entryDate,temp_entryMode,temp_entryComment,temp_entryRepeat,temp_entryStatus);
                returnList.add(temp_entry);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public FilterDetails getFilterAttributes(ProjectDetails projectDetails){

        List<CategoryDetails> returnCategoryList = new ArrayList<>();
        List<TransactionModeDetails> returnTransactionModeList = new ArrayList<>();
        List<TransactionTypeDetails> returnTransactionTypeList = new ArrayList<>();
        int entryMaxAmount = 0, entryMinAmount = 0, entryMaxDate = 0, entryMinDate = 0;
        FilterDetails filterDetails = new FilterDetails(returnCategoryList,returnTransactionModeList,returnTransactionTypeList,entryMaxAmount,entryMinAmount,entryMaxDate,entryMinDate);

        String queryStringCategory = "SELECT * FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__CATEGORYID + " IN ( SELECT " + ENTRYTBL__CATEGORYID + " FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + ")";
        String queryStringTransactionMode = "SELECT * FROM " + TRANSACTIONMASTERTBL_NAME + " WHERE " + TRANSACTIONMASTERTBL__MODE_ID + " IN ( SELECT " + ENTRYTBL__MODE_OF_TRANSACTION + " FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + ")";
        String queryStringTransactionType = "SELECT * FROM " + TRANSACTIONTYPETBL_NAME + " WHERE " + TRANSACTIONTYPETBL__TYPEID + " IN ( SELECT " + ENTRYTBL__ENTRY_TYPE + " FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + ")";
        String queryStringRemaining = "SELECT MAX(" + ENTRYTBL__AMOUNT + ") , MIN(" + ENTRYTBL__AMOUNT + ") , MAX(" + ENTRYTBL__DATE + ") , MIN(" + ENTRYTBL__DATE + ") FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        //Get entry categories from database
        cursor = db.rawQuery(queryStringCategory,null);
        if(cursor.moveToFirst()){
            do{
                int categoryID = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                int categoryStatus = cursor.getInt(2);
                String categoryImageName = cursor.getString(3);
                int categoryBudget = cursor.getInt(4);

                CategoryDetails categoryDetails = new CategoryDetails(categoryID,categoryName,categoryStatus,categoryImageName,categoryBudget);
                returnCategoryList.add(categoryDetails);
            } while (cursor.moveToNext());
        }
        filterDetails.setCategoryDetails(returnCategoryList);

        //Get entry transaction modes from database
        cursor = db.rawQuery(queryStringTransactionMode,null);
        if(cursor.moveToFirst()){
            do{
                int transactionModeID = cursor.getInt(0);
                String transactionModeName = cursor.getString(1);
                int transactionModeStatus = cursor.getInt(2);

                TransactionModeDetails transactionModeDetails = new TransactionModeDetails(transactionModeID,transactionModeName,transactionModeStatus);
                returnTransactionModeList.add(transactionModeDetails);
            } while (cursor.moveToNext());
        }
        filterDetails.setTransactionModeDetails(returnTransactionModeList);

        //Get entry transaction types from database
        cursor = db.rawQuery(queryStringTransactionType,null);
        if(cursor.moveToFirst()){
            do{
                int transactionTypeID = cursor.getInt(0);
                String transactionTypeName = cursor.getString(1);

                TransactionTypeDetails transactionTypeDetails = new TransactionTypeDetails(transactionTypeID,transactionTypeName);
                returnTransactionTypeList.add(transactionTypeDetails);
            } while (cursor.moveToNext());
        }
        filterDetails.setTransactionTypeDetails(returnTransactionTypeList);

        //Get max amount, min amount, max date and min date
        cursor = db.rawQuery(queryStringRemaining,null);
        if(cursor.moveToFirst()){
            do{
                filterDetails.setMaxAmount(cursor.getInt(0));
                filterDetails.setMinAmount(cursor.getInt(1));
                filterDetails.setMaxDate(cursor.getInt(2));
                filterDetails.setMinDate(cursor.getInt(3));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return filterDetails;
    }

    public int getActiveEntryCount(ProjectDetails projectDetails){

        int entryCount = 0;
        String queryString = "SELECT COUNT(*) FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                entryCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entryCount;
    }

    public int getDailyNewEntryCount(int currentDate, ProjectDetails projectDetails){

        int entryCount = 0;
        String queryString = "SELECT COUNT(*) FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND " + ENTRYTBL__DATE + " = " + currentDate;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                entryCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entryCount;
    }

    public List<Pair<String,Integer>> getDataForPieChart(int startDate,int endDate,int entryType, ProjectDetails projectDetails){

        String queryString = "SELECT CT." + CATEGORYTBL__CATEGORYNAME + " , SUM(ET." + ENTRYTBL__AMOUNT + ") FROM " + CATEGORYTBL_NAME + " CT INNER JOIN " + ENTRYTBL_NAME + " ET ON CT." + CATEGORYTBL__CATEGORYID + " = ET." + ENTRYTBL__CATEGORYID + " WHERE ET." + ENTRYTBL__DATE + " >= " + startDate + " AND ET." + ENTRYTBL__DATE + " < " + endDate + " AND ET." + ENTRYTBL__STATUS + " = 1 AND ET." + ENTRYTBL__ENTRY_TYPE + " = " + entryType + " AND ET." + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " GROUP BY CT." + CATEGORYTBL__CATEGORYNAME;

        SQLiteDatabase db = this.getReadableDatabase();

        List<Pair<String,Integer>> returnList = new ArrayList<>();
                Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                String categoryName = cursor.getString(0);
                int sumForMonth = cursor.getInt(1);
                Pair<String,Integer> pair = new Pair<>(categoryName,sumForMonth);
                returnList.add(pair);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;

    }

    public List<CategoryBudgetDetails> getBudgetData(int startDate, int endDate, int entryType, ProjectDetails projectDetails){

        String queryString = "SELECT CATEGORYID, CATEGORYNAME, CATEGORYIMAGENAME, MAX(MONTHLY_SUM), BUDGET, NOTIFY_DATE FROM ( "
                + "SELECT CT."+ CATEGORYTBL__CATEGORYID + " AS CATEGORYID , CT." + CATEGORYTBL__CATEGORYNAME + " AS CATEGORYNAME , CT." + CATEGORYTBL__CATEGORYIMAGENAME + " AS CATEGORYIMAGENAME , SUM(ET." + ENTRYTBL__AMOUNT + ") AS MONTHLY_SUM , CT." + CATEGORYTBL__CATEGORYBUDGET + " AS BUDGET , CT." + CATEGORYTBL__NOTIFY_DATE + " AS NOTIFY_DATE FROM  " + CATEGORYTBL_NAME + " CT INNER JOIN " + ENTRYTBL_NAME + " ET ON CT." + CATEGORYTBL__CATEGORYID + "  = ET." + ENTRYTBL__CATEGORYID + "  WHERE ET." + ENTRYTBL__DATE + "  >=  " + startDate + " AND ET." + ENTRYTBL__DATE + " <  " + endDate + " AND ET." + ENTRYTBL__STATUS + " = 1 AND ET." + ENTRYTBL__ENTRY_TYPE + " = " + entryType + " AND ET." + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND CT." + CATEGORYTBL__CATEGORYBUDGET + " > 0 AND " + CATEGORYTBL__CATEGORYSTATUS + " = 1 GROUP BY CT." + CATEGORYTBL__CATEGORYID + " "
                + "UNION "
                + "SELECT " + CATEGORYTBL__CATEGORYID + " , " + CATEGORYTBL__CATEGORYNAME + " , " + CATEGORYTBL__CATEGORYIMAGENAME + " , 0 , " + CATEGORYTBL__CATEGORYBUDGET + " , " + CATEGORYTBL__NOTIFY_DATE + " FROM " + CATEGORYTBL_NAME + " WHERE " + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND " + CATEGORYTBL__CATEGORYBUDGET + " > 0 AND " + CATEGORYTBL__CATEGORYSTATUS + " = 1) T1 "
                + "GROUP BY " + CATEGORYTBL__CATEGORYID;

        SQLiteDatabase db = this.getReadableDatabase();

        List<CategoryBudgetDetails> returnList = new ArrayList<>();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int categoryID = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                String categoryImageName = cursor.getString(2);
                int sumForMonth = cursor.getInt(3);
                int categoryBudget = cursor.getInt(4);
                int categoryNotificationDate = cursor.getInt(5);
                CategoryBudgetDetails categoryBudgetDetails = new CategoryBudgetDetails(categoryID,categoryName,categoryImageName,sumForMonth,categoryBudget,categoryNotificationDate);
                returnList.add(categoryBudgetDetails);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;

    }

    public int getSumOverIntervalAndType(int startDate, int endDate, int type, ProjectDetails projectDetails){
        //Specify the type of entries that you want to consider for sum

        String queryString = "SELECT SUM(" + ENTRYTBL__AMOUNT + ") FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__DATE + " >= " + startDate + " AND " + ENTRYTBL__DATE + " < " + endDate + " AND " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__ENTRY_TYPE + " = " + type + " AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        int sum = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                sum = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sum;
    }

    public int getMinDate(ProjectDetails projectDetails){
        //Specify the type of entries that you want to consider for sum

        String queryString = "SELECT MIN(" + ENTRYTBL__DATE + ") FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID();

        int minDate = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                minDate = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return minDate;
    }

    public ArrayList<MonthlyDataFromDB> getMonthlySumForType(int type, ProjectDetails projectDetails){
        //Specify the type of entries that you want to consider for sum

        String queryString = "SELECT (( " + ENTRYTBL__DATE + "/100)*100+1) AS NEW_DATE,SUM( " + ENTRYTBL__AMOUNT + " ) FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND " + ENTRYTBL__STATUS + " = 1 AND " + ENTRYTBL__ENTRY_TYPE + " = " + type + " GROUP BY NEW_DATE  ORDER BY NEW_DATE";
        ArrayList<MonthlyDataFromDB> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int tempDate = cursor.getInt(0);
                int tempAmount = cursor.getInt(1);
                MonthlyDataFromDB monthlyDataFromDB = new MonthlyDataFromDB(tempDate,"",tempAmount);
                returnList.add(monthlyDataFromDB);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<DateStringIntegerFormat> getMonthList(ProjectDetails projectDetails) throws ParseException {
        //Specify the type of entries that you want to consider for sum

        String queryString = "SELECT (( " + ENTRYTBL__DATE + "/100)*100+1) AS NEW_DATE FROM " + ENTRYTBL_NAME + " WHERE " + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND " + ENTRYTBL__STATUS + " = 1 GROUP BY NEW_DATE ORDER BY NEW_DATE";
        ArrayList<DateStringIntegerFormat> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int tempDate = cursor.getInt(0);
                String tempDateString = getCustomMonthYear(tempDate,dateFormat,customDateFormat);
                returnList.add(new DateStringIntegerFormat(tempDateString,tempDate));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<SecurityQuestionDetails> getSecurityQuestions(){

        List<SecurityQuestionDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + SECURITY_QUESTION_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int temp_questionID = cursor.getInt(0);
                String temp_question = cursor.getString(1);

                SecurityQuestionDetails temp = new SecurityQuestionDetails(temp_questionID,temp_question);
                returnList.add(temp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public boolean addNewUser(UserDetails userDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_TABLE__FIRST_NAME,userDetails.getFirstName());
        cv.put(USER_TABLE__LAST_NAME,userDetails.getLastName());
        cv.put(USER_TABLE__EMAILID,userDetails.getEmailID());
        cv.put(USER_TABLE__PASSWORD,userDetails.getPasswordString());
        cv.put(USER_TABLE__PASSWORD_SALT,userDetails.getPasswordSaltString());
        cv.put(USER_TABLE__SECURITY_QUESTION_ID,userDetails.getSecurityQuestionID());
        cv.put(USER_TABLE__SECURITY_QUESTION_ANSWER,userDetails.getSecurityQuestionAnswer());
        cv.put(USER_TABLE__STATUS,userDetails.getUserStatus());
        cv.put(USER_TABLE__USER_LOGGED_IN,userDetails.getUserLoggedIn());

        long insert = db.insert(USER_TABLE_NAME, null, cv);

        db.close();

        if(insert == -1)
            return false;
        else
            return true;
    }

    public boolean checkIfUserAlreadyExists(String emailID){

        String queryString = "SELECT " + USER_TABLE__EMAILID + " FROM " + USER_TABLE_NAME + " WHERE " + USER_TABLE__STATUS + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                String userEmailID = cursor.getString(0);

                if(userEmailID.equalsIgnoreCase(emailID))
                    return true;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return false;
    }

    public UserDetails getUserDetails(String emailID){

        String queryString = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_TABLE__EMAILID + " = ? AND " + USER_TABLE__STATUS + " = 1";

        UserDetails userDetails = new UserDetails();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{emailID});

        if(cursor.moveToFirst()){
            do{
                userDetails.setUserID(cursor.getInt(0));
                userDetails.setFirstName(cursor.getString(1));
                userDetails.setLastName(cursor.getString(2));
                userDetails.setEmailID(cursor.getString(3));
                userDetails.setPasswordString(cursor.getString(4));
                userDetails.setPasswordSaltString(cursor.getString(5));
                userDetails.setUserStatus(cursor.getInt(6));
                userDetails.setSecurityQuestionID(cursor.getInt(7));
                userDetails.setSecurityQuestionAnswer(cursor.getString(8));
                userDetails.setUserLoggedIn(cursor.getInt(9));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userDetails;
    }

    public UserDetails getLoggedInUserDetails(){

        String queryString = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_TABLE__USER_LOGGED_IN + " = 1 AND " + USER_TABLE__STATUS + " = 1";

        UserDetails userDetails = new UserDetails();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                userDetails.setUserID(cursor.getInt(0));
                userDetails.setFirstName(cursor.getString(1));
                userDetails.setLastName(cursor.getString(2));
                userDetails.setEmailID(cursor.getString(3));
                userDetails.setPasswordString(cursor.getString(4));
                userDetails.setPasswordSaltString(cursor.getString(5));
                userDetails.setUserStatus(cursor.getInt(6));
                userDetails.setSecurityQuestionID(cursor.getInt(7));
                userDetails.setSecurityQuestionAnswer(cursor.getString(8));
                userDetails.setUserLoggedIn(cursor.getInt(9));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userDetails;
    }

    public boolean setUserLoggedInStatus(String emailID){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_TABLE__USER_LOGGED_IN, 0);

        db.update(USER_TABLE_NAME,cv,null,null);
        cv = new ContentValues();
        cv.put(USER_TABLE__USER_LOGGED_IN, 1);

        long update = db.update(USER_TABLE_NAME,cv,USER_TABLE__EMAILID + " = ?", new String[]{emailID});

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean setUserLogOutStatus(UserDetails userDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_TABLE__USER_LOGGED_IN, 0);

        long update = db.update(USER_TABLE_NAME,cv,USER_TABLE__USERID + " = " + userDetails.getUserID(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public List<ProjectDetails> getProjectForUser(UserDetails userDetails){

        List<ProjectDetails> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + PROJECT_TABLE_NAME + " WHERE " + PROJECT_TABLE__USERID + " = " + userDetails.getUserID() + " AND " + PROJECT_TABLE__PROJECT_STATUS + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int temp_userID = cursor.getInt(0);
                int temp_projectID = cursor.getInt(1);
                String temp_projectName = cursor.getString(2);
                String temp_projectImageName = cursor.getString(3);
                int temp_projectStatus = cursor.getInt(4);
                String temp_projectCurrencyID = cursor.getString(5);
                int temp_projectAmount = cursor.getInt(6);
                int temp_projectBudget = cursor.getInt(7);
                int temp_projectNotifyDate = cursor.getInt(8);

                ProjectDetails temp = new ProjectDetails(temp_userID,temp_projectID,temp_projectName,temp_projectImageName,temp_projectStatus,temp_projectCurrencyID,temp_projectAmount,temp_projectBudget,temp_projectNotifyDate);
                returnList.add(temp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public boolean checkIfProjectAlreadyExistsForUser(ProjectDetails projectDetails){

        String queryString = "SELECT " + PROJECT_TABLE__PROJECT_NAME + " FROM " + PROJECT_TABLE_NAME + " WHERE " + PROJECT_TABLE__USERID + " = " + projectDetails.getUserID() + " AND " + PROJECT_TABLE__PROJECT_ID + " <> " + projectDetails.getProjectID() + " AND " + PROJECT_TABLE__PROJECT_STATUS + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                String projectName = cursor.getString(0);

                if(projectName.equalsIgnoreCase(projectDetails.getProjectName()))
                    return true;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return false;
    }

    public boolean updateUserPassword(UserDetails userDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_TABLE__PASSWORD, userDetails.getPasswordString());
        cv.put(USER_TABLE__PASSWORD_SALT, userDetails.getPasswordSaltString());

        long update = db.update(USER_TABLE_NAME,cv,USER_TABLE__EMAILID + " = ?", new String[]{userDetails.getEmailID()});

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public boolean addNewProject(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PROJECT_TABLE__USERID,projectDetails.getUserID());
        cv.put(PROJECT_TABLE__PROJECT_NAME,projectDetails.getProjectName());
        cv.put(PROJECT_TABLE__PROJECT_IMAGE_NAME,projectDetails.getProjectImageName());
        cv.put(PROJECT_TABLE__PROJECT_STATUS,projectDetails.getProjectStatus());
        cv.put(PROJECT_TABLE__PROJECT_CURRENCY_ID,projectDetails.getProjectCurrencyID());
        cv.put(PROJECT_TABLE__PROJECT_START_AMOUNT,projectDetails.getProjectStartAmount());
        cv.put(PROJECT_TABLE__PROJECT_BUDGET,projectDetails.getProjectMonthlyBudget());
        cv.put(PROJECT_TABLE__PROJECT_NOTIFY_DATE,0);

        long status = 0;

        status = db.insert(PROJECT_TABLE_NAME, null, cv);

        db.close();

        if(status == -1)
            return false;
        else
            return true;

    }

    public boolean updateProject(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PROJECT_TABLE__USERID,projectDetails.getUserID());
        cv.put(PROJECT_TABLE__PROJECT_NAME,projectDetails.getProjectName());
        cv.put(PROJECT_TABLE__PROJECT_IMAGE_NAME,projectDetails.getProjectImageName());
        cv.put(PROJECT_TABLE__PROJECT_STATUS,projectDetails.getProjectStatus());
        cv.put(PROJECT_TABLE__PROJECT_CURRENCY_ID,projectDetails.getProjectCurrencyID());
        cv.put(PROJECT_TABLE__PROJECT_START_AMOUNT,projectDetails.getProjectStartAmount());
        cv.put(PROJECT_TABLE__PROJECT_BUDGET,projectDetails.getProjectMonthlyBudget());

        long status = 0;

        status = db.update(PROJECT_TABLE_NAME,cv,PROJECT_TABLE__PROJECT_ID + " = " + projectDetails.getProjectID(), null);

        db.close();

        if(status == -1)
            return false;
        else
            return true;

    }

    public boolean updateProjectNotificationDate(int projectID, int date){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PROJECT_TABLE__PROJECT_NOTIFY_DATE, date);

        long update = db.update(PROJECT_TABLE_NAME,cv,PROJECT_TABLE__PROJECT_ID + " = " + projectID, null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public ProjectDetails getProjectDetails(ProjectDetails projectDetails){

        ProjectDetails temp = new ProjectDetails();

        String queryString = "SELECT * FROM " + PROJECT_TABLE_NAME + " WHERE " + PROJECT_TABLE__USERID + " = " + projectDetails.getUserID() + " AND " + PROJECT_TABLE__PROJECT_STATUS + " = 1 AND " + PROJECT_TABLE__PROJECT_NAME + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,new String[]{projectDetails.getProjectName()});

        if(cursor.moveToFirst()){
            do{
                int temp_userID = cursor.getInt(0);
                int temp_projectID = cursor.getInt(1);
                String temp_projectName = cursor.getString(2);
                String temp_projectImageName = cursor.getString(3);
                int temp_projectStatus = cursor.getInt(4);
                String temp_projectCurrencyID = cursor.getString(5);
                int temp_projectAmount = cursor.getInt(6);
                int temp_projectBudget = cursor.getInt(7);
                int temp_projectNotifyDate = cursor.getInt(8);

                temp = new ProjectDetails(temp_userID,temp_projectID,temp_projectName,temp_projectImageName,temp_projectStatus,temp_projectCurrencyID,temp_projectAmount,temp_projectBudget,temp_projectNotifyDate);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return temp;
    }

    public boolean addCurrencyDetails(CurrencyDetails currencyDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CURRENCY_TABLE__CURRENCY_ID,currencyDetails.getId());
        cv.put(CURRENCY_TABLE__CURRENCY_NAME,currencyDetails.getCurrencyName());
        cv.put(CURRENCY_TABLE__SYMBOL,currencyDetails.getCurrencySymbol());

        long status = 0;

        String checkIfExistsQueryString = "SELECT " + CURRENCY_TABLE__CURRENCY_ID + " FROM " + CURRENCY_TABLE_NAME;

        Cursor cursor = db.rawQuery(checkIfExistsQueryString,null);

        if(cursor.moveToFirst()){
            do{
                String currencyID = cursor.getString(0);

                if(currencyID.equalsIgnoreCase(currencyDetails.getId()))
                    return true;
            } while (cursor.moveToNext());
        }
        cursor.close();

        status = db.insert(CURRENCY_TABLE_NAME, null, cv);

        db.close();

        if(status == -1)
            return false;
        else
            return true;

    }

    public CurrencyDetails getCurrencyDetails(ProjectDetails projectDetails){

        CurrencyDetails temp = new CurrencyDetails();

        String queryString = "SELECT * FROM " + CURRENCY_TABLE_NAME + " WHERE " + CURRENCY_TABLE__CURRENCY_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,new String[]{projectDetails.getProjectCurrencyID()});

        if(cursor.moveToFirst()){
            do{
                String temp_currencyID = cursor.getString(0);
                String temp_currencyName = cursor.getString(1);
                String temp_currencySymbol = cursor.getString(2);

                temp = new CurrencyDetails(temp_currencyID,temp_currencyName,temp_currencySymbol);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return temp;
    }

    public boolean deleteProject(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PROJECT_TABLE__PROJECT_STATUS, 0);

        long update = db.update(PROJECT_TABLE_NAME,cv,PROJECT_TABLE__PROJECT_ID + " = " + projectDetails.getProjectID(), null);

        db.close();

        if(update == -1)
            return false;
        else
            return true;
    }

    public List<ExportEntryDetails> exportEntryListForProject(ProjectDetails projectDetails){

        List<ExportEntryDetails> returnList = new ArrayList<>();

        String queryString = "SELECT T1." + ENTRYTBL__ENTRY_NUMBER + " , T2." + CATEGORYTBL__CATEGORYNAME + " , T3." + TRANSACTIONTYPETBL__TYPENAME + " , T1." + ENTRYTBL__AMOUNT + " , T1." + ENTRYTBL__DATE + " , T4." + TRANSACTIONMASTERTBL__MODE_NAME + " , T1." + ENTRYTBL__COMMENTS + " FROM " + ENTRYTBL_NAME + " T1 INNER JOIN " + CATEGORYTBL_NAME + " T2 ON T1." + ENTRYTBL__CATEGORYID + " = T2." + CATEGORYTBL__CATEGORYID + " INNER JOIN " + TRANSACTIONTYPETBL_NAME + " T3 ON T1." + ENTRYTBL__ENTRY_TYPE + " = T3." + TRANSACTIONTYPETBL__TYPEID + " INNER JOIN " + TRANSACTIONMASTERTBL_NAME + " T4 ON T1." + ENTRYTBL__MODE_OF_TRANSACTION + " = T4." + TRANSACTIONMASTERTBL__MODE_ID + " WHERE T1." + ENTRYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND T1." + ENTRYTBL__STATUS + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int temp_entryId = cursor.getInt(0);
                String temp_categoryName = cursor.getString(1);
                String temp_typeName = cursor.getString(2);
                int temp_entryAmount = cursor.getInt(3);
                int temp_entryDate = cursor.getInt(4);
                String temp_modeName = cursor.getString(5);
                String temp_comment = cursor.getString(6);

                ExportEntryDetails exportEntryDetails = new ExportEntryDetails(temp_entryId,temp_categoryName,temp_typeName,temp_entryAmount,temp_entryDate,temp_modeName,temp_comment);
                returnList.add(exportEntryDetails);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public boolean addToTempTable(List<String[]> stringList){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TEMP_TABLE_NAME);

        long insert = 0;

        for(int i = 0; i < stringList.size(); i++){

            if(stringList.get(i).length < 7)
                continue;

            String[] data = initialCleanupForImport(stringList.get(i));

            if(!initialValidationForImport(data,dateFormat)){
                continue;
            }

            ContentValues cv = new ContentValues();
            cv.put(TEMP_TABLE__ENTRY_ID, -1);
            cv.put(TEMP_TABLE__ENTRY_CATEGORY_NAME,stringList.get(i)[1]);
            cv.put(TEMP_TABLE__ENTRY_TYPE,stringList.get(i)[2]);
            cv.put(TEMP_TABLE__ENTRY_AMOUNT,stringList.get(i)[3]);
            cv.put(TEMP_TABLE__ENTRY_DATE,stringList.get(i)[4]);
            cv.put(TEMP_TABLE__ENTRY_MODE,stringList.get(i)[5]);
            cv.put(TEMP_TABLE__ENTRY_COMMENT,stringList.get(i)[6]);

            insert = db.insert(TEMP_TABLE_NAME, null, cv);

        }
        db.close();

        if(insert == -1)
            return false;
        else
            return true;
    }

    public int tempToEntryTransfer(ProjectDetails projectDetails){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = 0;

        String queryString =
                "INSERT INTO " + ENTRYTBL_NAME + " ( " + ENTRYTBL__CATEGORYID + " , " + ENTRYTBL__ENTRY_TYPE + " , " + ENTRYTBL__AMOUNT + " , " + ENTRYTBL__DATE + " , " + ENTRYTBL__MODE_OF_TRANSACTION + " , " + ENTRYTBL__COMMENTS + " , " + ENTRYTBL__REPEATED + " , " + ENTRYTBL__STATUS + " , " + ENTRYTBL__PROJECT_ID + " ) " +
                "SELECT T2." + CATEGORYTBL__CATEGORYID + " , T3." + TRANSACTIONTYPETBL__TYPEID + " , T1." + TEMP_TABLE__ENTRY_AMOUNT + " , T1." + TEMP_TABLE__ENTRY_DATE + " , T4." + TRANSACTIONMASTERTBL__MODE_ID + " , T1." + TEMP_TABLE__ENTRY_COMMENT + " , 0 , 2 , " + projectDetails.getProjectID() + " FROM " + TEMP_TABLE_NAME +" T1 " +
                "INNER JOIN " + CATEGORYTBL_NAME + " T2 ON T1." + TEMP_TABLE__ENTRY_CATEGORY_NAME + " = T2." + CATEGORYTBL__CATEGORYNAME + " " +
                "INNER JOIN " + TRANSACTIONTYPETBL_NAME + " T3 ON T1." + TEMP_TABLE__ENTRY_TYPE + " = T3." + TRANSACTIONTYPETBL__TYPENAME + " " +
                "INNER JOIN " + TRANSACTIONMASTERTBL_NAME + " T4 ON T1." + TEMP_TABLE__ENTRY_MODE + " = T4." + TRANSACTIONMASTERTBL__MODE_NAME + " " +
                "WHERE T2." + CATEGORYTBL__CATEGORYSTATUS + " = 1 AND T2." + CATEGORYTBL__PROJECT_ID + " = " + projectDetails.getProjectID() + " AND T4." + TRANSACTIONMASTERTBL__MODE_STATUS + " = 1 AND T4." + TRANSACTIONMASTERTBL__PROJECT_ID + " = 1";

        db.execSQL(queryString);

        Cursor cursor = db.rawQuery("SELECT CHANGES() as CHANGES",null);

        int rowsAdded = -1;
        if(cursor.moveToFirst()){
            do{
                rowsAdded = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        return rowsAdded;

    }

}
