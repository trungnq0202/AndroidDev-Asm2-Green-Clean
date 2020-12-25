package com.trungngo.asm2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    Button loginActivityBtn, registerActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        linkViewElements();
        setLoginActivityBtnAction();
        setRegisterActivityBtnAction();
    }

    //Get View variables from xml id
    private void linkViewElements() {
        loginActivityBtn = (Button) findViewById(R.id.loginActivityBtn);
        registerActivityBtn = (Button) findViewById(R.id.registerActivityBtn);
    }

    //Set action when press on 'login' button, move to login activity
    private void setLoginActivityBtnAction() {
        loginActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Set action when press on 'register' button, move to register activity
    private void setRegisterActivityBtnAction() {
        registerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}