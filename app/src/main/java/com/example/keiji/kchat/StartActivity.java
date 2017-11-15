package com.example.keiji.kchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_Start_Act";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // defining UI elements
    private Button  mRegBtn,
                    mLoginBtn;



    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // referencing UI elements
        mRegBtn     = (Button)findViewById(R.id.start_reg_btn);
        mLoginBtn   = (Button)findViewById(R.id.start_login_btn);

        // set onclick listener for register button
        mRegBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // create intent to send user to the register interface
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);

            }//onclick

        });//anonymous onclicklistener

        // set onclick listener for login button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // create intent to send user to the register interface
                Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(login_intent);

            }//onclick

        });//anonymous onclicklistener

    }//--end of CONSTRUCTOR



}//--end of Start ACTIVITY
