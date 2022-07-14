package com.alpha.spentwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.alpha.spentwise.utils.AlarmReceiver;
import com.alpha.spentwise.utils.PasswordUtils;

import java.util.Calendar;

public class LoginPageActivity extends AppCompatActivity {

    private EditText emailTextBox, passwordTextBox;
    private Button loginButton, registerUserButton, forgotPasswordButton;
    private String enteredEmailID, enteredPassword;
    private DatabaseHandler dbHandler = new DatabaseHandler(LoginPageActivity.this);
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        emailTextBox = findViewById(R.id.loginPage_edt_email);
        passwordTextBox = findViewById(R.id.loginPage_edt_password);
        loginButton = findViewById(R.id.loginPage_btn_login);
        registerUserButton = findViewById(R.id.loginPage_btn_registerUser);
        forgotPasswordButton = findViewById(R.id.loginPage_btn_forgotPassword);

        userDetails = dbHandler.getLoggedInUserDetails();

        if(userDetails != null){
            if(userDetails.getUserID() > 0){
                logUserIn();
            }
        }

        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredEmailID = emailTextBox.getText().toString().trim();
                enteredPassword = passwordTextBox.getText().toString().trim();

                if(enteredEmailID.isEmpty()){
                    Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.emptyEmailFieldError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(enteredPassword.isEmpty()){
                    Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.emptyPasswordFieldError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dbHandler.checkIfUserAlreadyExists(enteredEmailID)){
                    userDetails = dbHandler.getUserDetails(enteredEmailID);
                    boolean passwordMatch = PasswordUtils.verifyUserPassword(enteredPassword, userDetails.getPasswordString(), userDetails.getPasswordSaltString());

                    if(passwordMatch){
                        if(dbHandler.setUserLoggedInStatus(enteredEmailID)){
                            Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                            logUserIn();
                        } else{
                            Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.loginFailure), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.incorrectCredentialsError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else {
                    Toast.makeText(LoginPageActivity.this, getResources().getString(R.string.incorrectCredentialsError), Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    private void logUserIn(){
        cancelAlarm();
        createReminderForUser();
        setAlarm();
        Intent intent = new Intent(LoginPageActivity.this, ManageProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((UserProjectDataHolder)getApplication()).setLoggedInUser(userDetails);
        startActivity(intent);
    }

    private void createReminderForUser(){

        CharSequence name = "SpentWiseReminderChannel";
        String description = "Reminder for project in SpentWise";

        NotificationChannel notificationChannel = new NotificationChannel("SPENTWISE_PROJECT_REMINDER",name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

    private void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,20);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelAlarm(){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,0);

        alarmManager.cancel(pendingIntent);

    }

}