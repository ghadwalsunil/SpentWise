package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.CategoryImageRecyclerViewAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class CategoryImagesActivity extends AppCompatActivity implements CategoryImageRecyclerViewAdapter.OnNoteListener {

    private RecyclerView categoryImagesRV;
    private List<Integer> images;
    private CategoryImageRecyclerViewAdapter categoryImageAdapter;
    private String[] imageNames = Constants.imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_images);

        categoryImagesRV = findViewById(R.id.categoryImages_recyclerView);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.categoryImageTitle);

        images = new ArrayList<>();

        for(int i = 0; i < imageNames.length; i++){
            images.add(getResources().getIdentifier(imageNames[i],"drawable", getPackageName()));
        }

        categoryImageAdapter = new CategoryImageRecyclerViewAdapter(this,images,this,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        categoryImagesRV.setLayoutManager(gridLayoutManager);
        categoryImagesRV.setAdapter(categoryImageAdapter);

    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(CategoryImagesActivity.this, CategoryManageActivity.class);
        ((UserProjectDataHolder)getApplication()).getSelectedCategoryDetails().setCategoryImageName(getResources().getResourceEntryName(images.get(position)));
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