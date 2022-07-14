package com.alpha.spentwise.dataManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Constants {

    //Initial Database entries (Make sure that the category names and images are in the same order)
    //Also, make sure that the images are also present in the drawable folder
    public static final String[] initialCategories = {
            "MEDICAL",
            "GROCERIES",
            "RENT",
            "FOOD",
            "EDUCATION"
    };

    public static final String[] moneyLendingAndBorrowing = {
            "BORROW",
            "LEND"
    };

    public static final String newProjectImageName = "project_add_new_project";

    public static final String[] initialCategoryImages = {
            "ic_baseline_medical_services_24",
            "ic_baseline_local_grocery_store_24",
            "ic_baseline_home_24",
            "ic_baseline_fastfood_24",
            "ic_baseline_menu_book_24"
    };
    public static final String[] initialModeOfTransactions = {
            "CASH",
            "DEBIT CARD",
            "CREDIT CARD"
    };
    public static final String[] initialTypeOfTransactions = {
            "DEBIT",
            "CREDIT"
    };
    public static final String[] securityQuestions = {
            "When you were young, what did you want to be when you grew up?",
            "Who was your childhood hero?",
            "Where was your best family vacation as a kid?",
            "What is your mother's maiden name?",
            "What is the name of your first pet?",
            "What was your first car?",
            "What elementary school did you attend?",
            "What is the name of the town where you were born?"
    };

    //List of available images for categories
    //Also, make sure that the images are also present in the drawable folder
    public static final String[] imagesList = {
            "ic_baseline_house_24",
            "ic_baseline_credit_card_24",
            "ic_baseline_medical_services_24",
            "ic_baseline_local_grocery_store_24",
            "ic_baseline_home_24",
            "ic_baseline_fastfood_24",
            "ic_baseline_sports_esports_24",
            "ic_baseline_menu_book_24",
            "ic_baseline_category_24",
            "ic_baseline_business_24",
            "ic_baseline_local_bar_24",
            "ic_baseline_apartment_24",
            "ic_baseline_engineering_24",
            "ic_baseline_account_balance_wallet_24",
            "ic_baseline_school_24",
            "ic_baseline_directions_bike_24",
            "ic_baseline_directions_car_24",
            "ic_baseline_airplanemode_active_24",
            "ic_baseline_train_24",
            "ic_baseline_laptop_24",
            "ic_baseline_smartphone_24"
    };

    //Toast Messages
    public static final String noEntryPresentMessage = "There are no entries present in the database";

    //Api info to get currency details
    public static final String currencyApiKey = "4f13e55d238b49c9dc03";
    public static final String baseURL = "https://free.currconv.com/api/v7/";
    public static final String getCurrencyListURL = baseURL + "currencies?apiKey=" + currencyApiKey;
    public static final String getConversionRateURLLeft = baseURL + "convert?apiKey=" + currencyApiKey + "&q=";
    public static final String getConversionRateURLRight = "&compact=y";
    public static final String unableToGetCurrencyListError = "Unable to get currency List";

    public static final String[] repeatType = {
            "DAILY",
            "WEEKLY",
            "MONTHLY"
    };

    public static final String[] repeatDayInstance = {
            "FIRST",
            "SECOND",
            "THIRD",
            "FOURTH"
    };

    public static final String[] daysOfWeek = {
            "SUNDAY",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY"
    };

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    //This date format is for the graph on main activity
    public static final SimpleDateFormat customDateFormat = new SimpleDateFormat("MMM-yyyy");
    public static final NumberFormat myFormat = NumberFormat.getInstance(Locale.ENGLISH);

    //Export file constants
    public static final String[] EXPORT_FILE_COLUMN_HEADERS = {"Entry ID","Category Name","Entry Type","Entry Amount","Entry Date","Mode of Transaction","Comments"};

    //Request code for permissions
    public static final int READ_CONTACT_PERMISSION_REQUEST = 100;
    public static final int READ_STORAGE_PERMISSION_REQUEST = 101;

    public static final String ABOUT_TEXT = "\n\nManage Money Effortlessly! \n\nSpentWise is your personal money manager. It is a free money tracking app to plan your budget, manage your expenses, and optimize your finance accordingly. \n\n\nThis application is developed by the students from Otto Von Guericke University, under the name of Team Alpha. \n\n\nWe love to get feedback from our users. You can contact us for any queries at:  \nTeamAlphaSpentWise@gmail.com";

    public static final String[] FAQ_QUESTIONS = {
            /* 1 */"What is this app about?",
            /* 2 */"What is a Project?",
            /* 3 */"Can I have multiple Projects?",
            /* 4 */"How can I add Project?",
            /* 5 */"Can I delete or edit a Project?",
            /* 6 */"How can I set a Project?",
            /* 7 */"How to add an expense?",
            /* 8 */"How to add repetitive or recurring transaction?",
            /* 9 */"Can I edit or delete a Category?",
            /* 10 */"How can I add a Category?",
            /* 11 */"Is there option while entering transaction to specify if I had lent or borrow money from someone?",
            /* 12 */"How to view the status of monthly budget?",
            /* 13 */"How to view the monthly expenses and income in different categories?",
            /* 14 */"How can I view the satistics of my expenses and income?",
            /* 15 */"How can I search the transactions made?",
            /* 16 */"How to search specific expense?"
    };

    public static final String[] FAQ_ANSWERS = {
            /* 1 */"This app is about managing your expenses and incomes, daily monthly or yearly.",
            /* 2 */"You can save and manage your expenses and incomes in different categories or under different Projects. Like if you want to save your domestic incomes and expenses, you can save it under project name “Home”.",
            /* 3 */"Yes, you can make multiple projects.\n",
            /* 4 */"In the Project Page by clicking on a Plus icon naming “ADD NEW PROJECT”, you can add a new Project.",
            /* 5 */"Yes, you can delete or edit an existing Project. On Project Page, there are two buttons for Edit or Delete a project.",
            /* 6 */"By clicking on a Plus icon in Project page, you can add a new Project. You have to provide Project Name. You can set your desired currency and Starting amount. You can customize the image of Project. On this page, you have to provide monthly Budget and Transaction mode like By Card or By Cash.",
            /* 7 */"To Add an expense click on ADD button in bottom navigation.\nClick on a Category e.g MEDICAL to select.\nClick again to Proceed.\nClick on Proceed with button.\nEnter required entries like Amount, Date, Transaction Mode, Transaction Type and description or comment.\nAfter this click on SUBMIT button.",
            /* 8 */"To Add an repetitive transaction click on ADD button in bottom navigation.\nClick on a Category e.g MEDICAL to select. \nClick again to Proceed.\nClick on Proceed with button.\nHere you have an option of REPEAT TRANSACTION. \nYou can SET REPEAT PATTERN\nAfter this click on SUBMIT button.",
            /* 9 */"Yes, you can add or delete a Category.\nclick on ADD button in bottom navigation.\nOn this page you have two Flag Buttons .\nBy clicking on Yellow Flag Button you can edit a category.\nBy clicking on Red Flag Button you can delete a category.",
            /* 10 */"Click on ADD button in bottom navigation.\nBy clicking on Green Plus sign Flag Button you can add a category.\nYou can enter \nCATEGORY NAME,\nCATEGPORY BUDGET,\nCATEGORY IMAGE. \nYou can save the category by clicking on SAVE CATEGORY BUTTON.",
            /* 11 */"Click on ADD button in bottom navigation.\nClick on MONEY LENT if you lent money to someone in your contact list.\nClick on MONEY BORROWED if you borrowed money from someone in your contact list.\nYou have to give access to your contact if you want to use this feature.",
            /* 12 */"Click on the HOME icon in the Bottom Navigation Bar\nClick on the ANALYTICS button\nClick on the Budget option\nMonthly Budget Status will be shown in Pie Chart\n",
            /* 13 */"Click on the HOME icon in the Bottom Navigation Bar\nClick on the ANALYTICS button\nClick on the CATEGORY SPREAD option\nMonthly CATEGORYWISE DISTRIBUTION of expenses and income will be shown in Pie Chart\n",
            /* 14 */"Click on the HOME icon in the Bottom Navigation Bar\nClick on the ANALYTICS button\nClick on STATISTICS option\nStatistics of Average Income and Expenses will be shown for a specific time period on daily, weekly and monthly basis.\n",
            /* 15 */"Click on Search button in bottom navigation menu.\nHere you can see the Transactions made\nYou can Sort the transaction by Amount and by Date by Selecting AMOUNT or DATE from SORT BY option\nYou can Sort the transaction by Order and by Date by Selecting ASCENDING or DESCENDING from SORT ORDER option\n",
            /* 16 */"Click on Search button in bottom navigation menu.\nClick on the red filter button in bottom right corner to apply on your transaction.\nApply the desired filters like\nYou can select \nMultiple Categories\nAmount Range\nDate Range\nTransaction Mode\nTransaction Type\n"


    };
}
