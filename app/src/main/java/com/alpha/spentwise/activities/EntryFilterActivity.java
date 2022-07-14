package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.AppliedFilterDetails;
import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.customDataObjects.FilterDetails;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.customDataObjects.TransactionTypeDetails;
import com.alpha.spentwise.adapters.CategoryFilterRecyclerViewAdapter;
import com.alpha.spentwise.adapters.TransactionModeFilterRecyclerViewAdapter;
import com.alpha.spentwise.adapters.TransactionTypeFilterRecyclerViewAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateString;
import static com.alpha.spentwise.utils.CustomFunctions.getNextDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class EntryFilterActivity extends AppCompatActivity implements CategoryFilterRecyclerViewAdapter.OnNoteListenerCategory, TransactionModeFilterRecyclerViewAdapter.OnNoteListener, TransactionTypeFilterRecyclerViewAdapter.OnNoteListener {

    private RecyclerView categoryFilterAttributes,transactionModeFilterAttributes,transactionTypeFilterAttributes;
    private DatabaseHandler dbHandler = new DatabaseHandler(EntryFilterActivity.this);
    private List<CategoryDetails> entryCategoryList = new ArrayList<>();
    private List<TransactionModeDetails> entryTransactionModeList = new ArrayList<>();
    private List<TransactionTypeDetails> entryTransactionTypeList = new ArrayList<>();
    private FilterDetails filterDetails = new FilterDetails(entryCategoryList,entryTransactionModeList,entryTransactionTypeList,0,0,0,0);
    private CategoryFilterRecyclerViewAdapter categoryFilterRecyclerViewAdapter;
    private TransactionModeFilterRecyclerViewAdapter transactionModeFilterRecyclerViewAdapter;
    private TransactionTypeFilterRecyclerViewAdapter transactionTypeFilterRecyclerViewAdapter;
    private Button applyFilterButton, clearFilterButton, updateRangeButton, dateRangeButton;
    private RangeSlider amountRangeSlider;
    private TextInputEditText amountFrom, amountTo;
    private NumberFormat myFormat = NumberFormat.getInstance(Locale.ENGLISH);
    private Currency cur;
    private AppliedFilterDetails appliedFilterDetails;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private StaggeredGridLayoutManager gridLayoutManagerCategory,gridLayoutManagerTransactionMode,gridLayoutManagerTransactionType;
    private int[] categoryItemPositionStatus,transactionModeItemPositionStatus,transactionTypeItemPositionStatus;
    private ArrayList<Integer> selectedFilterCategoryID, selectedFilterTransactionModeID, selectedFilterTransactionTypeID;
    private int[] selectedAmountRange = {-1,-1};
    private int[] selectedDateRange = {-1,-1};
    private ProjectDetails selectedProjectDetails;
    private CurrencyDetails selectedCurrencyDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_filter);
        categoryFilterAttributes = findViewById(R.id.entryFilter_rv_category);
        transactionModeFilterAttributes = findViewById(R.id.entryFilter_rv_transactionMode);
        transactionTypeFilterAttributes = findViewById(R.id.entryFilter_rv_transactionType);
        applyFilterButton = findViewById(R.id.entryFilter_btn_applyFilter);
        clearFilterButton = findViewById(R.id.entryFilter_btn_clearFilter);
        amountRangeSlider = findViewById(R.id.entryFilter_sld_amount);
        amountFrom = findViewById(R.id.entryFilter_edt_amountFrom);
        amountTo = findViewById(R.id.entryFilter_edt_amountTo);
        updateRangeButton = findViewById(R.id.entryFilter_btn_updateRange);
        dateRangeButton = findViewById(R.id.entryFilter_btn_setDateRange);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.entryFilterActivityTitle);

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        cur = Currency.getInstance(selectedCurrencyDetails.getId());

        try {
            loadSavedDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Set category recycler view adapter
        categoryFilterRecyclerViewAdapter = new CategoryFilterRecyclerViewAdapter(this,filterDetails,categoryItemPositionStatus,this);
        gridLayoutManagerCategory = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        categoryFilterAttributes.setLayoutManager(gridLayoutManagerCategory);
        categoryFilterAttributes.setAdapter(categoryFilterRecyclerViewAdapter);

        //Set transaction mode recycler view adapter
        transactionModeFilterRecyclerViewAdapter = new TransactionModeFilterRecyclerViewAdapter(this,filterDetails,transactionModeItemPositionStatus,this);
        gridLayoutManagerTransactionMode = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        transactionModeFilterAttributes.setLayoutManager(gridLayoutManagerTransactionMode);
        transactionModeFilterAttributes.setAdapter(transactionModeFilterRecyclerViewAdapter);

        //Set transaction mode recycler view adapter
        transactionTypeFilterRecyclerViewAdapter = new TransactionTypeFilterRecyclerViewAdapter(this,filterDetails,transactionTypeItemPositionStatus,this);
        gridLayoutManagerTransactionType = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        transactionTypeFilterAttributes.setLayoutManager(gridLayoutManagerTransactionType);
        transactionTypeFilterAttributes.setAdapter(transactionTypeFilterRecyclerViewAdapter);

        //Format the label with currency value
        amountRangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @NotNull
            @Override
            public String getFormattedValue(float value) {
                return cur.getSymbol() + " " + myFormat.format(value);
            }
        });

        //Get values from the amount range slider
        amountRangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull @NotNull RangeSlider slider, float value, boolean fromUser) {

                List<Float> selectedAmountRangeList = slider.getValues();
                selectedAmountRange[0] = selectedAmountRangeList.get(0).intValue();
                selectedAmountRange[1] = selectedAmountRangeList.get(1).intValue();

                amountFrom.setText(String.valueOf(selectedAmountRange[0]));
                amountTo.setText(String.valueOf(selectedAmountRange[1]));
            }
        });

        //Update range based on entered value
        updateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int invalidValueFlag = 0;
                //If the from value is blank, set min amount value
                if(amountFrom.getText().toString().equalsIgnoreCase("")) {
                    selectedAmountRange[0] = filterDetails.getMinAmount();
                    invalidValueFlag = 1;
                }
                else
                    selectedAmountRange[0] = Integer.parseInt(amountFrom.getText().toString());
                //If the to value is blank, set max amount value
                if(amountTo.getText().toString().equalsIgnoreCase("")) {
                    selectedAmountRange[1] = filterDetails.getMaxAmount();
                    invalidValueFlag = 1;
                }
                else
                    selectedAmountRange[1] = Integer.parseInt(amountTo.getText().toString());

                //If the to value is less than from value, reset the respective value
                if(selectedAmountRange[0] > selectedAmountRange[1]){
                    selectedAmountRange[0] = filterDetails.getMinAmount();
                    selectedAmountRange[1] = filterDetails.getMaxAmount();
                    invalidValueFlag = 1;
                }

                //If the to and from values are greater than min and max values, reset them
                if(selectedAmountRange[0] > filterDetails.getMaxAmount() || selectedAmountRange[0] < filterDetails.getMinAmount()) {
                    selectedAmountRange[0] = filterDetails.getMinAmount();
                    invalidValueFlag = 1;
                }
                if(selectedAmountRange[1] > filterDetails.getMaxAmount() || selectedAmountRange[1] < filterDetails.getMinAmount()) {
                    selectedAmountRange[1] = filterDetails.getMaxAmount();
                    invalidValueFlag = 1;
                }

                if(invalidValueFlag == 1)
                    Toast.makeText(EntryFilterActivity.this, "Invalid value range. Reset to Default.", Toast.LENGTH_SHORT).show();

                amountFrom.setText(String.valueOf(selectedAmountRange[0]));
                amountTo.setText(String.valueOf(selectedAmountRange[1]));
                amountRangeSlider.setValues((float) selectedAmountRange[0] , (float) selectedAmountRange[1]);
                closeKeyboard();
            }
        });

        //Setup Date range picker
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT DATE RANGE");
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        List<CalendarConstraints.DateValidator> validators= new ArrayList<>();
        try {
            validators.add(DateValidatorPointForward.from(getDateLongCustom(filterDetails.getMinDate(),dateFormat)));
            validators.add(DateValidatorPointBackward.before(getNextDateLongCustom(filterDetails.getMaxDate(),dateFormat)));
            constraintsBuilderRange.setValidator(CompositeDateValidator.allOf(validators));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        materialDateBuilder.setCalendarConstraints(constraintsBuilderRange.build());
        MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        //Open date picker on click of date range button
        dateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                Long startDate = selection.first;
                Long endDate = selection.second;
                selectedDateRange[0] = Integer.parseInt(dateFormat.format(startDate));
                selectedDateRange[1] = Integer.parseInt(dateFormat.format(endDate));
                String dateString = getDateString(startDate) + " - " + getDateString(endDate);
                dateRangeButton.setText(dateString);
            }
        });

        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedDetails();

                Intent intent = new Intent(EntryFilterActivity.this, SearchEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((UserProjectDataHolder)getApplication()).resetAppliedFilterDetails();

                Intent intent = new Intent(EntryFilterActivity.this,SearchEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onNoteClick(int position) {
    }

    private boolean atLeastTwoItemSelected(int[] itemSelectedPositionList){
        int sum = 0;
        for(int i = 0; i < itemSelectedPositionList.length; i++){
            sum = sum + itemSelectedPositionList[i];
            if(sum > 1)
                return true;
        }
        return false;
    }

    private static int[] resetFilter(int[] itemPositionStatus){

        itemPositionStatus[0] = 1;
        for(int i=1; i < itemPositionStatus.length; i++){
            itemPositionStatus[i] = 0;
        }

        return itemPositionStatus;

    }

    private static int[] setAllOne(int[] itemPositionStatus){

        for(int i=0; i < itemPositionStatus.length; i++){
            itemPositionStatus[i] = 1;
        }

        return itemPositionStatus;

    }

    private static int[] getCategoryPositionStatus(int[] itemPositionStatus, List<CategoryDetails> categoryDetails, List<Integer> selectedFilterCategoryID){

        for(int i = 1; i < categoryDetails.size(); i++){
            if(selectedFilterCategoryID.contains(categoryDetails.get(i).getCategoryID()))
                itemPositionStatus[i] = 1;
        }
        return itemPositionStatus;
    }

    private static int[] getTransactionModePositionStatus(int[] itemPositionStatus, List<TransactionModeDetails> transactionModeDetails, List<Integer> selectedFilterTransactionModeID){

        for(int i = 0; i < transactionModeDetails.size(); i++){
            if(selectedFilterTransactionModeID.contains(transactionModeDetails.get(i).getTransactionModeID()))
                itemPositionStatus[i] = 1;
        }
        return itemPositionStatus;
    }

    private static int[] getTransactionTypePositionStatus(int[] itemPositionStatus, List<TransactionTypeDetails> transactionTypeDetails, List<Integer> selectedFilterTransactionTypeID){

        for(int i = 0; i < transactionTypeDetails.size(); i++){
            if(selectedFilterTransactionTypeID.contains(transactionTypeDetails.get(i).getTransactionTypeID()))
                itemPositionStatus[i] = 1;
        }
        return itemPositionStatus;
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadSavedDetails() throws ParseException {
        appliedFilterDetails = ((UserProjectDataHolder)getApplication()).getAppliedFilterDetails();

        //Get attributes for filter
        filterDetails = dbHandler.getFilterAttributes(selectedProjectDetails);

        //Add All option for the Category filter
        entryCategoryList = filterDetails.getCategoryDetails();
        entryCategoryList.add(0,new CategoryDetails(-1,"ALL",1,"NA",0));
        filterDetails.setCategoryDetails(entryCategoryList);
        categoryItemPositionStatus = new int[entryCategoryList.size()];
        if(appliedFilterDetails.getFilterCategories().get(0) == -1)
            categoryItemPositionStatus = resetFilter(categoryItemPositionStatus);
        else
            categoryItemPositionStatus = getCategoryPositionStatus(categoryItemPositionStatus,entryCategoryList,appliedFilterDetails.getFilterCategories());

        //Add All option for the Transaction Mode filter
        entryTransactionModeList = filterDetails.getTransactionModeDetails();
        if(entryTransactionModeList.size() == 0){
            transactionModeItemPositionStatus = new int[1];
        } else {
            transactionModeItemPositionStatus = new int[entryTransactionModeList.size()];
            if (appliedFilterDetails.getFilterTransactionModes().get(0) == -1)
                transactionModeItemPositionStatus = setAllOne(transactionModeItemPositionStatus);
            else
                transactionModeItemPositionStatus = getTransactionModePositionStatus(transactionModeItemPositionStatus, entryTransactionModeList, appliedFilterDetails.getFilterTransactionModes());
        }

        //Add All option for the Transaction Mode filter
        entryTransactionTypeList = filterDetails.getTransactionTypeDetails();
        if(entryTransactionTypeList.size() == 0){
            transactionTypeItemPositionStatus = new int[1];
        } else {
            transactionTypeItemPositionStatus = new int[entryTransactionTypeList.size()];
            if (appliedFilterDetails.getFilterTransactionTypes().get(0) == -1)
                transactionTypeItemPositionStatus = setAllOne(transactionTypeItemPositionStatus);
            else
                transactionTypeItemPositionStatus = getTransactionTypePositionStatus(transactionTypeItemPositionStatus, entryTransactionTypeList, appliedFilterDetails.getFilterTransactionTypes());
        }

        //Amount Range slider setup
        if(filterDetails.getMinAmount() == filterDetails.getMaxAmount()){
            filterDetails.setMaxAmount(filterDetails.getMinAmount() + 1000);
        }
        selectedAmountRange = appliedFilterDetails.getFilterAmountRange();
        if(selectedAmountRange[0] <= 0 || selectedAmountRange[0] <= filterDetails.getMinAmount()){
            selectedAmountRange[0] = filterDetails.getMinAmount();
        }
        if(selectedAmountRange[1] <= 0 || selectedAmountRange[1] >= filterDetails.getMaxAmount()){
            selectedAmountRange[1] = filterDetails.getMaxAmount();
        }

        amountRangeSlider.setValueFrom(filterDetails.getMinAmount());
        amountRangeSlider.setValueTo(filterDetails.getMaxAmount());
        amountFrom.setText(String.valueOf(selectedAmountRange[0]));
        amountTo.setText(String.valueOf(selectedAmountRange[1]));
        amountRangeSlider.setValues((float) selectedAmountRange[0] , (float) selectedAmountRange[1]);

        //Set text for initial date range button value
        selectedDateRange = appliedFilterDetails.getFilterDateRange();
        if(selectedDateRange[0] <= 0 || selectedDateRange[1] <= 0){
            if(filterDetails.getMinDate() <= 0){
                selectedDateRange[0] = getDateIntCustom(System.currentTimeMillis(),dateFormat);
                selectedDateRange[1] = getDateIntCustom(System.currentTimeMillis(),dateFormat);
            } else {
                selectedDateRange[0] = filterDetails.getMinDate();
                selectedDateRange[1] = filterDetails.getMaxDate();
            }
        }
        String dateString = getDateString(selectedDateRange[0],dateFormat) + " - " + getDateString(selectedDateRange[1],dateFormat);
        dateRangeButton.setText(dateString);


    }

    private void saveSelectedDetails(){
        selectedFilterCategoryID = new ArrayList<>();
        selectedFilterTransactionModeID = new ArrayList<>();
        selectedFilterTransactionTypeID = new ArrayList<>();

        //Add selected categories to list
        if(categoryItemPositionStatus[0] == 1){
            selectedFilterCategoryID.add(-1);
        }
        else{
            for(int i = 1; i < categoryItemPositionStatus.length; i++){
                if(categoryItemPositionStatus[i] == 1)
                    selectedFilterCategoryID.add(filterDetails.getCategoryDetails().get(i).getCategoryID());
            }
        }

        //Add selected transaction modes to list
        for(int i = 0; i < transactionModeItemPositionStatus.length; i++){
            if(transactionModeItemPositionStatus[i] == 1)
                selectedFilterTransactionModeID.add(filterDetails.getTransactionModeDetails().get(i).getTransactionModeID());
        }

        //Add selected transaction types to list
        for(int i = 0; i < transactionTypeItemPositionStatus.length; i++){
            if(transactionTypeItemPositionStatus[i] == 1)
                selectedFilterTransactionTypeID.add(filterDetails.getTransactionTypeDetails().get(i).getTransactionTypeID());
        }

        //SelectedDateRange and SelectedAmountRange have already been handled in their respective listeners

        appliedFilterDetails = new AppliedFilterDetails(selectedFilterCategoryID,selectedFilterTransactionModeID,selectedFilterTransactionTypeID,selectedDateRange,selectedAmountRange,1);
        ((UserProjectDataHolder)getApplication()).setAppliedFilterDetails(appliedFilterDetails);

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