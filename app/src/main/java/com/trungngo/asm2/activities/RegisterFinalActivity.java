package com.trungngo.asm2.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterFinalActivity extends AppCompatActivity {
    Button backBtn, registerBtn;
    EditText emailEditText, passwordEditText;

    private String username, phone, birthDate, gender;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_final);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        linkViewElements();
        getPreviousRegisterFormInfo();
        setBackBtnAction();
        setRegisterBtnAction();
    }

    //Get View variables from xml id
    private void linkViewElements() {
        backBtn = (Button) findViewById(R.id.registerFinalBackBtn);
        registerBtn = (Button) findViewById(R.id.registerFinalRegisterBtn);
        emailEditText = (EditText) findViewById(R.id.registerFinalEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerFinalPasswordEditText);
    }

    //Go back to RegisterActivity when pressing 'back' button
    private void setBackBtnAction() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterFinalActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Get user input data from previous register activity
    private void getPreviousRegisterFormInfo() {
        Intent intent = getIntent();
        username = (String) intent.getExtras().get(Constants.FSUser.usernameField);
        phone = (String) intent.getExtras().get(Constants.FSUser.phoneField);
        birthDate = (String) intent.getExtras().get(Constants.FSUser.birthDateField);
        gender = (String) intent.getExtras().get(Constants.FSUser.genderField);
    }

    //Save user data to 'users' collection on firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveUserInfo() throws ParseException {
        String[] splitBirthDateStr = birthDate.split("/");

        int day = Integer.parseInt(splitBirthDateStr[0]);
        int month = Integer.parseInt(splitBirthDateStr[1]);
        int year = Integer.parseInt(splitBirthDateStr[2]);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date birthDateNew = df.parse(month + "/" + day + "/" + year);

        //Create data hashmap to push to FireStore db
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FSUser.usernameField, username);
        data.put(Constants.FSUser.phoneField, phone);
        data.put(Constants.FSUser.birthDateField, birthDateNew);
        data.put(Constants.FSUser.genderField, gender);
        data.put(Constants.FSUser.emailField, emailEditText.getText().toString());
        data.put(Constants.FSUser.superuserField, false);
        data.put(Constants.FSUser.ownSitesIdField, new ArrayList<String>());
        data.put(Constants.FSUser.participatingSitesIdField, new ArrayList<String>());

        db.collection(Constants.FSUser.userCollection).add(data);
    }

    //Redirect to LoginActivity
    private void moveToLoginActivity() {
        Intent i = new Intent(RegisterFinalActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    //
    private void setRegisterBtnAction() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Check if username and password fields are empty
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterFinalActivity.this, Constants.ToastMessage.emptyInputError, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Call FireStoreAuth for authentication process
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterFinalActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //If successful
                            Toast.makeText(RegisterFinalActivity.this, Constants.ToastMessage.registerSuccess, Toast.LENGTH_SHORT).show();
                            try {
                                saveUserInfo();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            moveToLoginActivity(); // go to login activity
                        } else {
                            Toast.makeText(RegisterFinalActivity.this, Constants.ToastMessage.registerFailure,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}