package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.AnalyticsPageAdapter;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class AnalyticsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TabItem monthlyBudgetTab, pieChartTab, statsTab;
    private ViewPager2 viewPager2;
    private AnalyticsPageAdapter analyticsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        tabLayout = findViewById(R.id.analytics_tabLayout);
        monthlyBudgetTab = findViewById(R.id.analytics_tab_budget);
        pieChartTab = findViewById(R.id.analytics_tab_pieChart);
        statsTab = findViewById(R.id.analytics_tab_stats);
        viewPager2 = findViewById(R.id.analytics_viewPager);
        analyticsPageAdapter = new AnalyticsPageAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager2.setAdapter(analyticsPageAdapter);
        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.analyticsActivityTitle);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


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