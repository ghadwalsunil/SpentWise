package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.HelpSectionRecyclerViewAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import static com.alpha.spentwise.dataManager.Constants.FAQ_ANSWERS;
import static com.alpha.spentwise.dataManager.Constants.FAQ_QUESTIONS;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class HelpActivity extends AppCompatActivity implements HelpSectionRecyclerViewAdapter.OnNoteListener {

    private RecyclerView helpRecyclerView;
    private HelpSectionRecyclerViewAdapter helpSectionRecyclerViewAdapter;
    private GridLayoutManager gridLayoutManager;
    private TextView helpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        helpTextView = findViewById(R.id.help_tv_welcome);

        setupHelpRecyclerView();

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.helpActivityTitle);
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

    private void setupHelpRecyclerView(){
        helpRecyclerView = findViewById(R.id.help_recyclerView);
        helpSectionRecyclerViewAdapter = new HelpSectionRecyclerViewAdapter(this,FAQ_QUESTIONS,FAQ_ANSWERS,this,-1);
        gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        helpRecyclerView.setLayoutManager(gridLayoutManager);
        helpRecyclerView.setAdapter(helpSectionRecyclerViewAdapter);
    }

    @Override
    public void onNoteClick(int position) {
    }
}