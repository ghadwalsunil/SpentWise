package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.alpha.spentwise.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class AddUpdateProject extends AppCompatActivity {

    private Button setCurrencyButton, changeImageButton, saveProjectButton;
    private ImageButton addTransactionModeButton;
    private CurrencyDetails selectedCurrencyDetails, newCurrencyDetails;
    private TextInputEditText projectNameEditText, projectAmountEditText, projectTransactionModeEditText, projectBudgetEditText;
    private ImageView projectImageView;
    private ProjectDetails selectedProject = new ProjectDetails();
    private TextView changeCurrencyWarningTextView;
    private DatabaseHandler dbHandler = new DatabaseHandler(AddUpdateProject.this);
    private ListView transactionModeListView;
    private ArrayAdapter<TransactionModeDetails> transactionModeDetailsArrayAdapter;
    private List<TransactionModeDetails> transactionModeDetailsList = new ArrayList<>();
    private AlertDialog.Builder transactionModeDeleteBuilder;
    private AlertDialog transactionModePopup;
    private RequestQueue requestQueue;
    private Double[] conversionRate = {1d};
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_project);

        projectNameEditText = findViewById(R.id.addUpdateProject_edt_projectName);
        projectAmountEditText = findViewById(R.id.addUpdateProject_edt_projectStartAmount);
        setCurrencyButton = findViewById(R.id.addUpdateProject_btn_selectCurrency);
        changeImageButton = findViewById(R.id.addUpdateProject_btn_changeImage);
        projectImageView = findViewById(R.id.addUpdateProject_iv_projectImage);
        saveProjectButton = findViewById(R.id.addUpdateProject_btn_saveProjectButton);
        addTransactionModeButton = findViewById(R.id.addUpdateProject_btn_addTransactionMode);
        transactionModeListView = findViewById(R.id.addUpdateProject_lv_transactionModes);
        projectTransactionModeEditText = findViewById(R.id.addUpdateProject_edt_transactionMode);
        changeCurrencyWarningTextView = findViewById(R.id.addUpdateProject_tv_changeCurrencyWarning);
        projectBudgetEditText = findViewById(R.id.addUpdateProject_edt_projectBudget);


        toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Set existing values
        loadSavedDetails();

        requestQueue = VolleySingleton.getMInstance(this).getRequestQueue();
        setCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilledDetails();
                ((UserProjectDataHolder)getApplication()).setPreviousActivity("AddUpdateProject");
                Intent intent = new Intent(AddUpdateProject.this,SelectCurrencyActivity.class);
                startActivity(intent);
            }
        });

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilledDetails();
                Intent intent = new Intent(AddUpdateProject.this, ProjectImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        transactionModeDetailsArrayAdapter = new ArrayAdapter<>(AddUpdateProject.this,android.R.layout.simple_list_item_1,transactionModeDetailsList);
        transactionModeListView.setAdapter(transactionModeDetailsArrayAdapter);

        transactionModeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String popupMessage = getResources().getString(R.string.deleteTransactionModePopupMessage) + "\n" + transactionModeDetailsList.get(position).getTransactionModeName();

                transactionModeDeleteBuilder = new AlertDialog.Builder(AddUpdateProject.this);
                transactionModeDeleteBuilder.setMessage(popupMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleteStatus = dbHandler.deleteTransactionModeForProject(transactionModeDetailsList.get(position));
                                if(deleteStatus){
                                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.deleteTransactionModeConfirmationSuccess), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.deleteTransactionModeConfirmationError), Toast.LENGTH_SHORT).show();
                                }
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
                transactionModePopup = transactionModeDeleteBuilder.create();
                transactionModePopup.show();
            }
        });

        addTransactionModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilledDetails();
                String transactionModeName = projectTransactionModeEditText.getText().toString().replaceAll("[^a-zA-Z0-9\\s]","").trim().toUpperCase();
                if(transactionModeName.isEmpty()){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.invalidTransactionModeNameError), Toast.LENGTH_SHORT).show();
                    return;
                }

                TransactionModeDetails transactionModeDetails = new TransactionModeDetails(-1,transactionModeName,1);
                if(checkIfTransactionModeAlreadyExists(transactionModeDetailsList,transactionModeDetails)){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.duplicateTransactionModeNameError), Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean status = dbHandler.addTransactionMode(transactionModeDetails,selectedProject);

                if(status){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.addTransactionModeSuccess), Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.addTransactionModeError), Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilledDetails();
                if(selectedProject.getProjectName().isEmpty()){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.blankProjectNameError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dbHandler.checkIfProjectAlreadyExistsForUser(selectedProject)){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectNameAlreadyExistsError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedProject.getProjectCurrencyID().isEmpty()){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.noCurrencySelected), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(transactionModeDetailsList.size() == 0){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.noTransactionModeNameError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newCurrencyDetails != null){
                    if(newCurrencyDetails.getId() != selectedCurrencyDetails.getId()){
                        performCurrencyConversion();
                    }
                } else {
                    saveAndExit();
                }

            }
        });

    }

    private void loadSavedDetails(){

        selectedProject = ((UserProjectDataHolder)getApplication()).getSelectedProject();

        transactionModeDetailsList = dbHandler.getActiveTransactionModes(selectedProject);

        if(selectedProject.getProjectID() == -1){
            toolbar.setTitle(getResources().getString(R.string.addUpdateProject_tv_addTitleLabel));
        }
        else {
            toolbar.setTitle(getResources().getString(R.string.addUpdateProject_tv_updateTitleLabel));
        }
        projectNameEditText.setText(selectedProject.getProjectName());
        selectedCurrencyDetails = ((UserProjectDataHolder)getApplication()).getSelectedCurrencyDetails();
        newCurrencyDetails = ((UserProjectDataHolder)getApplication()).getNewCurrencyDetails();
        if(newCurrencyDetails == null){
            if(selectedCurrencyDetails != null){
                String currencyText = "";
                if(selectedCurrencyDetails.getCurrencySymbol() != null)
                    currencyText = selectedCurrencyDetails.getId().concat(" ( ").concat(selectedCurrencyDetails.getCurrencySymbol()).concat(" )");
                else
                    currencyText = selectedCurrencyDetails.getId();
                setCurrencyButton.setText(currencyText);
                changeCurrencyWarningTextView.setText("");
            } else {
                setCurrencyButton.setText(getResources().getString(R.string.addUpdateProject_btn_selectCurrencyLabel));
            }
        } else {
            if(!newCurrencyDetails.getId().equalsIgnoreCase(selectedCurrencyDetails.getId())){
                String currencyText = "";
                if(newCurrencyDetails.getCurrencySymbol() != null)
                    currencyText = newCurrencyDetails.getId().concat(" ( ").concat(newCurrencyDetails.getCurrencySymbol()).concat(" )");
                else
                    currencyText = newCurrencyDetails.getId();
                setCurrencyButton.setText(currencyText);
                changeCurrencyWarningTextView.setText(getResources().getString(R.string.changeCurrencyWarning));
            } else {
                if(selectedCurrencyDetails != null){
                    String currencyText = "";
                    if(selectedCurrencyDetails.getCurrencySymbol() != null)
                        currencyText = selectedCurrencyDetails.getId().concat(" ( ").concat(selectedCurrencyDetails.getCurrencySymbol()).concat(" )");
                    else
                        currencyText = selectedCurrencyDetails.getId();
                    setCurrencyButton.setText(currencyText);
                    changeCurrencyWarningTextView.setText("");
                } else {
                    setCurrencyButton.setText(getResources().getString(R.string.addUpdateProject_btn_selectCurrencyLabel));
                }
            }
        }

        projectAmountEditText.setText(String.valueOf(selectedProject.getProjectStartAmount()));
        projectBudgetEditText.setText(String.valueOf(selectedProject.getProjectMonthlyBudget()));

        int imageID = getResources().getIdentifier(selectedProject.getProjectImageName(),"drawable", getPackageName());
        projectImageView.setImageResource(imageID);
    }

    private void saveFilledDetails(){

        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectID(selectedProject.getProjectID());
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setUserID(((UserProjectDataHolder)getApplication()).getLoggedInUser().getUserID());
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectName(projectNameEditText.getText().toString().replaceAll("[^a-zA-Z0-9\\s]","").trim().toUpperCase());
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectStatus(1);
        int projectAmount = 0, projectBudget = 0;
        if(!projectAmountEditText.getText().toString().trim().isEmpty()){
            projectAmount = Integer.parseInt(projectAmountEditText.getText().toString().trim());
        }
        if(!projectBudgetEditText.getText().toString().trim().isEmpty()){
            projectBudget = Integer.parseInt(projectBudgetEditText.getText().toString().trim());
        }
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectStartAmount(projectAmount);
        ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectMonthlyBudget(projectBudget);
        selectedProject = ((UserProjectDataHolder)getApplication()).getSelectedProject();
    }

    private boolean checkIfTransactionModeAlreadyExists(List<TransactionModeDetails> transactionModeDetailsList, TransactionModeDetails transactionModeDetails){

        for(int i = 0; i < transactionModeDetailsList.size(); i++){
            if(transactionModeDetails.getTransactionModeName().equalsIgnoreCase(transactionModeDetailsList.get(i).getTransactionModeName())){
                return true;
            }
        }
        return false;

    }

    private void saveAndExit(){

        boolean status;

        if(selectedProject.getProjectID() <= 0){
            status = dbHandler.addNewProject(selectedProject);
            if(status){
                dbHandler.addCurrencyDetails(selectedCurrencyDetails);
                selectedProject = dbHandler.getProjectDetails(selectedProject);
                if(selectedProject.getProjectID() > 0){
                    status = dbHandler.addCategoriesAndUpdateTransactionModesOfNewProject(selectedProject);
                    if(status){
                        Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectSuccess), Toast.LENGTH_SHORT).show();
                        resetAndExit();
                    } else {
                        Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
            }
        } else {
            if(newCurrencyDetails != null){
                if(newCurrencyDetails.getId() != selectedCurrencyDetails.getId()){
                    status = dbHandler.updateEntriesWithNewCurrencies(conversionRate,selectedProject);
                    if(!status){
                        Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.currencyConversionError), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dbHandler.addCurrencyDetails(newCurrencyDetails);
                        status = dbHandler.updateProject(selectedProject);
                        if(status){
                            Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectSuccess), Toast.LENGTH_SHORT).show();
                            resetAndExit();
                        } else {
                            Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    status = dbHandler.updateProject(selectedProject);
                    if(status){
                        Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectSuccess), Toast.LENGTH_SHORT).show();
                        resetAndExit();
                    } else {
                        Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                status = dbHandler.updateProject(selectedProject);
                if(status){
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectSuccess), Toast.LENGTH_SHORT).show();
                    resetAndExit();
                } else {
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.projectError), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void resetAndExit(){

        ((UserProjectDataHolder)getApplication()).resetSelectedProject();
        ((UserProjectDataHolder)getApplication()).resetSelectedCurrencyDetails();
        ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
        Intent intent = new Intent(AddUpdateProject.this, ManageProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void performCurrencyConversion() {

        String currencyPartOfURL = selectedCurrencyDetails.getId() + "_" + newCurrencyDetails.getId();
        String url = Constants.getConversionRateURLLeft + currencyPartOfURL + Constants.getConversionRateURLRight;
        conversionRate = new Double[]{1d};

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String responseString = String.valueOf(response);

                Pattern regexPattern = Pattern.compile("\\{\\\"" + currencyPartOfURL + "\\\":\\{\\\"val\\\":(.*?)\\}\\}");
                Matcher matcher = regexPattern.matcher(responseString);

                if(matcher.find()){
                    conversionRate[0] = Double.parseDouble(matcher.group(1));
                    saveAndExit();
                } else {
                    resetAndExit();
                    Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.conversionError), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetAndExit();
                Toast.makeText(AddUpdateProject.this, getResources().getString(R.string.conversionError), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
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