package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.textfield.TextInputEditText;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class CategoryManageActivity extends AppCompatActivity {

    private TextInputEditText categoryNameEdt, categoryBudgetEdt;
    private ImageView categoryImageView;
    private Button changeImageBtn , saveCategoryButton;
    private DatabaseHandler dbHandler = new DatabaseHandler(CategoryManageActivity.this);
    private ProjectDetails selectedProjectDetails;
    private CategoryDetails selectedCategory;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manage);

        categoryNameEdt = findViewById(R.id.categoryManage_edt_categoryName);
        categoryImageView = findViewById(R.id.manageCategory_iv_image);
        changeImageBtn = findViewById(R.id.manageCategory_btn_changeImage);
        saveCategoryButton = findViewById(R.id.manageCategory_btn_saveCategory);
        categoryBudgetEdt = findViewById(R.id.categoryManage_edt_categoryBudget);

        //Setup Toolbar
        toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Get global constants
        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        selectedCategory = ((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails();

        loadSavedDetails();

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryManageActivity.this, CategoryImagesActivity.class);
                saveSelectedDetails();
                startActivity(intent);
            }
        });

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedDetails();
                if(selectedCategory.getCategoryName().isEmpty()){
                    Toast.makeText(CategoryManageActivity.this, getResources().getString(R.string.blankCategoryNameError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedCategory.getCategoryID() == -1){
                    if(dbHandler.checkIfCategoryAlreadyExists(selectedCategory, selectedProjectDetails)){
                        Toast.makeText(CategoryManageActivity.this, getResources().getString(R.string.duplicateCategoryNameError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHandler.addNewCategory(selectedCategory, selectedProjectDetails);
                    Toast.makeText(CategoryManageActivity.this, getResources().getString(R.string.addCategoryConfirmationSuccess), Toast.LENGTH_SHORT).show();
                }
                else{
                    if(dbHandler.checkIfCategoryAlreadyExists(selectedCategory, selectedProjectDetails)){
                        Toast.makeText(CategoryManageActivity.this, getResources().getString(R.string.duplicateCategoryNameError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHandler.updateCategory(selectedCategory);
                    Toast.makeText(CategoryManageActivity.this, getResources().getString(R.string.updateCategoryConfirmationSuccess), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(CategoryManageActivity.this, SelectCategoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void loadSavedDetails(){

        selectedCategory = ((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails();
        if(selectedCategory.getCategoryID() == -1){
            toolbar.setTitle("NEW CATEGORY");
        }
        else{
            toolbar.setTitle("UPDATE CATEGORY");
        }
        categoryNameEdt.setText(selectedCategory.getCategoryName());
        categoryBudgetEdt.setText(String.valueOf(selectedCategory.getCategoryBudget()));

        int imageID = getResources().getIdentifier(selectedCategory.getCategoryImageName(),"drawable", getPackageName());
        categoryImageView.setImageResource(imageID);
    }

    private void saveSelectedDetails(){

        selectedCategory.setCategoryID(((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails().getCategoryID());
        selectedCategory.setCategoryName(categoryNameEdt.getText().toString().replaceAll("[^a-zA-Z0-9\\s]","").trim().toUpperCase());

        int categoryBudget = 0;
        if(!categoryBudgetEdt.getText().toString().trim().isEmpty()){
            categoryBudget = Integer.parseInt(categoryBudgetEdt.getText().toString().trim());
        }
        selectedCategory.setCategoryBudget(categoryBudget);

        selectedCategory.setCategoryImageName(((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails().getCategoryImageName());
        selectedCategory.setCategoryStatus(1);

        ((UserProjectDataHolder)getApplication()).setSelectedCategoryDetails(selectedCategory);

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