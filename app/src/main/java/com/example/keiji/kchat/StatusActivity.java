package com.example.keiji.kchat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_STATUS";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // app tool bar
    private Toolbar mToolbar;

    // ui elements
    private TextInputLayout mStatus;
    private Button mSaveBtn;

    // firebase db
    private DatabaseReference mStatusDb;
    private FirebaseUser mCurrentUser;

    // progress dialog
    private ProgressDialog mProgress;

    // current status from the settings activity
    String currentStatus;


    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // initialize app toolbar
        mToolbar = (Toolbar) findViewById(R.id.status_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize ui elements
        mStatus = (TextInputLayout) findViewById(R.id.status_status_til);
        mSaveBtn = (Button) findViewById(R.id.status_save_btn);

        // get current status and populate the textinput layout
        currentStatus = getIntent().getStringExtra("statusValue");
        mStatus.getEditText().setText(currentStatus);

        // initialize firebase db
        // 1. get current user id
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mCurrentUser.getUid();
        // 2. point to the db instance of the current user
        mStatusDb = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        // set onclick listener to save button
        mSaveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // initialize progress bar
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait...");
                mProgress.show();

                // get status from the edit text
                String status = mStatus.getEditText().getText().toString();

                // set it to the db (add an oncomplete listener)
                mStatusDb.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // if successfull dismiss dialog
                        if (task.isSuccessful()) {

                            mProgress.dismiss();

                        }
                        else {

                            // display an error msg
                            Toast.makeText(StatusActivity.this, "Error while saving you changes", Toast.LENGTH_SHORT).show();

                        }

                    }//end oncomplete

                });//end listsener

            }//end onclick

        }); //end onclicklistener


    }//--end CONSTRUCTOR

}//--end status ACTIVITY
