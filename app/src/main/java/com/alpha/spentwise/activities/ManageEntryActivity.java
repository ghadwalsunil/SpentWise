package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.ContactDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.alpha.spentwise.utils.DatePickerFragment;
import com.alpha.spentwise.customDataObjects.EntryDetails;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.customDataObjects.TransactionTypeDetails;
import com.alpha.spentwise.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.moneyLendingAndBorrowing;
import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateString;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class ManageEntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView showNumberOfRepeatEntries, showContactNameTextView;
    private EditText setComment;
    private TextInputEditText setAmount;
    private Button setDateButton,saveEntryButton,setRepeatPattern,changeCurrencyButton;
    private DatabaseHandler dbHandler = new DatabaseHandler(ManageEntryActivity.this);
    private EntryDetails newEntry = new EntryDetails();
    private SimpleDateFormat dateFormat = Constants.dateFormat;
    private CheckBox entryRepeatCheckBox;
    private List<Integer> repeatDaysList = new ArrayList<>();
    private int enteredAmountInt;
    private ProjectDetails selectedProjectDetails;
    private CategoryDetails selectedCategoryDetails;
    private String enteredAmount, enteredComment;
    private CurrencyDetails selectedCurrencyDetails, newCurrencyDetails;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private TransactionTypeDetails selectedTransactionTypeDetails;
    private TransactionModeDetails selectedTransactionModeDetails;
    private ContactDetails selectedContactDetails;
    private List<TransactionModeDetails> getTransactionModes;
    private List<TransactionTypeDetails> getTransactionTypes;
    private AutoCompleteTextView transactionTypeDropDown;
    private ArrayAdapter<TransactionTypeDetails> transactionTypeDetailsArrayAdapter;
    private AutoCompleteTextView transactionModeDropDown;
    private ArrayAdapter<TransactionModeDetails> transactionModeDetailsArrayAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_entry);
        progressBar = findViewById(R.id.manageEntry_progressBar);
        requestQueue = VolleySingleton.getMInstance(this).getRequestQueue();

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        selectedContactDetails = ((UserProjectDataHolder)getApplication()).getSelectedContactDetails();

        toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setAmount = findViewById(R.id.manageEntry_edt_enterAmount);
        setDateButton = findViewById(R.id.addEntry_btn_setDate);
        setComment = findViewById(R.id.addEntry_edt_addComment);
        saveEntryButton = findViewById(R.id.addEntry_btn_Submit);
        entryRepeatCheckBox = findViewById(R.id.manageEntry_chkbx_repeat);
        setRepeatPattern = findViewById(R.id.manageEntry_btn_repeat);
        showNumberOfRepeatEntries = findViewById(R.id.manageEntry_tv_numOfRepeatEntries);
        changeCurrencyButton = findViewById(R.id.manageEntry_btn_changeCurrency);
        transactionTypeDropDown = findViewById(R.id.manageEntry_atv_transactionType);
        transactionModeDropDown = findViewById(R.id.manageEntry_atv_transactionMode);
        showContactNameTextView = findViewById(R.id.manageEntry_tv_contactName);

        getTransactionModes = dbHandler.getActiveTransactionModes(selectedProjectDetails);
        getTransactionTypes = dbHandler.getTransactionTypes();

        //Calender selected date listener
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To close an open keyboard
                closeKeyboard();
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date");
            }
        });

        //Populate transaction modes
        populateActiveTransactionModes();

        //Save Selected Value for Transaction Mode
        transactionModeDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                newEntry.setEntryMode(transactionModeDetailsArrayAdapter.getItem(position).getTransactionModeID());
                selectedTransactionModeDetails = transactionModeDetailsArrayAdapter.getItem(position);
            }
        });

        //Populate transaction types
        populateTransactionTypes();

        transactionTypeDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                newEntry.setEntryType(transactionTypeDetailsArrayAdapter.getItem(position).getTransactionTypeID());
                selectedTransactionTypeDetails = transactionTypeDetailsArrayAdapter.getItem(position);
            }
        });

        //Enable repeat transaction pattern set button
        entryRepeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setRepeatPattern.setEnabled(isChecked);
                if(isChecked){
                    newEntry.setEntryRepeat(1);
                } else {
                    newEntry.setEntryRepeat(0);
                }
            }
        });

        //Set transaction repeat pattern
        setRepeatPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredAmount = setAmount.getText().toString().replaceAll("[^0-9]","");
                enteredComment = setComment.getText().toString().trim();
                if(enteredAmount.isEmpty()){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if(Integer.parseInt(enteredAmount) <= 0){
                        Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(enteredAmount) <= 0){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newEntry.getEntryDate() <= 0){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.dateNotSelectedError), Toast.LENGTH_SHORT).show();
                    return;
                }
                enteredAmountInt = Integer.parseInt(enteredAmount);
                saveEnteredDetails();
                ((UserProjectDataHolder)getApplication()).getEntryPatternDetails().setEntryPatternStartDateInt(newEntry.getEntryDate());
                if(((UserProjectDataHolder)getApplication()).getEntryPatternDetails().getEntryPatternLastDateInt() == 0){
                    ((UserProjectDataHolder)getApplication()).getEntryPatternDetails().setEntryPatternLastDateInt(newEntry.getEntryDate());
                }

                Intent intent = new Intent(ManageEntryActivity.this, EntryRepeatPatternActivity.class);
                startActivity(intent);

            }
        });

        changeCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredAmount = setAmount.getText().toString().replaceAll("[^0-9]","");
                enteredComment = setComment.getText().toString().trim();
                if(enteredAmount.isEmpty()){
                    enteredAmountInt = 0;
                } else {
                    try {
                        enteredAmountInt = Integer.parseInt(enteredAmount);
                    } catch (Exception e) {
                        e.printStackTrace();
                        enteredAmountInt = 0;
                    }
                }
                saveEnteredDetails();
                ((UserProjectDataHolder)getApplication()).setPreviousActivity("ManageEntryActivity");
                Intent intent = new Intent(ManageEntryActivity.this,SelectCurrencyActivity.class);
                startActivity(intent);
            }
        });

        loadSavedDetails();


        saveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                enteredAmount = setAmount.getText().toString().replaceAll("[^0-9]","");
                enteredComment = setComment.getText().toString().trim();
                if(enteredAmount.isEmpty()){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if(Integer.parseInt(enteredAmount) <= 0){
                        Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.invalidAmountError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newEntry.getEntryDate() <= 0){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.dateNotSelectedError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newEntry.getEntryRepeat() == 1 && repeatDaysList == null){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.repeatPatternNotSelectedError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newEntry.getEntryRepeat() == 1 && repeatDaysList.size() < 1){
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.repeatPatternNotSelectedError), Toast.LENGTH_SHORT).show();
                    return;
                }
                enteredAmountInt = Integer.parseInt(enteredAmount);
                progressBar.setVisibility(View.VISIBLE);

                if(selectedContactDetails != null){
                    if(!enteredComment.isEmpty()){
                        enteredComment = "\n" + enteredComment;
                    }
                    if(selectedCategoryDetails.getCategoryName().equalsIgnoreCase(moneyLendingAndBorrowing[0])){
                        if(selectedTransactionTypeDetails.getTransactionTypeID() == getTransactionTypes.get(1).getTransactionTypeID()){
                            enteredComment = "Borrowed Money from : " + selectedContactDetails.getContactName() + enteredComment;
                        } else {
                            enteredComment = "Borrowed Money returned to : " + selectedContactDetails.getContactName() + enteredComment;
                        }
                    } else {
                        if(selectedTransactionTypeDetails.getTransactionTypeID() == getTransactionTypes.get(0).getTransactionTypeID()){
                            enteredComment = "Lent Money to : " + selectedContactDetails.getContactName() + enteredComment;
                        } else {
                            enteredComment = "Lent Money received from : " + selectedContactDetails.getContactName() + enteredComment;
                        }
                    }
                }

                if(newCurrencyDetails != null && !newCurrencyDetails.getId().equalsIgnoreCase(selectedCurrencyDetails.getId())){
                    if(!enteredComment.isEmpty()){
                        enteredComment = enteredComment + "\n";
                    }
                    if(newCurrencyDetails.getCurrencySymbol() == null){
                        enteredComment = enteredComment + "Original Value: " + newCurrencyDetails.getId() + " " + enteredAmountInt;
                    } else {
                        enteredComment = enteredComment + "Original Value: " + newCurrencyDetails.getId() + " ( " + newCurrencyDetails.getCurrencySymbol() + " ) " + enteredAmountInt;
                    }
                    performCurrencyConversion();
                } else {
                    saveAndExit();
                }
            }
        });

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDateShowString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        setDateButton.setText(selectedDateShowString);
        int selectedDate = Integer.parseInt(dateFormat.format(c.getTime()));
        repeatDaysList = null;
        ((UserProjectDataHolder)getApplication()).getEntryDetails().setEntryDate(selectedDate);
        ((UserProjectDataHolder)getApplication()).resetEntryRepeatDateList();
        ((UserProjectDataHolder)getApplication()).resetEntryPatternDetails();
        showNumberOfRepeatEntries.setText("");
    }

    //Function to populate Active Transaction Modes in Spinner
    public void populateActiveTransactionModes(){

        transactionModeDetailsArrayAdapter = new ArrayAdapter<>(this,R.layout.util_exposed_dropdown_item_larger,getTransactionModes);
        transactionModeDropDown.setAdapter(transactionModeDetailsArrayAdapter);

    }

    //Function to populate Transaction Types in Spinner
    public void populateTransactionTypes(){

        transactionTypeDetailsArrayAdapter = new ArrayAdapter<>(this,R.layout.util_exposed_dropdown_item_larger,getTransactionTypes);
        transactionTypeDropDown.setAdapter(transactionTypeDetailsArrayAdapter);
    }

    //Method to close soft keyboard
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadSavedDetails(){

        selectedCategoryDetails = ((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails();
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        newCurrencyDetails = ((UserProjectDataHolder)getApplication()).getNewCurrencyDetails();
        toolbar.setTitle(selectedCategoryDetails.getCategoryName());

        if(newCurrencyDetails == null){
            if(selectedCurrencyDetails.getCurrencySymbol() == null){
                changeCurrencyButton.setText(selectedCurrencyDetails.getId());
            } else {
                String currencyText = selectedCurrencyDetails.getId() + " ( " + selectedCurrencyDetails.getCurrencySymbol() + " )";
                changeCurrencyButton.setText(currencyText);
            }
        } else {
            if(newCurrencyDetails.getCurrencySymbol() == null){
                changeCurrencyButton.setText(newCurrencyDetails.getId());
            } else {
                String currencyText = newCurrencyDetails.getId() + " ( " + newCurrencyDetails.getCurrencySymbol() + " )";
                changeCurrencyButton.setText(currencyText);
            }
        }

        newEntry = ((UserProjectDataHolder)getApplication()).getEntryDetails();
        setAmount.setText(String.valueOf(newEntry.getEntryAmount()));

        if(newEntry.getEntryDate() != -1){
            try {
                setDateButton.setText(getDateString(newEntry.getEntryDate(),dateFormat));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            setDateButton.setText(getResources().getString(R.string.addEntry_btn_setDateLabel));
        }

        if(newEntry.getEntryMode() == -1){
            transactionModeDropDown.setText(getTransactionModes.get(0).toString(),false);
            selectedTransactionModeDetails = transactionModeDetailsArrayAdapter.getItem(0);
            newEntry.setEntryMode(selectedTransactionModeDetails.getTransactionModeID());
        } else {
            selectedTransactionModeDetails = ((UserProjectDataHolder)getApplication()).getTransactionModeDetails();
            transactionModeDropDown.setText(selectedTransactionModeDetails.toString(),false);
        }

        if(newEntry.getEntryType() == -1){
            transactionTypeDropDown.setText(getTransactionTypes.get(0).toString(),false);
            selectedTransactionTypeDetails = transactionTypeDetailsArrayAdapter.getItem(0);
            newEntry.setEntryType(selectedTransactionTypeDetails.getTransactionTypeID());
        } else {
            selectedTransactionTypeDetails = ((UserProjectDataHolder)getApplication()).getTransactionTypeDetails();
            transactionTypeDropDown.setText(selectedTransactionTypeDetails.toString(),false);
        }

        if(newEntry.getEntryRepeat() == 0) {
            entryRepeatCheckBox.setChecked(false);
            setRepeatPattern.setEnabled(false);
        }
        else {
            entryRepeatCheckBox.setChecked(true);
            setRepeatPattern.setEnabled(true);
        }

        repeatDaysList = ((UserProjectDataHolder)getApplication()).getEntryRepeatDateList();

        if(repeatDaysList == null){
            showNumberOfRepeatEntries.setText("");
        }
        else if(repeatDaysList.size() == 0)
            showNumberOfRepeatEntries.setText("");
        else{
            String showString = repeatDaysList.size() + " " + getResources().getString(R.string.newEntryRepeatCountMessage);
            showNumberOfRepeatEntries.setText(showString);
        }
        if(selectedContactDetails == null){
            showContactNameTextView.setText("");
        }
        else
            showContactNameTextView.setText("Contact Name : " + selectedContactDetails.getContactName());
        setComment.setText(newEntry.getEntryComment());

    }

    private void saveEnteredDetails(){

        newEntry.setEntryNumber(-1);
        newEntry.setEntryCategoryID(selectedCategoryDetails.getCategoryID());
        newEntry.setEntryAmount(enteredAmountInt);
        //Entry Date is already handled in date picker
        newEntry.setEntryMode(selectedTransactionModeDetails.getTransactionModeID());
        newEntry.setEntryType(selectedTransactionTypeDetails.getTransactionTypeID());

        if(entryRepeatCheckBox.isChecked())
            newEntry.setEntryRepeat(1);
        else
            newEntry.setEntryRepeat(0);
        ((UserProjectDataHolder)getApplication()).setEntryRepeatDateList(repeatDaysList);
        newEntry.setEntryComment(enteredComment);
        if(newEntry.getEntryDate() > getDateIntCustom(Calendar.getInstance().getTime().getTime(),dateFormat)){
            newEntry.setEntryStatus(2);
        } else {
            newEntry.setEntryStatus(1);
        }
        ((UserProjectDataHolder)getApplication()).setEntryDetails(newEntry);
        ((UserProjectDataHolder)getApplication()).setTransactionModeDetails(selectedTransactionModeDetails);
        ((UserProjectDataHolder)getApplication()).setTransactionTypeDetails(selectedTransactionTypeDetails);
    }

    private void performCurrencyConversion() {

        String currencyPartOfURL = newCurrencyDetails.getId() + "_" + selectedCurrencyDetails.getId();
        String url = Constants.getConversionRateURLLeft + currencyPartOfURL + Constants.getConversionRateURLRight;
        final Double[] conversionRate = {1d};

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String responseString = String.valueOf(response);

                Pattern regexPattern = Pattern.compile("\\{\\\"" + currencyPartOfURL + "\\\":\\{\\\"val\\\":(.*?)\\}\\}");
                Matcher matcher = regexPattern.matcher(responseString);

                if(matcher.find()){
                    conversionRate[0] = Double.parseDouble(matcher.group(1));
                    Double convertedAmountDouble = enteredAmountInt * conversionRate[0];
                    if(convertedAmountDouble > Integer.MAX_VALUE || convertedAmountDouble <= 0){
                        enteredAmountInt = 0;
                    } else {
                        enteredAmountInt = (int) Math.round(convertedAmountDouble);
                    }
                    if(enteredAmountInt <= 0){
                        Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.conversionError), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        saveAndExit();
                    }
                } else {
                    Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.conversionError), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.conversionError), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void saveAndExit(){
        saveEnteredDetails();

        boolean entryRepeatStatus = false;
        boolean entryStatus = false;
        if(newEntry.getEntryRepeat() == 0){
            entryStatus = dbHandler.addNewEntry(newEntry,selectedProjectDetails);
        } else {
            entryRepeatStatus = dbHandler.addRepeatEntry(newEntry, repeatDaysList, selectedProjectDetails, getDateIntCustom(Calendar.getInstance().getTime().getTime(),dateFormat));
        }
        if(entryStatus || entryRepeatStatus){
            Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.entryAdditionConfirmationSuccess), Toast.LENGTH_SHORT).show();
            resetAndExit();
        }
        else {
            Toast.makeText(ManageEntryActivity.this, getResources().getString(R.string.entryAdditionConfirmationError), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void resetAndExit(){
        Intent intent = new Intent(ManageEntryActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((UserProjectDataHolder)getApplication()).resetEntryDetails();
        ((UserProjectDataHolder)getApplication()).resetEntryPatternDetails();
        ((UserProjectDataHolder)getApplication()).resetEntryRepeatDateList();
        ((UserProjectDataHolder)getApplication()).resetSelectedCategoryDetails();
        ((UserProjectDataHolder)getApplication()).resetTransactionModeDetails();
        ((UserProjectDataHolder)getApplication()).resetTransactionTypeDetails();
        ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
        ((UserProjectDataHolder)getApplication()).resetSelectedContactDetails();
        startActivity(intent);
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