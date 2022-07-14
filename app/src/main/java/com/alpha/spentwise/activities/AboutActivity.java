package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alpha.spentwise.BuildConfig;
import com.alpha.spentwise.R;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import static com.alpha.spentwise.dataManager.Constants.ABOUT_TEXT;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class AboutActivity extends AppCompatActivity {

    private TextView appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        appVersion = findViewById(R.id.about_tv_appVersion);

        String appVersionString = "Version " + BuildConfig.VERSION_NAME;
        appVersion.setText(appVersionString);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.about_tv_titleLabel);

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