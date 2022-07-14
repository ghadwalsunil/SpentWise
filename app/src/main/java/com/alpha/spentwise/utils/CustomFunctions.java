package com.alpha.spentwise.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;

import com.alpha.spentwise.R;
import com.alpha.spentwise.activities.AboutActivity;
import com.alpha.spentwise.activities.HelpActivity;
import com.alpha.spentwise.activities.LoginPageActivity;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CustomFunctions {

    public static String getDateString(Long date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    public static String getDateString(int date, SimpleDateFormat dateFormat) throws ParseException {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateFormat.parse(String.valueOf(date)));
    }

    public static int getDateIntCustom(Long date, SimpleDateFormat dateFormat) {
        return Integer.parseInt(dateFormat.format(date));
    }

    public static Long getDateLongCustom(int date, SimpleDateFormat dateFormat) throws ParseException {
        //date should be in yyyyMMdd format

        Date d = dateFormat.parse(Integer.toString(date));
        return d.getTime();
    }

    public static Long getNextDateLongCustom(int date, SimpleDateFormat dateFormat) throws ParseException {
        //date should be in yyyyMMdd format

        Date d = dateFormat.parse(Integer.toString(date));
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE,1);
        return c.getTimeInMillis();
    }

    public static String getCustomMonthYear(int date, SimpleDateFormat dateFormat, SimpleDateFormat customDateFormat) throws ParseException {

        Long dateLong = getDateLongCustom(date,dateFormat);
        return customDateFormat.format(dateLong);
    }

    public static int getNumberOfDaysBetweenDates(int laterDate, int earlierDate, SimpleDateFormat dateFormat) throws ParseException {

        Long dateDiff = getDateLongCustom(laterDate, dateFormat) - getDateLongCustom(earlierDate, dateFormat);

        int numberOfDays = (int) ((((dateDiff / 1000) / 60) / 60) / 24);

        return numberOfDays;
    }

    public static int getNextMonthIntCustom(int date, SimpleDateFormat dateFormat) throws ParseException {
        //date should be in yyyyMMdd format

        Date d = dateFormat.parse(Integer.toString(date));
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MONTH,1);
        return getDateIntCustom(c.getTimeInMillis(),dateFormat);
    }

    public static int getNumberOfDaysInMonth(int date, SimpleDateFormat dateFormat) throws ParseException {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(getDateLongCustom(date,dateFormat)));

        YearMonth yearMonth = YearMonth.of(c.get(Calendar.YEAR),c.get(Calendar.MONTH) - 1);
        return yearMonth.lengthOfMonth();
    }

    public static boolean containsTrue(boolean[] booleanArray){

        for(int i = 0; i < booleanArray.length; i++){
            if(booleanArray[i])
                return true;
        }
        return false;
    }

    public static List<Integer> getDailyDateList(int startDateInt, int interval, int option, Long lastDateLong, int numOfEntries, SimpleDateFormat dateFormat) throws ParseException {

        List<Integer> returnList = new ArrayList<>();
        Long startDateLong = getDateLongCustom(startDateInt, dateFormat);
        Date startDate;
        Date lastDate = new Date(lastDateLong);
        returnList.add(startDateInt);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(startDateLong));
        c.add(Calendar.DATE, interval);
        startDate = c.getTime();

        if (option == 0) {
            while (lastDate.compareTo(startDate) >= 0) {
                returnList.add(getDateIntCustom(startDate.getTime(), dateFormat));
                c.add(Calendar.DATE, interval);
                startDate = c.getTime();
            }
        } else {
            while (numOfEntries > 1) {
                c.add(Calendar.DATE, interval);
                returnList.add(getDateIntCustom(startDate.getTime(), dateFormat));
                startDate = c.getTime();
                numOfEntries--;
            }
        }

        return returnList;
    }

    public static List<Integer> getWeeklyDateList(int startDateInt, int interval, boolean[] checkedDays, int option, Long lastDateLong, int numOfEntries, SimpleDateFormat dateFormat) throws ParseException {

        List<Integer> returnList = new ArrayList<>();
        Long startDateLong = getDateLongCustom(startDateInt, dateFormat);
        Date startDate;
        Date lastDate = new Date(lastDateLong);
        int numOfTrues = 0;
        for(int i = 0; i < checkedDays.length; i++) {
            if(checkedDays[i])
                numOfTrues++;
        }
        int countForInterval = interval * numOfTrues;
        int addedDateCount = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(startDateLong));
        startDate = c.getTime();
        int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int nearestDay = -1;

        if(checkedDays[currentDayOfWeek - 1]) {
        }
        else {
            for(int i = 0; i < 7; i++) {
                if(checkedDays[(i + currentDayOfWeek - 1) % 7]) {
                    nearestDay = (i + currentDayOfWeek) % 7;
                    break;
                }
            }
            startDateLong = getDateOfNextGivenDayOfWeek(startDateLong, nearestDay);
            c.setTime(new Date(startDateLong));
            startDate = c.getTime();
        }

        if(startDateLong > lastDateLong)
            return returnList;

        if (option == 0) {
            do {
                if(addedDateCount < numOfTrues) {
                    returnList.add(getDateIntCustom(startDateLong,dateFormat));
                }
                addedDateCount++;
                if(addedDateCount == countForInterval)
                    addedDateCount = 0;
                currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                nearestDay = -1;

                for(int i = 0; i < 7; i++) {
                    if(checkedDays[(i + currentDayOfWeek) % 7]) {
                        nearestDay = (i + currentDayOfWeek + 1) % 7;
                        break;
                    }
                }
                startDateLong = getDateOfNextGivenDayOfWeek(startDateLong, nearestDay);
                c.setTime(new Date(startDateLong));
                startDate = c.getTime();
            } while (lastDate.compareTo(startDate) >= 0);
        } else {
            do {
                if(addedDateCount < numOfTrues) {
                    returnList.add(getDateIntCustom(startDateLong,dateFormat));
                    numOfEntries--;
                }
                addedDateCount++;
                if(addedDateCount == countForInterval)
                    addedDateCount = 0;
                currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                nearestDay = -1;

                for(int i = 0; i < 7; i++) {
                    if(checkedDays[(i + currentDayOfWeek) % 7]) {
                        nearestDay = (i + currentDayOfWeek + 1) % 7;
                        break;
                    }
                }
                startDateLong = getDateOfNextGivenDayOfWeek(startDateLong, nearestDay);
                c.setTime(new Date(startDateLong));
                startDate = c.getTime();
            } while (numOfEntries > 0);
        }
        return returnList;
    }

    public static Long getDateOfNextGivenDayOfWeek(Long currentDate, int day){

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(currentDate));

        int today = c.get(Calendar.DAY_OF_WEEK);

        if(day - today > 0){
            c.add(Calendar.DATE,day - today);
        } else
            c.add(Calendar.DATE, 7 -(today - day));
        return c.getTime().getTime();
    }

    public static List<Integer> getMonthlyDateList(int startDateInt, int interval, int repeatOnDay, int dayInstance, int dayName, int monthlyOption, int option, Long lastDateLong, int numOfEntries, SimpleDateFormat dateFormat) throws ParseException{
        List<Integer> returnList = new ArrayList<>();
        Long startDateLong = getDateLongCustom(startDateInt, dateFormat);
        Date startDate;
        Date lastDate = new Date(lastDateLong);
        int dateOfThisMonth;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(startDateLong));
        startDate = c.getTime();

        if(option == 0) {
            if(monthlyOption == 0) {
                if(startDateInt % 100 <= repeatOnDay) {
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay)
                        c.add(Calendar.MONTH, 1);
                    c.add(Calendar.DATE, repeatOnDay - startDateInt % 100);
                    startDateLong = c.getTimeInMillis();
                    startDateInt = getDateIntCustom(startDateLong, dateFormat);
                } else {
                    c.add(Calendar.MONTH, 1);
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay)
                        c.add(Calendar.MONTH, 1);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateInt = startDateInt - (startDateInt % 100) + repeatOnDay;
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                }

                startDate = new Date(startDateLong);
                c.setTime(startDate);

                if(lastDate.compareTo(startDate) < 0) {
                    return returnList;
                }

                do {
                    returnList.add(startDateInt);
                    c.add(Calendar.MONTH, interval);
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay) {
                        c.add(Calendar.MONTH, 1);
                    }
                    c.set(Calendar.DATE, repeatOnDay);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                    startDate = new Date(startDateLong);
                    c.setTime(startDate);
                } while(lastDate.compareTo(startDate) >= 0);
            } else {
                dateOfThisMonth = getDateOfGivenDayOfMonth(startDateInt, dayName, dayInstance, dateFormat);
                if(c.get(Calendar.DATE) <= dateOfThisMonth) {
                    c.set(Calendar.DATE, dateOfThisMonth);
                } else {
                    c.add(Calendar.MONTH, 1);
                    c.set(Calendar.DATE, 1);
                    dateOfThisMonth = getDateOfGivenDayOfMonth(getDateIntCustom(c.getTimeInMillis(), dateFormat), dayName, dayInstance, dateFormat);
                    c.set(Calendar.DATE, dateOfThisMonth);
                }
                startDate = new Date(c.getTimeInMillis());
                startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);

                if(lastDate.compareTo(startDate) < 0) {
                    return returnList;
                }
                do {
                    returnList.add(startDateInt);
                    c.add(Calendar.MONTH, interval);
                    c.set(Calendar.DATE, 1);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    dateOfThisMonth = getDateOfGivenDayOfMonth(startDateInt, dayName, dayInstance, dateFormat);
                    c.set(Calendar.DATE, dateOfThisMonth);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                    startDate = new Date(startDateLong);
                } while(lastDate.compareTo(startDate) >= 0);

            }
        } else {
            if(monthlyOption == 0) {
                if(startDateInt % 100 <= repeatOnDay) {
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay)
                        c.add(Calendar.MONTH, 1);
                    c.add(Calendar.DATE, repeatOnDay - startDateInt % 100);
                    startDateLong = c.getTimeInMillis();
                    startDateInt = getDateIntCustom(startDateLong, dateFormat);
                } else {
                    c.add(Calendar.MONTH, 1);
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay)
                        c.add(Calendar.MONTH, 1);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateInt = startDateInt - (startDateInt % 100) + repeatOnDay;
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                }

                startDate = new Date(startDateLong);
                c.setTime(startDate);

                do {
                    returnList.add(startDateInt);
                    numOfEntries--;
                    c.add(Calendar.MONTH, interval);
                    if(c.getActualMaximum(Calendar.DATE) < repeatOnDay) {
                        c.add(Calendar.MONTH, 1);
                    }
                    c.set(Calendar.DATE, repeatOnDay);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                    startDate = new Date(startDateLong);
                    c.setTime(startDate);
                } while(numOfEntries > 0);
            } else {
                dateOfThisMonth = getDateOfGivenDayOfMonth(startDateInt, dayName, dayInstance, dateFormat);
                if(c.get(Calendar.DATE) <= dateOfThisMonth) {
                    c.set(Calendar.DATE, dateOfThisMonth);
                } else {
                    c.add(Calendar.MONTH, 1);
                    c.set(Calendar.DATE, 1);
                    dateOfThisMonth = getDateOfGivenDayOfMonth(getDateIntCustom(c.getTimeInMillis(), dateFormat), dayName, dayInstance, dateFormat);
                    c.set(Calendar.DATE, dateOfThisMonth);
                }
                startDate = new Date(c.getTimeInMillis());
                startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);

                do {
                    returnList.add(startDateInt);
                    numOfEntries--;
                    c.add(Calendar.MONTH, interval);
                    c.set(Calendar.DATE, 1);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    dateOfThisMonth = getDateOfGivenDayOfMonth(startDateInt, dayName, dayInstance, dateFormat);
                    c.set(Calendar.DATE, dateOfThisMonth);
                    startDateInt = getDateIntCustom(c.getTimeInMillis(), dateFormat);
                    startDateLong = getDateLongCustom(startDateInt, dateFormat);
                    startDate = new Date(startDateLong);
                } while(numOfEntries > 0);

            }
        }
        return returnList;
    }

    public static int getDateOfGivenDayOfMonth(int currentDate, int day, int instance, SimpleDateFormat dateFormat) throws ParseException{

        int firstOfMonth = currentDate - (currentDate % 100) + 1;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(getDateLongCustom(firstOfMonth, dateFormat)));
        int firstDayOfMonth = c.get(Calendar.DAY_OF_WEEK);
        int date = 0;

        if(day - firstDayOfMonth >= 0){
            date = 1 + day - firstDayOfMonth + 7 * (instance - 1);
        } else
            date = 1 + 7 - (firstDayOfMonth - day) + 7 * (instance - 1);
        return date;

    }

    public static int getDayInt(String day) {
        switch (day) {
            case "Monday":
                return 2;
            case "Tuesday":
                return 3;
            case "Wednesday":
                return 4;
            case "Thursday":
                return 5;
            case "Friday":
                return 6;
            case "Saturday":
                return 7;
            default:
                return 1;
        }
    }
    public static int getDayInstance(String instance) {
        switch (instance) {
            case "Second":
                return 2;
            case "Third":
                return 3;
            case "Fourth":
                return 4;
            default:
                return 1;
        }
    }

    public static boolean isValidEmailID(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidName(CharSequence target) {
        String nameRegex = "^[a-zA-Z\\s]+$";
        return (!TextUtils.isEmpty(target) && Pattern.matches(nameRegex,target));
    }

    public static boolean isValidPassword(CharSequence target) {
        //The first regex to check whether it contains the 1 alphabet, 1 number,1 special character and 8 character limit
        String passwordRegex_1 = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        //The second regex to check if it contains characters outside of the specified
        String passwordRegex_2 = "^[a-zA-Z0-9#?!@$%^&*-]+$";
        return (!TextUtils.isEmpty(target) && Pattern.matches(passwordRegex_1,target) && Pattern.matches(passwordRegex_2,target));
    }

    public static boolean isValidAnswer(CharSequence target) {
        String answerRegex = "^[a-zA-Z0-9\\s]+$";
        return (!TextUtils.isEmpty(target) && Pattern.matches(answerRegex,target));
    }

    public static void optionMenu(int id, Context ctx, UserDetails userDetails){
        if (id== R.id.optionsMenu_about){
            Intent intent = new Intent(ctx.getApplicationContext(), AboutActivity.class);
            ctx.startActivity(intent);
        } else if (id==R.id.optionsMenu_help){
            Intent intent = new Intent(ctx.getApplicationContext(), HelpActivity.class);
            ctx.startActivity(intent);
        } else if (id==R.id.optionsMenu_exit){
            ((Activity)ctx).finishAffinity();
        } else if (id==R.id.optionsMenu_logOut){
            DatabaseHandler dbHandler = new DatabaseHandler(ctx);
            dbHandler.setUserLogOutStatus(userDetails);
            Intent intent = new Intent(ctx.getApplicationContext(), LoginPageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ctx.startActivity(intent);
        }
    }

    public static String[] initialCleanupForImport(String[] input){

        input[1] = input[1].replaceAll("[^a-zA-Z0-9\\s]","").trim().toUpperCase();
        input[2] = input[2].replaceAll("[^a-zA-Z]","").trim().toUpperCase();
        input[3] = input[3].replaceAll("[^0-9]","").trim();
        input[4] = input[4].replaceAll("[^0-9]","").trim();
        input[5] = input[5].replaceAll("[^a-zA-Z0-9\\s]","").trim().toUpperCase();
        input[6] = input[6].trim();

        return Arrays.copyOfRange(input, 0, input.length);
    }

    public static boolean initialValidationForImport(String[] input, SimpleDateFormat dateFormat){

        //input[0] = entryNumber
        //input[1] = entryCategory
        if(input[1].isEmpty()){
            return false;
        }
        //input[2] = entryType
        if(input[2].isEmpty()){
            return false;
        }
        //input[3] = entryAmount
        if(input[3].isEmpty()){
            return false;
        }
        try {
            if(Integer.parseInt(input[3]) <= 0){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //input[4] = entryDate
        if(input[4].isEmpty() || input[4].length() > 8){
            return false;
        }
        if(Integer.parseInt(input[4]) <= 0){
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "yyyyMMdd" , Locale.ENGLISH );
            LocalDate parsedDate = LocalDate.parse(input[4], formatter);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //input[5] = entryMode
        if(input[5].isEmpty()){
            return false;
        }
        //input[6] = entryComment
        return true;
    }

}
