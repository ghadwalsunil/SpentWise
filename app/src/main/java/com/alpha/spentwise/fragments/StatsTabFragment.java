package com.alpha.spentwise.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.myFormat;
import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateString;
import static com.alpha.spentwise.utils.CustomFunctions.getNextDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getNumberOfDaysBetweenDates;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsTabFragment extends Fragment {

    private TextView avgDailyIncomeTextView, avgWeeklyIncomeTextView, avgMonthlyIncomeTextView, avgDailyExpenseTextView, avgWeeklyExpenseTextView, avgMonthlyExpenseTextView;
    private Button dateRangeButton;
    private DatabaseHandler dbHandler;
    private ProjectDetails selectedProjectDetails;
    private CurrencyDetails selectedCurrencyDetails;
    private Currency cur;
    private int minDate = 0, currentDate = 0;
    private Context ctx;
    private MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder;
    private MaterialDatePicker materialDatePicker;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsTabFragment newInstance(String param1, String param2) {
        StatsTabFragment fragment = new StatsTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats_tab, container, false);

        avgDailyIncomeTextView = view.findViewById(R.id.statsFragment_tv_avgDailyIncomeTitle);
        avgWeeklyIncomeTextView = view.findViewById(R.id.statsFragment_tv_avgWeeklyIncomeTitle);
        avgMonthlyIncomeTextView = view.findViewById(R.id.statsFragment_tv_avgMonthlyIncomeTitle);
        avgDailyExpenseTextView = view.findViewById(R.id.statsFragment_tv_avgDailyExpenseTitle);
        avgWeeklyExpenseTextView = view.findViewById(R.id.statsFragment_tv_avgWeeklyExpenseTitle);
        avgMonthlyExpenseTextView = view.findViewById(R.id.statsFragment_tv_avgMonthlyExpenseTitle);
        dateRangeButton = view.findViewById(R.id.statsFragment_btn_dateRange);
        ctx = getContext();
        dbHandler = new DatabaseHandler(ctx);

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getActivity().getApplication()).getSelectedProject();
        selectedCurrencyDetails = ((UserProjectDataHolder)getActivity().getApplication()).getSelectedCurrencyDetails();
        cur = Currency.getInstance(selectedCurrencyDetails.getId());
        minDate = dbHandler.getMinDate(selectedProjectDetails);
        currentDate = getDateIntCustom(Calendar.getInstance().getTime().getTime(),dateFormat);
        if(minDate == 0 || minDate > currentDate){
            minDate = currentDate;
        }
        try {
            currentDate = getDateIntCustom(getNextDateLongCustom(currentDate,dateFormat),dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        populateData();
        try {
            setDateRangeButton();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Open date picker on click of date range button
        dateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                Long startDate = selection.first;
                Long endDate = selection.second;
                minDate = Integer.parseInt(dateFormat.format(startDate));
                currentDate = Integer.parseInt(dateFormat.format(endDate));
                String dateString = getDateString(startDate) + " - " + getDateString(endDate);
                dateRangeButton.setText(dateString);
                populateData();
            }
        });

        return view;
    }

    private void populateData(){

        float numOfMonths = 1f, numOfWeeks = 1f, numOfDays = 1f;
        int debitAmount = 0, creditAmount = 0;
        int debitType = 1, creditType = 2;
        String avgDailyExpense = cur.getSymbol() + " " + myFormat.format(0);
        String avgWeeklyExpense = cur.getSymbol() + " " + myFormat.format(0);
        String avgMonthlyExpense = cur.getSymbol() + " " + myFormat.format(0);
        String avgDailyIncome = cur.getSymbol() + " " + myFormat.format(0);
        String avgWeeklyIncome = cur.getSymbol() + " " + myFormat.format(0);
        String avgMonthlyIncome = cur.getSymbol() + " " + myFormat.format(0);

        if(minDate > 0 && minDate < currentDate){
            debitAmount = dbHandler.getSumOverIntervalAndType(minDate, currentDate, debitType, selectedProjectDetails);
            creditAmount = dbHandler.getSumOverIntervalAndType(minDate,currentDate,creditType,selectedProjectDetails);
            try {
                numOfDays = (float)getNumberOfDaysBetweenDates(currentDate, minDate, dateFormat);
            } catch (ParseException e) {
                e.printStackTrace();
                numOfDays = 1;
            }
            numOfWeeks = numOfDays / 7;
            numOfMonths = numOfDays / 30;

            if(numOfWeeks <= 0){
                numOfWeeks = 1;
            }
            if(numOfMonths <= 0){
                numOfMonths = 1;
            }

            avgDailyIncome = cur.getSymbol() + " " + myFormat.format(Math.round((float)creditAmount / numOfDays));
            avgWeeklyIncome = cur.getSymbol() + " " + myFormat.format(Math.round((float)creditAmount / numOfWeeks));
            avgMonthlyIncome = cur.getSymbol() + " " + myFormat.format(Math.round((float)creditAmount / numOfMonths));
            avgDailyExpense = cur.getSymbol() + " " + myFormat.format(Math.round((float)debitAmount / numOfDays));
            avgWeeklyExpense = cur.getSymbol() + " " + myFormat.format(Math.round((float)debitAmount / numOfWeeks));
            avgMonthlyExpense = cur.getSymbol() + " " + myFormat.format(Math.round((float)debitAmount / numOfMonths));

        }

        avgDailyIncomeTextView.setText(avgDailyIncome);
        avgWeeklyIncomeTextView.setText(avgWeeklyIncome);
        avgMonthlyIncomeTextView.setText(avgMonthlyIncome);
        avgDailyExpenseTextView.setText(avgDailyExpense);
        avgWeeklyExpenseTextView.setText(avgWeeklyExpense);
        avgMonthlyExpenseTextView.setText(avgMonthlyExpense);

    }

    private void setDateRangeButton() throws ParseException {
        materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT DATE RANGE");
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        List<CalendarConstraints.DateValidator> validators= new ArrayList<>();
        try {
            validators.add(DateValidatorPointForward.from(getDateLongCustom(minDate,dateFormat)));
            validators.add(DateValidatorPointBackward.before(getDateLongCustom(currentDate,dateFormat)));
            constraintsBuilderRange.setValidator(CompositeDateValidator.allOf(validators));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        materialDateBuilder.setCalendarConstraints(constraintsBuilderRange.build());
        materialDatePicker = materialDateBuilder.build();
        String dateString = getDateString(getDateLongCustom(minDate,dateFormat)) + " - " + getDateString(currentDate,dateFormat);
        dateRangeButton.setText(dateString);
    }
}