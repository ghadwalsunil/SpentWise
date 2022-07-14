package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.SelectCategoryRecyclerViewAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.READ_CONTACT_PERMISSION_REQUEST;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class SelectCategoryActivity extends AppCompatActivity implements SelectCategoryRecyclerViewAdapter.OnNoteListener {

    private DatabaseHandler dbHandler = new DatabaseHandler(SelectCategoryActivity.this);
    private RecyclerView categoryRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SelectCategoryRecyclerViewAdapter categoryAdapter;
    private List<CategoryDetails> getCategories = new ArrayList<>();
    private List<CategoryDetails> getSpecialCategories = new ArrayList<>();
    private CategoryDetails selectedCategory = new CategoryDetails(-1, "", 1, "ic_baseline_category_24",0);
    private Button moneyLentCategory, moneyBorrowedCategory;
    private AlertDialog.Builder deleteConfirmationPopupBuilder;
    private AlertDialog deleteConfirmationPopup;
    private BottomNavigationView bottomNavigationView;
    private ProjectDetails selectedProjectDetails;
    private int selectCategoryPosition = -1, previouslySelectedCategoryPosition = -1;
    private FloatingActionButton addCategoryFloatButton, updateCategory, deleteCategory;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        updateCategory = findViewById(R.id.selectCategory_floatbtn_updateCategory);
        deleteCategory = findViewById(R.id.selectCategory_floatbtn_deleteCategory);
        bottomNavigationView = findViewById(R.id.selectCategory_bottomNavBar);
        moneyLentCategory = findViewById(R.id.selectCategory_btn_moneyLentCategory);
        moneyBorrowedCategory = findViewById(R.id.selectCategory_btn_moneyBorrowedCategory);
        addCategoryFloatButton = findViewById(R.id.selectCategory_floatbtn_addCategory);

        //Toolbar Setup
        Toolbar toolbar=findViewById(R.id.toolbar_primary);
        setSupportActionBar(toolbar);
        titleTextView = findViewById(R.id.toolbar_primary_title);
        titleTextView.setText(R.string.selectCategoryActivityTitle);
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
        selectedCategory = ((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails();

        //Recycler View configuration
        categoryRecyclerView = findViewById(R.id.selectCategory_rv_categoryGrid);
        layoutManager = new GridLayoutManager(this,3);
        categoryRecyclerView.setLayoutManager(layoutManager);
        //Get categories to be displayed from the database
        getCategories = dbHandler.getActiveCategories(selectedProjectDetails);
        getSpecialCategories = dbHandler.getSpecialCategories(selectedProjectDetails);
        //Set the recycler view with the received categories
        categoryAdapter = new SelectCategoryRecyclerViewAdapter(getCategories, this, this,selectCategoryPosition);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setHasFixedSize(true);

        //Set up add new category functionality
        addCategoryFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, CategoryManageActivity.class);
                ((UserProjectDataHolder)getApplication()).resetSelectedCategoryDetails();
                startActivity(intent);
            }
        });

        //Set up update category functionality
        updateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, CategoryManageActivity.class);
                ((UserProjectDataHolder)getApplication()).setSelectedCategoryDetails(selectedCategory);
                startActivity(intent);
            }
        });

        //Set up delete category functionality
        deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmationPopupBuilder = new AlertDialog.Builder(SelectCategoryActivity.this);
                String popupMessage = getResources().getString(R.string.deleteCategoryPopupMessage) + "\n" + selectedCategory.getCategoryName();
                deleteConfirmationPopupBuilder.setMessage(popupMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleteStatus = dbHandler.deleteCategory(selectedCategory);
                        if(deleteStatus)
                            Toast.makeText(SelectCategoryActivity.this, getResources().getString(R.string.deleteCategoryConfirmationSuccess), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SelectCategoryActivity.this, getResources().getString(R.string.deleteCategoryConfirmationError), Toast.LENGTH_SHORT).show();
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
        });

        moneyLentCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory.setVisibility(View.INVISIBLE);
                deleteCategory.setVisibility(View.INVISIBLE);
                checkPermission(getSpecialCategories.get(1));
            }
        });

        moneyBorrowedCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory.setVisibility(View.INVISIBLE);
                deleteCategory.setVisibility(View.INVISIBLE);
                checkPermission(getSpecialCategories.get(0));
            }
        });

        //Setup bottom navigation bar
        bottomNavigationView.setSelectedItemId(R.id.main_navBar_add);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                int activeEntryCount = dbHandler.getActiveEntryCount(selectedProjectDetails);
                if(item.getItemId() == R.id.main_navBar_home){
                    Intent intent = new Intent(SelectCategoryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.main_navBar_add){
                    return true;
                } else if(item.getItemId() == R.id.main_navBar_search){
                    if(activeEntryCount > 0) {
                        ((UserProjectDataHolder)getApplication()).resetAppliedFilterDetails();
                        Intent intent = new Intent(SelectCategoryActivity.this, SearchEntryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }
                    else {
                        Toast.makeText(SelectCategoryActivity.this, Constants.noEntryPresentMessage, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onNoteClick(int position) {
        //Action to do after clicking

        selectedCategory = getCategories.get(position);
        updateCategory.setVisibility(View.VISIBLE);
        deleteCategory.setVisibility(View.VISIBLE);

        if(previouslySelectedCategoryPosition != position){
            previouslySelectedCategoryPosition = position;
        } else {
            Intent intent = new Intent(SelectCategoryActivity.this, ManageEntryActivity.class);
            ((UserProjectDataHolder)getApplication()).setSelectedCategoryDetails(selectedCategory);
            updateCategoryAndEntry();
            resetGlobalFields();
            startActivity(intent);
        }

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

    private void checkPermission(CategoryDetails categoryDetails){

        if(ContextCompat.checkSelfPermission(SelectCategoryActivity.this
                , Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SelectCategoryActivity.this,new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION_REQUEST);
        } else {
            selectedCategory = categoryDetails;
            updateCategoryAndEntry();
            resetGlobalFields();
            Intent intent = new Intent(SelectCategoryActivity.this,SelectContactActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_CONTACT_PERMISSION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, getResources().getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.permissionNotGranted), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void updateCategoryAndEntry(){
        ((UserProjectDataHolder)getApplication()).setSelectedCategoryDetails(selectedCategory);
        ((UserProjectDataHolder)getApplication()).resetEntryDetails();
        ((UserProjectDataHolder)getApplication()).getEntryDetails().setEntryCategoryID(selectedCategory.getCategoryID());
    }

    private void resetGlobalFields(){
        ((UserProjectDataHolder)getApplication()).resetEntryPatternDetails();
        ((UserProjectDataHolder)getApplication()).resetEntryRepeatDateList();
        ((UserProjectDataHolder)getApplication()).resetTransactionModeDetails();
        ((UserProjectDataHolder)getApplication()).resetTransactionTypeDetails();
        ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
        ((UserProjectDataHolder)getApplication()).resetSelectedContactDetails();
    }

}