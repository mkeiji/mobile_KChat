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

public class LoginActivity extends AppCompatActivity {


    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_Login";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // firebase authentication object
    private FirebaseAuth mAuth;

    // defining UI elements
    private TextInputLayout mLoginEmail,
                            mLoginPassword;
    private Button mLoginBtn;

    // toolbar object
    private Toolbar mToolbar;

    // progress diaolog object
    private ProgressDialog mLoginProgress;


    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initializing firebase auth obj
        mAuth = FirebaseAuth.getInstance();

        // initializing the toolbar
        mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        // referencing the UI elements
        mLoginEmail     = (TextInputLayout)findViewById(R.id.login_email_ti);
        mLoginPassword  = (TextInputLayout)findViewById(R.id.login_password_ti);
        mLoginBtn       = (Button)findViewById(R.id.login_login_btn);

        // initializing progress dialog
        mLoginProgress = new ProgressDialog(this);

        // set onclick listener to the button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String  email = mLoginEmail.getEditText().getText().toString(),
                        password = mLoginPassword.getEditText().getText().toString();

                // validate fields
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    // show progress bar
                    mLoginProgress.setTitle("Loggin In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    // login user
                    myLoginUser (email, password);

                }

            }//onclick

        });//onclicklistener

    }//--end of CONSTRUCTOR


    /**
     * Logs the user in with email and password
     * (using the firebase assistant auth code)
     * @param email
     * @param password
     */
    private void myLoginUser(String email, String password) {

        // authenticating user with firebase assistant sign in code
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // if task is successfull
                        if (task.isSuccessful()) {

                            // dismiss progress bar on completion
                            mLoginProgress.dismiss();

                            // send user to the main activity
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            // killing current task and creating a new one to avoid user going to
                            // login start activity on back press if user is loged in
                            // use addFlags()
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            // use finish to avoid user coming back to this activity on back press
                            finish();

                        }
                        // if fails
                        else {

                            // hide progress bar on error
                            mLoginProgress.hide();

                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again", Toast.LENGTH_LONG).show();

                        }

                    }//anonymous func

                });//oncompletelistener

    }//--end of myLoginUser FUNCTION

}//--end of Login ACTIVITY
