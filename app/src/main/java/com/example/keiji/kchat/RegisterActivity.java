package com.example.keiji.kchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_Reg_Act";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // defining UI elements
    private TextInputLayout mDisplayName,
                            mEmail,
                            mPassword;
    private Button mCreateBtn;

    // toolbar object
    private Toolbar mToolbar;

    // defining firebase authentication obj
    private FirebaseAuth mAuth;

    // firebase database reference
    private DatabaseReference mDatabase;

    // progress dialog object
    private ProgressDialog mRegProgress;



    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initializing the firebase authentication obj
        mAuth = FirebaseAuth.getInstance();

        // referencing UI elements
        mDisplayName    = (TextInputLayout)findViewById(R.id.reg_display_name_ti);
        mEmail          = (TextInputLayout)findViewById(R.id.reg_email_ti);
        mPassword       = (TextInputLayout)findViewById(R.id.reg_password_ti);
        mCreateBtn      = (Button)findViewById(R.id.reg_create_btn);

        // initializing the toolbar
        mToolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will add the back button in the toolbar

        // initializing the progress dialog
        mRegProgress = new ProgressDialog(this);

        // adding onclick listener to create btn
        mCreateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String  display_name = mDisplayName.getEditText().getText().toString(),
                        email = mEmail.getEditText().getText().toString(),
                        password = mPassword.getEditText().getText().toString();

                // validate fields
                if (TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();

                }
                else {

                    // call the progress bar
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account.");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    // calling the register User function
                    myRegisterUser (display_name, email, password);

                }

            }//onclick

        });//onclicklistener

    }//--end CONSTRUCTOR


    /**
     * Creates a new user based on the createUserWithEmailAndPassword method
     * @param display_name (string): display name
     * @param email (string): email address
     * @param password (string): password
     */
    private void myRegisterUser(final String display_name, String email, String password) {

        // code from firebase assistant (number 4)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // check if sign in is successfull
                        if (task.isSuccessful()) {

                            // get current user info
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uId = currentUser.getUid();

                            // get database reference root
                            // and add a child "user" that has another child "user id"
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

                            // creating a hashmap (key, value) to store data into the user id "table"
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("status", "Hello there, I'm using KChat app.");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");

                            // writting to the db (use on complete listener to proceed with the intent to other activity
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // check if task was successfull
                                    if (task.isSuccessful()) {

                                        // dismiss the progress bar when OK
                                        mRegProgress.dismiss();

                                        // send user to the main activity
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        // killing current task and creating a new one to avoid user going to
                                        // login start activity on back press if user is loged in
                                        // use addFlags()
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        // use finish to avoid user coming back to this activity on back press
                                        finish();

                                    }
                                    else {

                                        Toast.makeText(RegisterActivity.this, "Registration Failed. Please Try again.", Toast.LENGTH_LONG).show();

                                    }//end task check

                                }//end oncomplete

                            });//finnish writting to db

                        }
                        else {

                            // hide the progress bar in case of error
                            mRegProgress.hide();

                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();

                        }

                    }

                });

    }//--end of myRegisterUser FUNCTION


}//--end of Register ACTIVITY
