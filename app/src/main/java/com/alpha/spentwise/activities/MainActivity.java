package com.alpha.spentwise.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ExportEntryDetails;
import com.alpha.spentwise.customDataObjects.MonthlyDataForGraph;
import com.alpha.spentwise.customDataObjects.MonthlyDataFromDB;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.READ_STORAGE_PERMISSION_REQUEST;
import static com.alpha.spentwise.dataManager.Constants.myFormat;
import static com.alpha.spentwise.utils.CustomFunctions.getCustomMonthYear;
import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getNextDateLongCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getNextMonthIntCustom;
import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.customDateFormat;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;
import static com.alpha.spentwise.utils.ImportExportCSV.exportDataAsCSV;
import static com.alpha.spentwise.utils.ImportExportCSV.importDataFromCSV;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;

    private ArrayList<BarEntry> creditBarEntryArrayList;
    private ArrayList<BarEntry> debitBarEntryArrayList;
    private ArrayList<String> labelsName;

    private ArrayList<MonthlyDataForGraph> monthCreditDataArrayList = new ArrayList<>();
    private ArrayList<MonthlyDataForGraph> monthDebitDataArrayList = new ArrayList<>();

    private Button btnAnalytics, exportDataButton, importDataButton;
    private TextView noDataForGraphTextView;
    private TextView balanceTextView;

    private DatabaseHandler dbHandler = new DatabaseHandler(MainActivity.this);
    private BottomNavigationView bottomNavigationView;
    private ProjectDetails selectedProjectDetails;
    private int activeEntryCount;
    private List<ExportEntryDetails> exportEntryDetailsList;
    private CurrencyDetails selectedCurrencyDetails;
    private Currency cur;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAnalytics= findViewById(R.id.main_btn_Analytics);
        bottomNavigationView = findViewById(R.id.main_bottomNavBar);

        Toolbar toolbar=findViewById(R.id.toolbar_primary);
        setSupportActionBar(toolbar);
        titleTextView = findViewById(R.id.toolbar_primary_title);
        titleTextView.setText(R.string.mainActivityTitle);
        ImageView toolbarNotesIconImageView = findViewById(R.id.toolbar_primary_notes);
        toolbarNotesIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NotesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        barChart=findViewById(R.id.main_barChart);
        noDataForGraphTextView = findViewById(R.id.main_tv_noDataMessage);
        exportDataButton = findViewById(R.id.main_btn_ExportData);
        importDataButton = findViewById(R.id.main_btn_importData);
        balanceTextView = findViewById(R.id.main_tv_balance);

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getApplication()).getSelectedProject();
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        if(selectedCurrencyDetails != null){
            cur = Currency.getInstance(selectedCurrencyDetails.getId());
        }
        try {
            checkProjectBudgetExceeded(selectedProjectDetails);
            checkCategoryBudgetExceeded(selectedProjectDetails);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        activeEntryCount = dbHandler.getActiveEntryCount(selectedProjectDetails);
        setBalance();

        btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeEntryCount > 0){
                    Intent intent=new Intent(MainActivity.this,AnalyticsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, Constants.noEntryPresentMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Setting up the bottom navigation bar
        bottomNavigationView.setSelectedItemId(R.id.main_navBar_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                if(item.getItemId() == R.id.main_navBar_home){
                    return true;
                }
                else if(item.getItemId() == R.id.main_navBar_add){
                    Intent intent = new Intent(MainActivity.this, SelectCategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if(item.getItemId() == R.id.main_navBar_search){
                    if(activeEntryCount > 0) {
                        ((UserProjectDataHolder)getApplication()).resetAppliedFilterDetails();
                        Intent intent = new Intent(MainActivity.this, SearchEntryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }
                    else {
                        Toast.makeText(MainActivity.this, Constants.noEntryPresentMessage, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });


        //Setup the bar Graph
        if(activeEntryCount > 0){
            noDataForGraphTextView.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            setUpBarGraph();
        } else {
            noDataForGraphTextView.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        }

        //Export Data as excel button setup
        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeEntryCount > 0){
                    exportEntryDetailsList = dbHandler.exportEntryListForProject(selectedProjectDetails);
                    try {
                        exportDataAsCSV(exportEntryDetailsList,MainActivity.this,selectedProjectDetails);
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.exportDataSuccess), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.exportDataFailure), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, Constants.noEntryPresentMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        importDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }

        });

    }

    private ActivityResultLauncher<Intent> selectFileActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        Uri uri = data.getData();

                        try {
                            //Read the data from the selected file and store it in a string builder
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            if(inputStream == null){
                                throw new Exception();
                            }
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader r = new BufferedReader(inputStreamReader);
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = r.readLine()) != null) {
                                total.append(line).append('\n');
                            }
                            inputStreamReader.close();
                            inputStream.close();

                            //Create a temp file in cache directory and write the data to that file
                            File outputFile = File.createTempFile("temp", ".csv", getCacheDir());

                            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                            outputStreamWriter.write(total.toString());
                            outputStreamWriter.close();

                            fileOutputStream.close();

                            //Use the opencsv reader to create a list of string arrays from data and add ot to the temp table in database
                            List<String[]> inputData = importDataFromCSV(outputFile.getAbsolutePath());

                            if(inputData == null){
                                Toast.makeText(MainActivity.this, "Unable to Read CSV File", Toast.LENGTH_SHORT).show();
                            } else {
                                int rowsToBeAdded = inputData.size();
                                dbHandler.addToTempTable(inputData);
                                int rowsAdded = dbHandler.tempToEntryTransfer(selectedProjectDetails);
                                dbHandler.updateRepeatEntryStatus(selectedProjectDetails, getDateIntCustom(Calendar.getInstance().getTimeInMillis(),dateFormat));
                                String message = rowsAdded + " out of " + rowsToBeAdded + " entries have been added";
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Unable to Read CSV File", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            });


    private void checkPermission(){

        if(ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            selectFileActivityLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_STORAGE_PERMISSION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, getResources().getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.permissionNotGranted), Toast.LENGTH_SHORT).show();
            }
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

    private void setUpBarGraph(){
        creditBarEntryArrayList =new ArrayList<>();
        debitBarEntryArrayList =new ArrayList<>();
        labelsName=new ArrayList<>();
        try {
            getGraphData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i=0;i<monthCreditDataArrayList.size();i++){
            String month=monthCreditDataArrayList.get(i).getMonth();
            int expenses=monthCreditDataArrayList.get(i).getAmount();
            creditBarEntryArrayList.add(new BarEntry(i,expenses));
            labelsName.add(month);
        }

        for (int j=0;j<monthDebitDataArrayList.size();j++){
            int credit=monthDebitDataArrayList.get(j).getAmount();
            debitBarEntryArrayList.add(new BarEntry(j,credit));
        }

        BarDataSet creditBarDataSet=new BarDataSet(creditBarEntryArrayList,"Monthly Credit");
        BarDataSet debitBarDataSet=new BarDataSet(debitBarEntryArrayList,"Monthly Expense");

        creditBarDataSet.setColors(Color.GREEN);
        debitBarDataSet.setColors(Color.RED);

        Description description=new Description();
        description.setText("Months");
        barChart.setDescription(description);
        BarData barData=new BarData(creditBarDataSet,debitBarDataSet);

        barChart.setData(barData);

        barChart.setMaxVisibleValueCount(50);
        barChart.setDrawGridBackground(false);
        barChart.setPinchZoom(false);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(5);

        barData.setBarWidth(0.9f);
        float groupSpace=0.24f;
        float barSpace=0.02f;
        float barWidth=0.36f;

        barData.setBarWidth(barWidth);
        barChart.groupBars(0,groupSpace,barSpace);


        //xvalue formatter

        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsName));

        //set position of labels(months name)

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(labelsName.size());
        xAxis.setLabelRotationAngle(270);
        xAxis.setAxisMinimum(-barData.getBarWidth() / 2);
        xAxis.setAxisMaximum(creditBarEntryArrayList.size()-barData.getBarWidth() / 2);

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void getGraphData() throws ParseException {

        ArrayList<MonthlyDataFromDB> monthlyDebitDataFromDB = dbHandler.getMonthlySumForType(1,selectedProjectDetails);
        ArrayList<MonthlyDataFromDB> monthlyCreditDataFromDB = dbHandler.getMonthlySumForType(2,selectedProjectDetails);

        int firstDate = getDateIntCustom(Calendar.getInstance().getTime().getTime(),dateFormat);

        firstDate = firstDate - (firstDate % 100) + 1;

        if(monthlyCreditDataFromDB.size() == 0 && monthlyDebitDataFromDB.size() == 0){

        } else {
            if(monthlyCreditDataFromDB.size() > 0 && monthlyDebitDataFromDB.size() > 0){
                if(monthlyCreditDataFromDB.get(0).getMonth() > monthlyDebitDataFromDB.get(0).getMonth()){
                    monthCreditDataArrayList = getMonthListArray(monthlyDebitDataFromDB.get(0).getMonth(),firstDate);
                    monthDebitDataArrayList = getMonthListArray(monthlyDebitDataFromDB.get(0).getMonth(),firstDate);
                } else {
                    monthCreditDataArrayList = getMonthListArray(monthlyCreditDataFromDB.get(0).getMonth(),firstDate);
                    monthDebitDataArrayList = getMonthListArray(monthlyCreditDataFromDB.get(0).getMonth(),firstDate);
                }
                monthlyDebitDataFromDB = updateMonthStringInArray(monthlyDebitDataFromDB);
                monthlyCreditDataFromDB = updateMonthStringInArray(monthlyCreditDataFromDB);

                monthCreditDataArrayList = updateAmountInGraphArray(monthCreditDataArrayList,monthlyCreditDataFromDB);
                monthDebitDataArrayList = updateAmountInGraphArray(monthDebitDataArrayList,monthlyDebitDataFromDB);
            } else if(monthlyCreditDataFromDB.size() > 0){
                monthCreditDataArrayList = getMonthListArray(monthlyCreditDataFromDB.get(0).getMonth(),firstDate);
                monthDebitDataArrayList = getMonthListArray(monthlyCreditDataFromDB.get(0).getMonth(),firstDate);

                monthlyCreditDataFromDB = updateMonthStringInArray(monthlyCreditDataFromDB);

                monthCreditDataArrayList = updateAmountInGraphArray(monthCreditDataArrayList,monthlyCreditDataFromDB);

            } else if(monthlyDebitDataFromDB.size() > 0){
                monthCreditDataArrayList = getMonthListArray(monthlyDebitDataFromDB.get(0).getMonth(),firstDate);
                monthDebitDataArrayList = getMonthListArray(monthlyDebitDataFromDB.get(0).getMonth(),firstDate);
                monthlyDebitDataFromDB = updateMonthStringInArray(monthlyDebitDataFromDB);

                monthDebitDataArrayList = updateAmountInGraphArray(monthDebitDataArrayList,monthlyDebitDataFromDB);
            }

        }

    }

    private ArrayList<MonthlyDataForGraph> getMonthListArray(int startDate, int endDate) throws ParseException {

        ArrayList<MonthlyDataForGraph> monthlyDataForGraphs = new ArrayList<>();

        while(endDate >= startDate){
            monthlyDataForGraphs.add(new MonthlyDataForGraph(getCustomMonthYear(startDate,dateFormat,customDateFormat),0));
            startDate = getNextMonthIntCustom(startDate,dateFormat);
        }

        return monthlyDataForGraphs;
    }

    private ArrayList<MonthlyDataFromDB> updateMonthStringInArray(ArrayList<MonthlyDataFromDB> monthlyDataFromDB) throws ParseException {

        for(int i = 0; i < monthlyDataFromDB.size(); i++){
            monthlyDataFromDB.get(i).setMonthName(getCustomMonthYear(monthlyDataFromDB.get(i).getMonth(),dateFormat,customDateFormat));
        }
        return monthlyDataFromDB;
    }

    private ArrayList<MonthlyDataForGraph> updateAmountInGraphArray(ArrayList<MonthlyDataForGraph> monthlyDataForGraphs,ArrayList<MonthlyDataFromDB> monthlyDataFromDBs){

        for(int i = 0,j = 0; i < monthlyDataForGraphs.size() && j < monthlyDataFromDBs.size(); i++){
            if(monthlyDataForGraphs.get(i).getMonth().equalsIgnoreCase(monthlyDataFromDBs.get(j).getMonthName())){
                monthlyDataForGraphs.get(i).setAmount(monthlyDataFromDBs.get(j).getAmount());
                j++;
            }
        }
        return monthlyDataForGraphs;
    }

    private void setBalance(){

        int minDate = dbHandler.getMinDate(selectedProjectDetails);
        int toDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(),dateFormat);
        int debitAmount = 0, creditAmount = 0;
        int debitType = 1, creditType = 2;
        try {
            toDate = getDateIntCustom(getNextDateLongCustom(toDate,dateFormat),dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(minDate > 0 && minDate < toDate) {
            debitAmount = dbHandler.getSumOverIntervalAndType(minDate, toDate, debitType, selectedProjectDetails);
            creditAmount = dbHandler.getSumOverIntervalAndType(minDate, toDate, creditType, selectedProjectDetails);
        }

        int balance = (selectedProjectDetails.getProjectStartAmount() + creditAmount - debitAmount);
        String balanceString = "";
        if(cur != null){
            balanceString = cur.getSymbol() + " " + myFormat.format(balance);
        }

        balanceTextView.setText(balanceString);
        if(balance < 0){
            balanceTextView.setTextColor(getResources().getColor(R.color.red,getTheme()));
        } else if(balance > 0){
            balanceTextView.setTextColor(getResources().getColor(R.color.lime_green,getTheme()));
        } else {
            balanceTextView.setTextColor(getResources().getColor(R.color.black,getTheme()));
        }

    }

    private void checkCategoryBudgetExceeded(ProjectDetails projectDetails) throws ParseException {

        int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(),Constants.dateFormat);
        int startDate = currentDate - (currentDate % 100) + 1;
        int endDate = getNextMonthIntCustom(startDate,dateFormat);
        List<CategoryBudgetDetails> categoryBudgetDetailsList = dbHandler.getBudgetData(startDate,endDate,1,projectDetails);

        for(int i = 0; i < categoryBudgetDetailsList.size(); i++){

            CategoryBudgetDetails categoryBudgetDetails = categoryBudgetDetailsList.get(i);

            if(categoryBudgetDetails.getCategoryExpenseThisMonth() > categoryBudgetDetails.getCategoryBudget()){
                if(categoryBudgetDetails.getCategoryNotificationDate() < currentDate){
                    showNotificationForCategory(i, categoryBudgetDetails, projectDetails);
                    dbHandler.updateCategoryNotificationDate(categoryBudgetDetails.getCategoryID(),currentDate);
                }
            }

        }

    }

    private void showNotificationForCategory(int i, CategoryBudgetDetails categoryBudgetDetails, ProjectDetails projectDetails) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SPENTWISE_PROJECT_CATEGORY_BUDGET_STATUS");
        Intent intent = new Intent(this, LoginPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "SPENTWISE_PROJECT_CATEGORY_BUDGET_STATUS";
        NotificationChannel channel = new NotificationChannel(channelId,"Category Budget Exceeded",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);

        bigText.bigText("Monthly Budget Exceeded for category " + categoryBudgetDetails.getCategoryName() + " of Project " + projectDetails.getProjectName());
        bigText.setBigContentTitle(projectDetails.getProjectName() + " - " + categoryBudgetDetails.getCategoryName());
        bigText.setSummaryText("Category Budget Exceeded");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_applogo_cropped);
        mBuilder.setContentTitle(projectDetails.getProjectName() + " - " + categoryBudgetDetails.getCategoryName());
        mBuilder.setContentText("Category Budget Exceeded");
        mBuilder.setStyle(bigText);

        mNotificationManager.notify(i, mBuilder.build());


    }

    private void checkProjectBudgetExceeded(ProjectDetails projectDetails) throws ParseException {

        int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(),Constants.dateFormat);
        int startDate = currentDate - (currentDate % 100) + 1;
        int endDate = getNextMonthIntCustom(startDate,dateFormat);

        int sum = dbHandler.getSumOverIntervalAndType(startDate,endDate,1,selectedProjectDetails);

        if(sum > projectDetails.getProjectMonthlyBudget() && projectDetails.getProjectMonthlyBudget() > 0){
            if(projectDetails.getProjectNotifyDate() < currentDate){
                showNotificationForProject(projectDetails);
                dbHandler.updateProjectNotificationDate(projectDetails.getProjectID(),currentDate);
            }
        }

    }

    private void showNotificationForProject(ProjectDetails projectDetails) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SPENTWISE_PROJECT_BUDGET_STATUS");
        Intent intent = new Intent(this, LoginPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "SPENTWISE_PROJECT_BUDGET_STATUS";
        NotificationChannel channel = new NotificationChannel(channelId,"Project Budget Exceeded",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);

        bigText.bigText("Monthly Budget Exceeded for Project " + projectDetails.getProjectName());
        bigText.setBigContentTitle(projectDetails.getProjectName());
        bigText.setSummaryText("Project Budget Exceeded");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_applogo_cropped);
        mBuilder.setContentTitle(projectDetails.getProjectName());
        mBuilder.setContentText("Project Budget Exceeded");
        mBuilder.setStyle(bigText);

        mNotificationManager.notify(0, mBuilder.build());


    }

}