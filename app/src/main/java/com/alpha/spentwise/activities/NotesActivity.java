package com.alpha.spentwise.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.NotesRecyclerViewAdapter;
import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.myFormat;
import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getNextMonthIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getNumberOfDaysInMonth;

public class NotesActivity extends AppCompatActivity {

    private ImageView projectIconImageView;
    private TextView projectNameTextView, projectStatusTextView, projectEstimatedTextView, projectBudgetTextView, projectSpentTextView;
    private ProjectDetails selectedProjectDetails;
    private CurrencyDetails selectedCurrencyDetails;
    private Currency cur;
    private DatabaseHandler dbHandler = new DatabaseHandler(NotesActivity.this);
    private RecyclerView notesRecyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;
    private GridLayoutManager gridLayoutManager;
    private CardView cardView_1, cardView_2;
    private TextView noDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        projectIconImageView = findViewById(R.id.notes_iv_projectIcon);
        projectNameTextView = findViewById(R.id.notes_tv_projectName);
        projectStatusTextView = findViewById(R.id.notes_tv_statusDesc);
        projectEstimatedTextView = findViewById(R.id.notes_tv_estimatedValue);
        projectBudgetTextView = findViewById(R.id.notes_tv_budgetValue);
        projectSpentTextView = findViewById(R.id.notes_tv_spentValue);
        notesRecyclerView = findViewById(R.id.notes_rv_categoryRecyclerView);
        cardView_1 = findViewById(R.id.notes_cardView_1);
        cardView_2 = findViewById(R.id.notes_cardView_2);
        noDataTextView = findViewById(R.id.notes_tv_noData);

        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        if(selectedCurrencyDetails != null){
            cur = Currency.getInstance(selectedCurrencyDetails.getId());
        }

        boolean projectSetup = false, categorySetup = false;

        try {
            projectSetup = setupProjectBudgetCard();
            categorySetup = setUpCategoryBudgetCards();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!projectSetup){
            cardView_1.setVisibility(View.GONE);
        }
        if(!categorySetup){
            cardView_2.setVisibility(View.GONE);
        }
        if(!projectSetup && !categorySetup){
            noDataTextView.setVisibility(View.VISIBLE);
        }



        //Toolbar setup
        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.notesActivityTitle);
    }

    private boolean setupProjectBudgetCard() throws ParseException {

        if(selectedProjectDetails.getProjectMonthlyBudget() <= 0){
            return false;
        } else {
            int imageID = getResources().getIdentifier(selectedProjectDetails.getProjectImageName(),"drawable", getPackageName());
            projectIconImageView.setImageResource(imageID);
            projectIconImageView.setColorFilter(getResources().getColor(R.color.app_primary_color,getTheme()));
            projectNameTextView.setText(selectedProjectDetails.getProjectName());

            int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(), Constants.dateFormat);
            int startDate = currentDate - (currentDate % 100) + 1;
            int endDate = getNextMonthIntCustom(startDate,dateFormat);
            int sum = dbHandler.getSumOverIntervalAndType(startDate,endDate,1,selectedProjectDetails);
            int daysInMonth = getNumberOfDaysInMonth(currentDate,dateFormat);
            int estimatedSpending = Math.round(((float)sum / (float)(currentDate % 100)) * daysInMonth);
            int dayOfExceedingSpending = Math.round(((float)selectedProjectDetails.getProjectMonthlyBudget()) / ((float)sum / (float)(currentDate % 100)));

            String projectStatus = "";
            if(sum >= selectedProjectDetails.getProjectMonthlyBudget()){
                projectStatus = "You have already exhausted your monthly budget";
                projectEstimatedTextView.setTextColor(Color.RED);
                projectStatusTextView.setTextColor(Color.RED);
                projectSpentTextView.setTextColor(Color.RED);
            } else if(estimatedSpending > selectedProjectDetails.getProjectMonthlyBudget()){
                projectStatus = "Slow Down! At the current rate, you'll exhaust your budget by day " + dayOfExceedingSpending + " of this month";
                projectEstimatedTextView.setTextColor(getResources().getColor(R.color.bright_orange,getTheme()));
                projectStatusTextView.setTextColor(getResources().getColor(R.color.bright_orange,getTheme()));
                projectSpentTextView.setTextColor(getResources().getColor(R.color.bright_orange,getTheme()));
            } else {
                projectStatus = "Well done! Your spending is on track with your budget this month";
                projectEstimatedTextView.setTextColor(getResources().getColor(R.color.lime_green,getTheme()));
                projectStatusTextView.setTextColor(getResources().getColor(R.color.lime_green,getTheme()));
                projectSpentTextView.setTextColor(getResources().getColor(R.color.lime_green,getTheme()));
            }
            projectStatusTextView.setText(projectStatus);
            projectBudgetTextView.setTextColor(getResources().getColor(R.color.lime_green,getTheme()));

            String estimatedString = "", budgetString = "", spentString = "";
            if(cur != null){
                estimatedString = cur.getSymbol() + " " + myFormat.format(estimatedSpending);
                budgetString = cur.getSymbol() + " " + myFormat.format(selectedProjectDetails.getProjectMonthlyBudget());
                spentString = cur.getSymbol() + " " + myFormat.format(sum);
            }
            projectSpentTextView.setText(spentString);
            projectEstimatedTextView.setText(estimatedString);
            projectBudgetTextView.setText(budgetString);
            return true;
        }

    }

    private boolean setUpCategoryBudgetCards() throws ParseException {

        int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(), Constants.dateFormat);
        int startDate = currentDate - (currentDate % 100) + 1;
        int endDate = getNextMonthIntCustom(startDate,dateFormat);
        List<CategoryBudgetDetails> categoryBudgetDetailsList = dbHandler.getBudgetData(startDate,endDate,1,selectedProjectDetails);
        int daysInMonth = getNumberOfDaysInMonth(currentDate,dateFormat);

        if(categoryBudgetDetailsList.size() == 0){
            return false;
        } else {
            notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(this,this,categoryBudgetDetailsList,currentDate,daysInMonth,cur);
            gridLayoutManager = new GridLayoutManager(this,1,RecyclerView.VERTICAL,false);
            notesRecyclerView.setLayoutManager(gridLayoutManager);
            notesRecyclerView.setAdapter(notesRecyclerViewAdapter);
            return true;
        }
    }

}