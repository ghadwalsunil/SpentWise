package com.alpha.spentwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.SecurityQuestionDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.utils.PasswordUtils;

import java.util.List;
import java.util.regex.Pattern;

import static com.alpha.spentwise.utils.CustomFunctions.isValidAnswer;
import static com.alpha.spentwise.utils.CustomFunctions.isValidEmailID;
import static com.alpha.spentwise.utils.CustomFunctions.isValidName;
import static com.alpha.spentwise.utils.CustomFunctions.isValidPassword;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText firstName, lastName, emailID, password, confirmPassword, securityQuestionAnswer;
    private AutoCompleteTextView securityQuestion;
    private Button createUser;
    private String firstNameString, lastNameString, emailIDString, passwordString, confirmPasswordString, securityQuestionAnswerString;
    private int securityQuestionID, userStatus;
    private TextView firstNameError, lastNameError, emailIDError, passwordError, confirmPasswordError, secAnswerError;
    private boolean validFirstName, validLastName, validEmail, validPassword, validConfirmPassword, validSecAnswer;
    private ArrayAdapter securityQuestionAdapter;
    private List<SecurityQuestionDetails> securityQuestionDetailsList;
    private DatabaseHandler dbHandler = new DatabaseHandler(RegisterUserActivity.this);
    private UserDetails newUser = new UserDetails();
    private SecurityQuestionDetails selectedSecurityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        firstName = findViewById(R.id.registerUser_edt_firstName);
        lastName = findViewById(R.id.registerUser_edt_lastName);
        emailID = findViewById(R.id.registerUser_edt_email);
        password = findViewById(R.id.registerUser_edt_password);
        confirmPassword = findViewById(R.id.registerUser_edt_confirmPassword);
        securityQuestion = findViewById(R.id.registerUser_atv_securityQuestion);
        securityQuestionAnswer = findViewById(R.id.registerUser_edt_securityQuestionAnswer);
        createUser = findViewById(R.id.registerUser_btn_registerUser);
        firstNameError = findViewById(R.id.registerUser_tv_firstNameError);
        lastNameError = findViewById(R.id.registerUser_tv_lastNameError);
        emailIDError = findViewById(R.id.registerUser_tv_emailError);
        passwordError = findViewById(R.id.registerUser_tv_passwordError);
        confirmPasswordError = findViewById(R.id.registerUser_tv_confirmPasswordError);
        secAnswerError = findViewById(R.id.registerUser_tv_secAnswerError);
        validFirstName = false;
        validLastName = false;
        validEmail = false;
        validPassword = false;
        validConfirmPassword = false;
        validSecAnswer = false;
        securityQuestionDetailsList = dbHandler.getSecurityQuestions();

        //Validating each input field with an on text change listener

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNameString = firstName.getText().toString();

                if(!isValidName(firstNameString)){
                    firstNameError.setText(getResources().getString(R.string.specialCharacterError));
                    firstName.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validFirstName = false;
                    return;
                }
                firstNameError.setText("");
                firstName.setBackgroundResource(R.drawable.util_textbox_success_background);
                validFirstName = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastNameString = lastName.getText().toString();

                if(!isValidName(lastNameString)){
                    lastNameError.setText(getResources().getString(R.string.specialCharacterError));
                    lastName.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validLastName = false;
                    return;
                }
                lastNameError.setText("");
                lastName.setBackgroundResource(R.drawable.util_textbox_success_background);
                validLastName = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailIDString = emailID.getText().toString();

                if(!isValidEmailID(emailIDString)){
                    emailIDError.setText(getResources().getString(R.string.invalidEmailIDError));
                    emailID.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validEmail = false;
                    return;
                }
                emailIDError.setText("");
                emailID.setBackgroundResource(R.drawable.util_textbox_success_background);
                validEmail = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordString = password.getText().toString();
                confirmPasswordString = confirmPassword.getText().toString();

                if(!isValidPassword(passwordString)){
                    passwordError.setText(getResources().getString(R.string.invalidPasswordError));
                    password.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validPassword = false;
                }
                else {
                    passwordError.setText("");
                    password.setBackgroundResource(R.drawable.util_textbox_success_background);
                    validPassword = true;
                }

                if(!passwordString.equals(confirmPasswordString)){
                    confirmPasswordError.setText(getResources().getString(R.string.passwordNotMatchingError));
                    confirmPassword.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validConfirmPassword = false;
                } else {
                    confirmPasswordError.setText("");
                    confirmPassword.setBackgroundResource(R.drawable.util_textbox_success_background);
                    validConfirmPassword = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordString = password.getText().toString();
                confirmPasswordString = confirmPassword.getText().toString();
                if(!passwordString.equals(confirmPasswordString)){
                    confirmPasswordError.setText(getResources().getString(R.string.passwordNotMatchingError));
                    confirmPassword.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validConfirmPassword = false;
                    return;
                }
                confirmPasswordError.setText("");
                confirmPassword.setBackgroundResource(R.drawable.util_textbox_success_background);
                validConfirmPassword = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        securityQuestionAdapter = new ArrayAdapter(this, R.layout.util_exposed_dropdown_item, securityQuestionDetailsList);
        securityQuestion.setAdapter(securityQuestionAdapter);
        //Set default question
        securityQuestion.setText(securityQuestionAdapter.getItem(0).toString(),false);
        securityQuestionID = 1;

        securityQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSecurityQuestion = (SecurityQuestionDetails) securityQuestionAdapter.getItem(position);
                securityQuestionID = selectedSecurityQuestion.getQuestionID();
            }
        });

        securityQuestionAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                securityQuestionAnswerString = securityQuestionAnswer.getText().toString();

                if(!isValidAnswer(securityQuestionAnswerString)){
                    secAnswerError.setText(getResources().getString(R.string.securityQuestionAnswerError));
                    securityQuestionAnswer.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validSecAnswer = false;
                    return;
                }
                secAnswerError.setText("");
                securityQuestionAnswer.setBackgroundResource(R.drawable.util_textbox_success_background);
                validSecAnswer = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validFirstName && validLastName && validEmail && validPassword && validConfirmPassword && validSecAnswer){

                    firstNameString = firstName.getText().toString().trim();
                    lastNameString = lastName.getText().toString().trim();
                    emailIDString = emailID.getText().toString();
                    passwordString = password.getText().toString();
                    securityQuestionAnswerString = securityQuestionAnswer.getText().toString().trim();

                    newUser.setFirstName(firstNameString);
                    newUser.setLastName(lastNameString);
                    newUser.setEmailID(emailIDString);
                    newUser.setPasswordSaltString(PasswordUtils.getSalt(30));
                    newUser.setPasswordString(PasswordUtils.generateSecurePassword(passwordString,newUser.getPasswordSaltString()));
                    newUser.setSecurityQuestionID(securityQuestionID);
                    newUser.setSecurityQuestionAnswer(securityQuestionAnswerString);
                    newUser.setUserStatus(1);
                    newUser.setUserLoggedIn(0);

                    if(dbHandler.checkIfUserAlreadyExists(emailIDString)){
                        Toast.makeText(RegisterUserActivity.this, getResources().getString(R.string.emailAlreadyExistsError), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean creationStatus = dbHandler.addNewUser(newUser);
                    if(creationStatus){
                        Toast.makeText(RegisterUserActivity.this, getResources().getString(R.string.userCreationSuccess), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterUserActivity.this, getResources().getString(R.string.userCreationError), Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(RegisterUserActivity.this,LoginPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterUserActivity.this, getResources().getString(R.string.missingFieldsError), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}