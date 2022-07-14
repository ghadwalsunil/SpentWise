package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.AppliedFilterDetails;
import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.customDataObjects.EntryDetails;
import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.SearchEntryRecyclerViewAdapter;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.alpha.spentwise.dataManager.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class SearchEntryActivity extends AppCompatActivity implements SearchEntryRecyclerViewAdapter.OnNoteListener {

    private RecyclerView searchEntryRecyclerView;
    private List<EntryDetails> activeEntryList;
    private List<CategoryDetails> allCategories;
    private List<TransactionModeDetails> allTransactionModes;
    private DatabaseHandler dbHandler = new DatabaseHandler(SearchEntryActivity.this);
    private SearchEntryRecyclerViewAdapter searchEntryRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private AutoCompleteTextView sortByAutoCompleteTv, sortOrderAutoCompleteTv;
    private String[] sortByOptions, sortOrderOptions;
    private String[] sortCombination = {"",""};
    private ArrayAdapter sortDropdownAdapter;
    private AlertDialog.Builder deleteConfirmationPopupBuilder;
    private AlertDialog deleteConfirmationPopup;
    private Currency cur;
    private FloatingActionButton filterButton;
    private BottomNavigationView bottomNavigationView;
    private ProjectDetails selectedProjectDetails;
    private AppliedFilterDetails appliedFilterDetails;
    private CurrencyDetails selectedCurrencyDetails;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_entry);

        searchEntryRecyclerView = findViewById(R.id.searchEntry_rv_searchResult);
        sortByAutoCompleteTv = findViewById(R.id.searchEntry_autoCompleteTv_sortBy);
        sortByOptions = getResources().getStringArray(R.array.sortByOptionsArray);
        sortOrderAutoCompleteTv = findViewById(R.id.searchEntry_autoCompleteTv_sortOrder);
        sortOrderOptions = getResources().getStringArray(R.array.sortOrderOptionsArray);
        filterButton = findViewById(R.id.searchEntry_filterFloatingActionButton);
        bottomNavigationView = findViewById(R.id.searchEntry_bottomNavBar);

        //Toolbar Setup
        Toolbar toolbar=findViewById(R.id.toolbar_primary);
        setSupportActionBar(toolbar);
        titleTextView = findViewById(R.id.toolbar_primary_title);
        titleTextView.setText(R.string.searchEntryActivityTitle);
        ImageView toolbarNotesIconImageView = findViewById(R.id.toolbar_primary_notes);
        toolbarNotesIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NotesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        appliedFilterDetails = ((UserProjectDataHolder)getApplication()).getAppliedFilterDetails();
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        if(selectedCurrencyDetails != null){
            cur = Currency.getInstance(selectedCurrencyDetails.getId());
        }


        //Setup the sort by dropdown adapter
        sortDropdownAdapter = new ArrayAdapter(this,R.layout.util_exposed_dropdown_item,sortByOptions);
        //Set default value for dropdown
        //sortByAutoCompleteTv.setText(sortDropdownAdapter.getItem(0).toString(),false);
        sortByAutoCompleteTv.setAdapter(sortDropdownAdapter);
        sortByAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortCombination[0] = sortByAutoCompleteTv.getText().toString();
                sortCombination[1] = sortOrderAutoCompleteTv.getText().toString();
                activeEntryList = sortSearchResults(activeEntryList,sortCombination,sortByOptions,sortOrderOptions);
                searchEntryRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Setup the sort order dropdown adapter
        sortDropdownAdapter = new ArrayAdapter(this,R.layout.util_exposed_dropdown_item,sortOrderOptions);
        //Set default value for dropdown
        sortOrderAutoCompleteTv.setText(sortDropdownAdapter.getItem(0).toString(),false);
        sortOrderAutoCompleteTv.setAdapter(sortDropdownAdapter);
        sortOrderAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortCombination[0] = sortByAutoCompleteTv.getText().toString();
                sortCombination[1] = sortOrderAutoCompleteTv.getText().toString();
                activeEntryList = sortSearchResults(activeEntryList,sortCombination,sortByOptions,sortOrderOptions);
                searchEntryRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Get the list of all active entries
        activeEntryList = dbHandler.getActiveEntries(selectedProjectDetails);
        //Get the list of all categories and transaction modes
        allCategories = dbHandler.getAllCategories(selectedProjectDetails);
        allTransactionModes = dbHandler.getAllTransactionModes(selectedProjectDetails);

        //Update Entry list based on filter
        activeEntryList = filterSearchResults(appliedFilterDetails, activeEntryList);

        //Setup the adapter to show search results
        searchEntryRecyclerViewAdapter = new SearchEntryRecyclerViewAdapter(this,activeEntryList,allCategories,allTransactionModes,this,selectedCurrencyDetails,this);
        linearLayoutManager = new LinearLayoutManager(this);
        searchEntryRecyclerView.setLayoutManager(linearLayoutManager);
        searchEntryRecyclerView.setAdapter(searchEntryRecyclerViewAdapter);

        //On click listener for the floating filter button
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchEntryActivity.this, EntryFilterActivity.class);
                startActivity(intent);
            }
        });

        //Setting up the bottom navigation bar
        bottomNavigationView.setSelectedItemId(R.id.main_navBar_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                if(item.getItemId() == R.id.main_navBar_home){
                    Intent intent = new Intent(SearchEntryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.main_navBar_add){
                    Intent intent = new Intent(SearchEntryActivity.this, SelectCategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if(item.getItemId() == R.id.main_navBar_search){
                    return true;
                }
                return false;
            }
        });

    }

    public List<EntryDetails> sortSearchResults(List<EntryDetails> entryList, String[] sortCombination, String[] sortByOptions, String[] sortOrderOptions){

        Comparator<EntryDetails> com = (o1, o2) -> 0;

        if(sortCombination[0].equalsIgnoreCase(sortByOptions[0])){
            com = (o1, o2) -> {
                if(sortCombination[1].equalsIgnoreCase(sortOrderOptions[0]))
                    return o2.getEntryAmount() - o1.getEntryAmount();
                return o1.getEntryAmount() - o2.getEntryAmount();
            };
        } else if(sortCombination[0].equalsIgnoreCase(sortByOptions[1])){
            com = (o1, o2) -> {
                if(sortCombination[1].equalsIgnoreCase(sortOrderOptions[0]))
                    return o2.getEntryDate() - o1.getEntryDate();
                return o1.getEntryDate() - o2.getEntryDate();
            };
        }

        Collections.sort(entryList, com);

        return entryList;

    }

    @Override
    public void onNoteClick(int position) {
        EntryDetails clickedEntry = activeEntryList.get(position);
        deleteConfirmationPopupBuilder = new AlertDialog.Builder(SearchEntryActivity.this);
        String messageString = "";
        if(cur != null){
            messageString = getResources().getString(R.string.searchEntry_deleteConfirmationMessage) + "\n" + cur.getSymbol() + " " + Constants.myFormat.format(clickedEntry.getEntryAmount());
        }
        deleteConfirmationPopupBuilder.setMessage(messageString)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleteStatus = dbHandler.deleteEntry(clickedEntry);
                        if(deleteStatus)
                            Toast.makeText(SearchEntryActivity.this, getResources().getString(R.string.searchEntry_deleteConfirmationSuccess), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SearchEntryActivity.this, getResources().getString(R.string.searchEntry_deleteConfirmationSuccess), Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        deleteConfirmationPopup = deleteConfirmationPopupBuilder.create();
        deleteConfirmationPopup.show();

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

    private static List<EntryDetails> filterSearchResults(AppliedFilterDetails appliedFilterDetails, List<EntryDetails> entryDetailsList){

        List<EntryDetails> containsList = new ArrayList<>();
        ArrayList<Integer> selectedFilterCategoryID = appliedFilterDetails.getFilterCategories();
        ArrayList<Integer> selectedFilterTransactionModeID = appliedFilterDetails.getFilterTransactionModes();
        ArrayList<Integer> selectedFilterTransactionTypeID = appliedFilterDetails.getFilterTransactionTypes();
        int[] selectedAmountRange = appliedFilterDetails.getFilterAmountRange();
        int[] selectedDateRange = appliedFilterDetails.getFilterDateRange();

        if(appliedFilterDetails.getFilterStatus() == 1) {
            for (int i = 0; i < entryDetailsList.size(); i++) {
                if (selectedFilterCategoryID.get(0) != -1) {
                    if (!selectedFilterCategoryID.contains(entryDetailsList.get(i).getEntryCategoryID())) {
                        continue;
                    }
                }

                if (selectedFilterTransactionModeID.get(0) != -1) {
                    if (!selectedFilterTransactionModeID.contains(entryDetailsList.get(i).getEntryMode())) {
                        continue;
                    }
                }

                if (selectedFilterTransactionTypeID.get(0) != -1) {
                    if (!selectedFilterTransactionTypeID.contains(entryDetailsList.get(i).getEntryType())) {
                        continue;
                    }
                }

                if (selectedAmountRange[0] > 0 && selectedAmountRange[1] > 0) {
                    int amount = entryDetailsList.get(i).getEntryAmount();
                    if (amount < selectedAmountRange[0] || amount > selectedAmountRange[1]) {
                        continue;
                    }
                }

                if (selectedDateRange[0] > 0 && selectedDateRange[1] > 0) {
                    int date = entryDetailsList.get(i).getEntryDate();
                    if (date < selectedDateRange[0] || date > selectedDateRange[1]) {
                        continue;
                    }
                }

                containsList.add(entryDetailsList.get(i));
            }
            return containsList;
        }
        return  entryDetailsList;
    }

}