package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.EntryPatternDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.containsTrue;
import static com.alpha.spentwise.utils.CustomFunctions.getDailyDateList;
import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateString;
import static com.alpha.spentwise.utils.CustomFunctions.getDayInstance;
import static com.alpha.spentwise.utils.CustomFunctions.getDayInt;
import static com.alpha.spentwise.utils.CustomFunctions.getMonthlyDateList;
import static com.alpha.spentwise.utils.CustomFunctions.getWeeklyDateList;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;


public class EntryRepeatPatternActivity extends AppCompatActivity {

    private AutoCompleteTextView repeatPatternTextView, repeatMonthlyDayNumberDropDown, repeatMonthlyDayDropDown;
    private String[] repeatPatternList, repeatDayNumberOfMonthList, repeatDayOfMonthList;
    private ArrayAdapter repeatPatternAdapter, repeatDayOfMonthAdapter, repeatDayNumberOfMonthAdapter;
    private View patternView;
    private ViewGroup patternViewHolder;
    private EditText dailyRepeatDays, endAfterEntries, weeklyRepeatDays, repeatMonthEdt, repeatOnDayNumberEdt;
    private RadioButton endByRadioButton, endAfterRadioButton, repeatByDayOfMonthRBtn, repeatByDayOfWeekInMonthRBtn;
    private Button setEndDateButton;
    private SimpleDateFormat dateFormat = Constants.dateFormat;
    private int selectedDateEnd, selectedDateFrom;
    private String selectedDateString, monthlySelectedDayInstance, monthlySelectedDay;
    private Button savePattern, cancelPattern;
    private int selectedOption = 0, numOfEntries = 2, dailyInterval = 1, weeklyInterval = 1, monthlyInterval = 1, repeatDayOfMonth = 1, monthlySelectedOption = 0;
    private Long lastDateLong = 0l;
    private List<Integer> entryRepeatDateList = new ArrayList<>();
    private CheckBox monChk, tueChk, wedChk, thuChk, friChk, satChk, sunChk;
    private boolean[] checkedDays = new boolean[7];
    private EntryPatternDetails entryPatternDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_repeat_pattern);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.entryRepeatPatternActivityTitle);

        //Set the exposed dropdown values
        repeatPatternTextView = findViewById(R.id.entryRepeatPattern_atv);
        repeatPatternList = Constants.repeatType;
        patternViewHolder = (ViewGroup) findViewById(R.id.entryRepeatPattern_internalLayout_1);
        repeatPatternAdapter = new ArrayAdapter(this, R.layout.util_exposed_dropdown_item, repeatPatternList);
        repeatPatternTextView.setAdapter(repeatPatternAdapter);
        savePattern = findViewById(R.id.entryRepeatPattern_btn_savePattern);
        cancelPattern = findViewById(R.id.entryRepeatPattern_btn_cancel);
        endAfterEntries = findViewById(R.id.entryRepeatPattern_edt_endAfterEntries);
        endByRadioButton = findViewById(R.id.entryRepeatPattern_rBtn_repeatEndByDate);
        endAfterRadioButton = findViewById(R.id.entryRepeatPattern_rBtn_repeatEndAfterTimes);
        setEndDateButton = findViewById(R.id.entryRepeatPattern_btn_setEndByDate);
        repeatDayNumberOfMonthList = Constants.repeatDayInstance;
        repeatDayOfMonthList = Constants.daysOfWeek;

        if (patternView != null) {
            patternViewHolder.removeView(patternView);
        }
        try {
            loadSavedDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        repeatPatternTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (patternView != null) {
                    patternViewHolder.removeView(patternView);
                }
                if (repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[0])) {
                    patternView = getLayoutInflater().inflate(R.layout.layout_repeat_daily, null);
                    patternViewHolder.addView(patternView);
                    try {
                        setDailyRepeatLayout(patternView);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[1])) {
                    patternView = getLayoutInflater().inflate(R.layout.layout_repeat_weekly, null);
                    patternViewHolder.addView(patternView);
                    try {
                        setWeeklyRepeatLayout(patternView);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[2])) {
                    patternView = getLayoutInflater().inflate(R.layout.layout_repeat_monthly, null);
                    patternViewHolder.addView(patternView);
                    setMonthlyRepeatLayout(patternView);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Set up material date picker
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        try {
            constraintsBuilderRange.setValidator(DateValidatorPointForward.from(getDateLongCustom(selectedDateFrom, dateFormat)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        materialDateBuilder.setCalendarConstraints(constraintsBuilderRange.build());
        MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        //Set up radio button on check listeners
        endByRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endByRadioButton.setChecked(true);
                endAfterRadioButton.setChecked(false);
                setEndDateButton.setEnabled(true);
                endAfterEntries.setEnabled(false);
                selectedOption = 0;
            }
        });
        endAfterRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endByRadioButton.setChecked(false);
                endAfterRadioButton.setChecked(true);
                setEndDateButton.setEnabled(false);
                endAfterEntries.setEnabled(true);
                selectedOption = 1;
            }
        });

        //Set on click listener for set date button
        setEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        //Set date on Date picker
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                selectedDateString = getDateString(selection);
                setEndDateButton.setText(selectedDateString);
                selectedDateEnd = getDateIntCustom(selection, dateFormat);
                try {
                    lastDateLong = getDateLongCustom(selectedDateEnd,dateFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        savePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validations for the radio button options
                if(selectedOption == 0){
                    if(selectedDateEnd == 0){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the last date until the entry has to be added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(selectedOption == 1){
                    if(endAfterEntries.getText().toString().isEmpty()){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the number of entries", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    numOfEntries = Integer.parseInt(endAfterEntries.getText().toString());
                    if(numOfEntries < 2){
                        Toast.makeText(EntryRepeatPatternActivity.this, "The number of entries cannot be less than 2", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //Validations and get data for daily option
                if(repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[0])){
                    if(dailyRepeatDays.getText().toString().isEmpty()){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the daily interval", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dailyInterval = Integer.parseInt(dailyRepeatDays.getText().toString());
                    if(dailyInterval < 1){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Daily interval cannot be less than 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        entryRepeatDateList = getDailyDateList(selectedDateFrom,dailyInterval,selectedOption,lastDateLong,numOfEntries,dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Validations and get data for weekly option
                } else if(repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[1])){
                    checkedDays[0] = sunChk.isChecked();
                    checkedDays[1] = monChk.isChecked();
                    checkedDays[2] = tueChk.isChecked();
                    checkedDays[3] = wedChk.isChecked();
                    checkedDays[4] = thuChk.isChecked();
                    checkedDays[5] = friChk.isChecked();
                    checkedDays[6] = satChk.isChecked();
                    if(weeklyRepeatDays.getText().toString().isEmpty()){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the weekly interval", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    weeklyInterval = Integer.parseInt(weeklyRepeatDays.getText().toString());
                    if(weeklyInterval < 1){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Weekly interval cannot be less than 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!containsTrue(checkedDays)){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please selected at least 1 day of the week for repetition", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        entryRepeatDateList = getWeeklyDateList(selectedDateFrom,weeklyInterval,checkedDays,selectedOption,lastDateLong,numOfEntries,dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if(repeatPatternTextView.getText().toString().equalsIgnoreCase(repeatPatternList[2])){
                    if(repeatMonthEdt.getText().toString().isEmpty()){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the monthly interval", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    monthlyInterval = Integer.parseInt(repeatMonthEdt.getText().toString());
                    if(monthlyInterval < 1){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Monthly interval cannot be less than 1", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(repeatOnDayNumberEdt.getText().toString().isEmpty()){
                        Toast.makeText(EntryRepeatPatternActivity.this, "Please set the day of monthly repeat", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    repeatDayOfMonth = Integer.parseInt(repeatOnDayNumberEdt.getText().toString());
                    if(repeatDayOfMonth < 1 && repeatDayOfMonth > 31){
                        Toast.makeText(EntryRepeatPatternActivity.this, "The day of monthly repeat should be between 1 and 31", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    monthlySelectedDayInstance = repeatMonthlyDayNumberDropDown.getText().toString();
                    monthlySelectedDay = repeatMonthlyDayDropDown.getText().toString();

                    try {
                        entryRepeatDateList = getMonthlyDateList(selectedDateFrom,monthlyInterval,repeatDayOfMonth,getDayInstance(monthlySelectedDayInstance),getDayInt(monthlySelectedDay),monthlySelectedOption,selectedOption,lastDateLong,numOfEntries,dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(entryRepeatDateList.size() <= 1){
                    Toast.makeText(EntryRepeatPatternActivity.this, "There cannot be more than 1 repeat transaction based on the given criteria", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveSelectedDetails();
                ((UserProjectDataHolder)getApplication()).setEntryRepeatDateList(entryRepeatDateList);
                Intent intent = new Intent(EntryRepeatPatternActivity.this, ManageEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        cancelPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntryRepeatPatternActivity.this, ManageEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    public void setDailyRepeatLayout(View patternView) throws ParseException {
        dailyRepeatDays = patternView.findViewById(R.id.repeatDaily_edt_repeatInterval);

        //Set default values
        dailyRepeatDays.setText(String.valueOf(dailyInterval));

    }

    public void setWeeklyRepeatLayout(View patternView) throws ParseException {
        sunChk = findViewById(R.id.weeklyRepeat_chk_day1);
        monChk = findViewById(R.id.weeklyRepeat_chk_day2);
        tueChk = findViewById(R.id.weeklyRepeat_chk_day3);
        wedChk = findViewById(R.id.weeklyRepeat_chk_day4);
        thuChk = findViewById(R.id.weeklyRepeat_chk_day5);
        friChk = findViewById(R.id.weeklyRepeat_chk_day6);
        satChk = findViewById(R.id.weeklyRepeat_chk_day7);

        weeklyRepeatDays = patternView.findViewById(R.id.weeklyRepeat_edt_repeatInterval);
        //Set Default value
        weeklyRepeatDays.setText(String.valueOf(weeklyInterval));

    }

    public void setMonthlyRepeatLayout(View patternView) {

        repeatMonthEdt = patternView.findViewById(R.id.repeatMonthly_edt_repeatMonthNumber);
        repeatOnDayNumberEdt = patternView.findViewById(R.id.repeatMonthly_edt_repeatDayOfMonth);
        repeatMonthlyDayNumberDropDown = findViewById(R.id.repeatMonthly_aEdt_dayNumber);
        repeatMonthlyDayDropDown = findViewById(R.id.repeatMonthly_aEdt_day);
        repeatDayNumberOfMonthAdapter = new ArrayAdapter(this,R.layout.util_exposed_dropdown_item,repeatDayNumberOfMonthList);
        repeatDayOfMonthAdapter = new ArrayAdapter(this,R.layout.util_exposed_dropdown_item,repeatDayOfMonthList);
        repeatMonthlyDayNumberDropDown.setAdapter(repeatDayNumberOfMonthAdapter);
        repeatMonthlyDayDropDown.setAdapter(repeatDayOfMonthAdapter);
        repeatByDayOfMonthRBtn = patternView.findViewById(R.id.repeatMonthly_rBtn_repeatByDate);
        repeatByDayOfWeekInMonthRBtn = patternView.findViewById(R.id.repeatMonthly_rBtn_repeatByDay);

        //Set Default value
        repeatMonthEdt.setText(String.valueOf(monthlyInterval));
        repeatOnDayNumberEdt.setText(String.valueOf(repeatDayOfMonth));
        repeatByDayOfMonthRBtn.setChecked(true);
        repeatMonthlyDayNumberDropDown.setEnabled(false);
        repeatMonthlyDayDropDown.setEnabled(false);

        repeatByDayOfMonthRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatByDayOfWeekInMonthRBtn.setChecked(false);
                repeatMonthlyDayNumberDropDown.setEnabled(false);
                repeatMonthlyDayDropDown.setEnabled(false);
                repeatOnDayNumberEdt.setEnabled(true);
                monthlySelectedOption = 0;
            }
        });

        repeatByDayOfWeekInMonthRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatByDayOfMonthRBtn.setChecked(false);
                repeatOnDayNumberEdt.setEnabled(false);
                repeatMonthlyDayNumberDropDown.setEnabled(true);
                repeatMonthlyDayDropDown.setEnabled(true);
                monthlySelectedOption = 1;
            }
        });



    }

    private void loadSavedDetails() throws ParseException {

        entryPatternDetails = ((UserProjectDataHolder)getApplication()).getEntryPatternDetails();

        repeatPatternTextView.setText(entryPatternDetails.getEntryPatternName(),false);

        lastDateLong = getDateLongCustom(entryPatternDetails.getEntryPatternLastDateInt(),dateFormat);
        setEndDateButton.setText(getDateString(lastDateLong));
        selectedDateEnd = getDateIntCustom(lastDateLong,dateFormat);
        selectedDateFrom = entryPatternDetails.getEntryPatternStartDateInt();

        numOfEntries = entryPatternDetails.getEntryPatternNumberOfEntries();
        endAfterEntries.setText(String.valueOf(numOfEntries));

        selectedOption = entryPatternDetails.getEntryPatternSelectedOption();
        if(selectedOption == 0){
            endByRadioButton.setChecked(true);
            endAfterRadioButton.setChecked(false);
            setEndDateButton.setEnabled(true);
            endAfterEntries.setEnabled(false);
        } else {
            endByRadioButton.setChecked(false);
            endAfterRadioButton.setChecked(true);
            setEndDateButton.setEnabled(false);
            endAfterEntries.setEnabled(true);
        }

        if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[0])) {
            patternView = getLayoutInflater().inflate(R.layout.layout_repeat_daily, null);
            patternViewHolder.addView(patternView);
            try {
                setDailyRepeatLayout(patternView);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dailyRepeatDays.setText(String.valueOf(entryPatternDetails.getEntryPatternDailyInterval()));
        } else if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[1])) {
            patternView = getLayoutInflater().inflate(R.layout.layout_repeat_weekly, null);
            patternViewHolder.addView(patternView);
            try {
                setWeeklyRepeatLayout(patternView);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            weeklyRepeatDays.setText(String.valueOf(entryPatternDetails.getEntryPatternWeeklyInterval()));
            checkedDays = entryPatternDetails.getEntryPatternSelectedCheckBoxes();
            sunChk.setChecked(checkedDays[0]);
            monChk.setChecked(checkedDays[1]);
            tueChk.setChecked(checkedDays[2]);
            wedChk.setChecked(checkedDays[3]);
            thuChk.setChecked(checkedDays[4]);
            friChk.setChecked(checkedDays[5]);
            satChk.setChecked(checkedDays[6]);
        } else if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[2])) {
            patternView = getLayoutInflater().inflate(R.layout.layout_repeat_monthly, null);
            patternViewHolder.addView(patternView);
            setMonthlyRepeatLayout(patternView);
            repeatMonthEdt.setText(String.valueOf(entryPatternDetails.getEntryPatternMonthlyInterval()));
            repeatOnDayNumberEdt.setText(String.valueOf(entryPatternDetails.getEntryPatternMonthlyRepeatOnDay()));
            repeatMonthlyDayNumberDropDown.setText(entryPatternDetails.getEntryPatternMonthlyDayInstance(),false);
            repeatMonthlyDayDropDown.setText(entryPatternDetails.getEntryPatternMonthlyDayOfWeek(),false);
            monthlySelectedOption = entryPatternDetails.getEntryPatternMonthlySelectedOption();
            if(monthlySelectedOption == 0){
                repeatByDayOfMonthRBtn.setChecked(true);
                repeatByDayOfWeekInMonthRBtn.setChecked(false);
                repeatMonthlyDayNumberDropDown.setEnabled(false);
                repeatMonthlyDayDropDown.setEnabled(false);
                repeatOnDayNumberEdt.setEnabled(true);
            } else {
                repeatByDayOfMonthRBtn.setChecked(false);
                repeatByDayOfWeekInMonthRBtn.setChecked(true);
                repeatMonthlyDayNumberDropDown.setEnabled(true);
                repeatMonthlyDayDropDown.setEnabled(true);
                repeatOnDayNumberEdt.setEnabled(false);
            }

        }

    }

    private void saveSelectedDetails(){

        entryPatternDetails.setEntryPatternName(repeatPatternTextView.getText().toString());

        entryPatternDetails.setEntryPatternStartDateInt(selectedDateFrom);
        entryPatternDetails.setEntryPatternSelectedOption(selectedOption);

        if(selectedOption == 0){
            entryPatternDetails.setEntryPatternLastDateInt(selectedDateEnd);
        } else {
            entryPatternDetails.setEntryPatternNumberOfEntries(numOfEntries);
        }

        if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[0])) {
            entryPatternDetails.setEntryPatternDailyInterval(dailyInterval);
        } else if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[1])) {
            entryPatternDetails.setEntryPatternWeeklyInterval(weeklyInterval);
            checkedDays[0] = sunChk.isChecked();
            checkedDays[1] = monChk.isChecked();
            checkedDays[2] = tueChk.isChecked();
            checkedDays[3] = wedChk.isChecked();
            checkedDays[4] = thuChk.isChecked();
            checkedDays[5] = friChk.isChecked();
            checkedDays[6] = satChk.isChecked();
            entryPatternDetails.setEntryPatternSelectedCheckBoxes(checkedDays);
        } else if (entryPatternDetails.getEntryPatternName().equalsIgnoreCase(repeatPatternList[2])) {
            entryPatternDetails.setEntryPatternMonthlyInterval(monthlyInterval);
            entryPatternDetails.setEntryPatternMonthlySelectedOption(monthlySelectedOption);
            if(monthlySelectedOption == 0){
                entryPatternDetails.setEntryPatternMonthlyRepeatOnDay(repeatDayOfMonth);
            } else {
                entryPatternDetails.setEntryPatternMonthlyDayInstance(monthlySelectedDayInstance);
                entryPatternDetails.setEntryPatternMonthlyDayOfWeek(monthlySelectedDay);
            }
        }
        ((UserProjectDataHolder)getApplication()).setEntryPatternDetails(entryPatternDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        optionMenu(id,this,((UserProjectDataHolder)getApplication()).getLoggedInUser());

        return true;
    }


}