package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.CurrencyJsonResponse;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.alpha.spentwise.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class SelectCurrencyActivity extends AppCompatActivity {

    private ListView currencyListView;
    private ArrayAdapter<CurrencyDetails> currencyListArrayAdapter;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private ProjectDetails selectedProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_currency);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.selectCurrencyActivityTitle);

        currencyListView = findViewById(R.id.selectCurrency_lv_currencyList);
        progressBar = findViewById(R.id.selectCurrency_progressBar);
        selectedProject = ((UserProjectDataHolder)getApplication()).getSelectedProject();

        requestQueue = VolleySingleton.getMInstance(this).getRequestQueue();
        getCurrencyList();

        currencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(((UserProjectDataHolder)getApplication()).getPreviousActivity().equalsIgnoreCase("ManageEntryActivity")){
                    ((UserProjectDataHolder)getApplication()).setNewCurrencyDetails(currencyListArrayAdapter.getItem(position));
                    Intent intent = new Intent(SelectCurrencyActivity.this,ManageEntryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if(selectedProject.getProjectID() > 0){
                        ((UserProjectDataHolder)getApplication()).setNewCurrencyDetails(currencyListArrayAdapter.getItem(position));
                    } else {
                        ((UserProjectDataHolder)getApplication()).setSelectedCurrencyDetails(currencyListArrayAdapter.getItem(position));
                    }
                    ((UserProjectDataHolder)getApplication()).getSelectedProject().setProjectCurrencyID(currencyListArrayAdapter.getItem(position).getId());
                    Intent intent = new Intent(SelectCurrencyActivity.this,AddUpdateProject.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

    }

    private void getCurrencyList() {

        String url = Constants.getCurrencyListURL;
        List<CurrencyDetails> returnList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CurrencyJsonResponse currencyJsonResponse = gson.fromJson(String.valueOf(response), CurrencyJsonResponse.class);
                Map<String,CurrencyDetails> currencies = currencyJsonResponse.getResults();

                for(String key: currencies.keySet()){
                    returnList.add(currencies.get(key));
                }

                Collections.sort(returnList);

                currencyListArrayAdapter = new ArrayAdapter<>(SelectCurrencyActivity.this, android.R.layout.simple_list_item_1, returnList);
                currencyListView.setAdapter(currencyListArrayAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SelectCurrencyActivity.this, Constants.unableToGetCurrencyListError, Toast.LENGTH_SHORT).show();
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