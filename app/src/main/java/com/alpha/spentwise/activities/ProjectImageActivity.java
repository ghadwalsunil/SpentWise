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

import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.ProjectImageRecyclerViewAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class ProjectImageActivity extends AppCompatActivity implements ProjectImageRecyclerViewAdapter.OnNoteListener{

    private RecyclerView projectImagesRV;
    private List<Integer> projectImages;
    private ProjectImageRecyclerViewAdapter projectImageAdapter;
    private String[] imageNames = Constants.imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_image);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.projectImageActivityTitle);

        projectImagesRV = findViewById(R.id.projectImage_rv);

        projectImages = new ArrayList<>();

        for(int i = 0; i < imageNames.length; i++){
            projectImages.add(getResources().getIdentifier(imageNames[i],"drawable", getPackageName()));
        }

        projectImageAdapter = new ProjectImageRecyclerViewAdapter(this,projectImages,this,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        projectImagesRV.setLayoutManager(gridLayoutManager);
        projectImagesRV.setAdapter(projectImageAdapter);
    }

    @Override
    public void onNoteClick(int position) {
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectImageName(getResources().getResourceEntryName(projectImages.get(position)));
        Intent intent = new Intent(ProjectImageActivity.this, AddUpdateProject.class);
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