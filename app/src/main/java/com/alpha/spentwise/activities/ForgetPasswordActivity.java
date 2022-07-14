package com.alpha.spentwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.SecurityQuestionDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.utils.PasswordUtils;

import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.isValidAnswer;
import static com.alpha.spentwise.utils.CustomFunctions.isValidEmailID;
import static com.alpha.spentwise.utils.CustomFunctions.isValidPassword;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText, securityAnswerEditText, passwordEditText, confirmPasswordEditText;
    private TextView emailIDErrorTextView, passwordErrorTextView, confirmPasswordErrorTextView, secAnswerErrorTextView, invalidAnswerTextView;
    private Button validateAnswerButton, savePasswordButton;
    private AutoCompleteTextView securityQuestionAutoTextView;
    private ArrayAdapter securityQuestionAdapter;
    private List<SecurityQuestionDetails> securityQuestionDetailsList;
    private DatabaseHandler dbHandler = new DatabaseHandler(ForgetPasswordActivity.this);
    private UserDetails userDetails = new UserDetails();
    private SecurityQuestionDetails selectedSecurityQuestion;
    private String emailIDString, passwordString, confirmPasswordString, securityQuestionAnswerString;
    private boolean validEmail, validPassword, validConfirmPassword, validSecAnswer;
    private int securityQuestionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        emailEditText = findViewById(R.id.forgetPassword_edt_email);
        securityQuestionAutoTextView = findViewById(R.id.forgetPassword_atv_securityQuestion);
        securityAnswerEditText = findViewById(R.id.forgetPassword_edt_securityQuestionAnswer);
        validateAnswerButton = findViewById(R.id.forgetPassword_btn_validateSecurityAnswer);
        passwordEditText = findViewById(R.id.forgetPassword_edt_password);
        confirmPasswordEditText = findViewById(R.id.forgetPassword_edt_confirmPassword);
        savePasswordButton = findViewById(R.id.forgetPassword_btn_updateNewPassword);
        emailIDErrorTextView = findViewById(R.id.forgetPassword_tv_emailError);
        secAnswerErrorTextView = findViewById(R.id.forgetPassword_tv_secAnswerError);
        invalidAnswerTextView = findViewById(R.id.forgetPassword_tv_invalidAnswerError);
        passwordErrorTextView = findViewById(R.id.forgetPassword_tv_passwordError);
        confirmPasswordErrorTextView = findViewById(R.id.forgetPassword_tv_confirmPasswordError);
        validEmail = false;
        validPassword = false;
        validConfirmPassword = false;
        validSecAnswer = false;
        securityQuestionDetailsList = dbHandler.getSecurityQuestions();

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailIDString = emailEditText.getText().toString();

                if(!isValidEmailID(emailIDString)){
                    emailIDErrorTextView.setText(getResources().getString(R.string.invalidEmailIDError));
                    emailEditText.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validEmail = false;
                    return;
                }
                emailIDErrorTextView.setText("");
                emailEditText.setBackgroundResource(R.drawable.util_textbox_success_background);
                validEmail = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        securityQuestionAdapter = new ArrayAdapter(this, R.layout.util_exposed_dropdown_item, securityQuestionDetailsList);
        securityQuestionAutoTextView.setAdapter(securityQuestionAdapter);
        //Set default question
        securityQuestionAutoTextView.setText(securityQuestionAdapter.getItem(0).toString(),false);
        securityQuestionID = 1;

        securityQuestionAutoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSecurityQuestion = (SecurityQuestionDetails) securityQuestionAdapter.getItem(position);
                securityQuestionID = selectedSecurityQuestion.getQuestionID();
            }
        });

        securityAnswerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                securityQuestionAnswerString = securityAnswerEditText.getText().toString();

                if(!isValidAnswer(securityQuestionAnswerString)){
                    secAnswerErrorTextView.setText(getResources().getString(R.string.securityQuestionAnswerError));
                    securityAnswerEditText.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validSecAnswer = false;
                    return;
                }
                secAnswerErrorTextView.setText("");
                securityAnswerEditText.setBackgroundResource(R.drawable.util_textbox_success_background);
                validSecAnswer = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        validateAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validEmail && validSecAnswer){
                    closeKeyboard();
                    emailIDString = emailEditText.getText().toString();
                    securityQuestionAnswerString = securityAnswerEditText.getText().toString();

                    userDetails = dbHandler.getUserDetails(emailIDString);
                    if(userDetails == null){
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.invalidDataError), Toast.LENGTH_SHORT).show();
                    } else if(userDetails.getSecurityQuestionID() != securityQuestionID || !userDetails.getSecurityQuestionAnswer().equalsIgnoreCase(securityQuestionAnswerString)){
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.invalidDataError), Toast.LENGTH_SHORT).show();
                    } else {
                        passwordEditText.setVisibility(View.VISIBLE);
                        confirmPasswordEditText.setVisibility(View.VISIBLE);
                        savePasswordButton.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.validDataSuccessMessage), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.missingFieldsError), Toast.LENGTH_SHORT).show();
                }
            }
        });


        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordString = passwordEditText.getText().toString();
                confirmPasswordString = confirmPasswordEditText.getText().toString();

                if(!isValidPassword(passwordString)){
                    passwordErrorTextView.setText(getResources().getString(R.string.invalidPasswordError));
                    passwordEditText.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validPassword = false;
                }
                else {
                    passwordErrorTextView.setText("");
                    passwordEditText.setBackgroundResource(R.drawable.util_textbox_success_background);
                    validPassword = true;
                }

                if(!passwordString.equals(confirmPasswordString)){
                    confirmPasswordErrorTextView.setText(getResources().getString(R.string.passwordNotMatchingError));
                    confirmPasswordEditText.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validConfirmPassword = false;
                } else {
                    confirmPasswordErrorTextView.setText("");
                    confirmPasswordEditText.setBackgroundResource(R.drawable.util_textbox_success_background);
                    validConfirmPassword = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordString = passwordEditText.getText().toString();
                confirmPasswordString = confirmPasswordEditText.getText().toString();
                if(!passwordString.equals(confirmPasswordString)){
                    confirmPasswordErrorTextView.setText(getResources().getString(R.string.passwordNotMatchingError));
                    confirmPasswordEditText.setBackgroundResource(R.drawable.util_textbox_error_background);
                    validConfirmPassword = false;
                    return;
                }
                confirmPasswordErrorTextView.setText("");
                confirmPasswordEditText.setBackgroundResource(R.drawable.util_textbox_success_background);
                validConfirmPassword = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                passwordString = passwordEditText.getText().toString();
                confirmPasswordString = confirmPasswordEditText.getText().toString();

                userDetails.setPasswordSaltString(PasswordUtils.getSalt(30));
                userDetails.setPasswordString(PasswordUtils.generateSecurePassword(passwordString,userDetails.getPasswordSaltString()));

                if(validPassword && validConfirmPassword){
                    boolean passwordStatus = dbHandler.updateUserPassword(userDetails);
                    if(passwordStatus){
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.passwordUpdateSuccess), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.passwordUpdateError), Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(ForgetPasswordActivity.this,LoginPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.missingFieldsError), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Method to close soft keyboard
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}