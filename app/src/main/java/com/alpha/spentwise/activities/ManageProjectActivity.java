package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.ManageProjectRecyclerViewAdapter;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class ManageProjectActivity extends AppCompatActivity implements ManageProjectRecyclerViewAdapter.OnNoteListener {

    private UserDetails loggedInUser = new UserDetails();
    private List<ProjectDetails> userProjectList = new ArrayList<>();
    private DatabaseHandler dbHandler = new DatabaseHandler(ManageProjectActivity.this);
    private ManageProjectRecyclerViewAdapter manageProjectRecyclerViewAdapter;
    private GridLayoutManager projectGridLayoutManager;
    private RecyclerView manageProjectRecyclerView;
    private ProjectDetails selectedProject , tempProjectDetails;
    private int selectedProjectPosition = -1, previouslySelectedPosition = -1;
    private FloatingActionButton editProjectFloatingButton, deleteProjectFloatingButton;
    private AlertDialog.Builder deleteProjectPopupBuilder;
    private AlertDialog deleteProjectPopup;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_project);
        manageProjectRecyclerView = findViewById(R.id.manageProject_rv_projectList);
        editProjectFloatingButton = findViewById(R.id.manageProject_fbtn_updateProject);
        deleteProjectFloatingButton = findViewById(R.id.manageProject_fbtn_deleteProject);

        Toolbar toolbar=findViewById(R.id.toolbar_primary);
        ImageView toolbarImageView = findViewById(R.id.toolbar_primary_notes);
        setSupportActionBar(toolbar);
        titleTextView = findViewById(R.id.toolbar_primary_title);
        toolbarImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(R.string.manageProjectActivityTitle);

        loggedInUser = ((UserProjectDataHolder)getApplication()).getLoggedInUser();
        userProjectList = dbHandler.getProjectForUser(loggedInUser);
        updateRepeatEntryStatus(userProjectList);

        //Add an extra icon to add a new project option
        tempProjectDetails = new ProjectDetails(-1,-1,"ADD NEW PROJECT", Constants.newProjectImageName,0,"",-1,-1,0);
        userProjectList.add(tempProjectDetails);

        //Initialize the adapter
        manageProjectRecyclerViewAdapter = new ManageProjectRecyclerViewAdapter(this,userProjectList,this,this,selectedProjectPosition);
        projectGridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        manageProjectRecyclerView.setLayoutManager(projectGridLayoutManager);
        manageProjectRecyclerView.setAdapter(manageProjectRecyclerViewAdapter);

        editProjectFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserProjectDataHolder)getApplication()).setSelectedProject(selectedProject);
                ((UserProjectDataHolder)getApplication()).setSelectedCurrencyDetails(dbHandler.getCurrencyDetails(selectedProject));
                ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
                Intent intent = new Intent(ManageProjectActivity.this, AddUpdateProject.class);
                startActivity(intent);
            }
        });

        deleteProjectFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String popupMessage = getResources().getString(R.string.deleteProjectPopupMessage) + "\n" + selectedProject.getProjectName();

                deleteProjectPopupBuilder = new AlertDialog.Builder(ManageProjectActivity.this);
                deleteProjectPopupBuilder.setMessage(popupMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleteStatus = dbHandler.deleteProject(selectedProject);
                                if(deleteStatus){
                                    Toast.makeText(ManageProjectActivity.this, getResources().getString(R.string.deleteProjectConfirmationSuccess), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ManageProjectActivity.this, getResources().getString(R.string.deleteProjectConfirmationError), Toast.LENGTH_SHORT).show();
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
                deleteProjectPopup = deleteProjectPopupBuilder.create();
                deleteProjectPopup.show();
            }
        });

    }

    @Override
    public void onNoteClick(int position) {

        selectedProject = userProjectList.get(position);

        if(selectedProject.getProjectID() == -1){
            ((UserProjectDataHolder)getApplication()).resetSelectedProject();
            ((UserProjectDataHolder)getApplication()).resetSelectedCurrencyDetails();
            ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
            dbHandler.deleteRedundantTransactionModes();
            editProjectFloatingButton.setVisibility(View.GONE);
            deleteProjectFloatingButton.setVisibility(View.GONE);
            dbHandler.addDefaultTransactions(((UserProjectDataHolder)getApplication()).getSelectedProject());
            Intent intent = new Intent(ManageProjectActivity.this, AddUpdateProject.class);
            startActivity(intent);
        } else if(previouslySelectedPosition != position){
            previouslySelectedPosition = position;
            editProjectFloatingButton.setVisibility(View.VISIBLE);
            deleteProjectFloatingButton.setVisibility(View.VISIBLE);
        } else {
            ((UserProjectDataHolder)getApplication()).setSelectedProject(selectedProject);
            ((UserProjectDataHolder)getApplication()).setSelectedCurrencyDetails(dbHandler.getCurrencyDetails(selectedProject));
            ((UserProjectDataHolder)getApplication()).resetNewCurrencyDetails();
            dbHandler.deleteRedundantTransactionModes();
            Intent intent = new Intent(ManageProjectActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    private void updateRepeatEntryStatus(List<ProjectDetails> projectDetailsList){

        int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(),Constants.dateFormat);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SPENTWISE_PROJECT_ENTRY_UPDATE");
        Intent intent = new Intent(this, LoginPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "SPENTWISE_PROJECT_ENTRY_UPDATE";
        NotificationChannel channel = new NotificationChannel(channelId,"Transactions Added",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);

        for(int i = 0; i < projectDetailsList.size(); i++){

            int entriesUpdated = dbHandler.updateRepeatEntryStatus(projectDetailsList.get(i),currentDate);

            if(entriesUpdated > 0){
                bigText.bigText(entriesUpdated + " new Transactions have been added");
                bigText.setBigContentTitle(projectDetailsList.get(i).getProjectName());
                bigText.setSummaryText("Transactions added");

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setSmallIcon(R.drawable.ic_applogo_cropped);
                mBuilder.setContentTitle(projectDetailsList.get(i).getProjectName());
                mBuilder.setContentText("Transactions added");
                mBuilder.setStyle(bigText);

                mNotificationManager.notify(i, mBuilder.build());
            }

        }
    }

}