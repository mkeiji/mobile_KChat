package com.example.keiji.kchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_SETTINGS";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // firebase user
    FirebaseUser mCurrentUser;

    // firebase db
    DatabaseReference mUserDatabase;

    // UI elements
    CircleImageView mCircleImageView;
    TextView    mDisplayName,
                mStatus;
    Button  mChangeImage,
            mChangeStatus;




    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize UI elements
        mCircleImageView = (CircleImageView)findViewById(R.id.settings_image_civ);
        mDisplayName = (TextView)findViewById(R.id.settings_displayName_tv);
        mStatus = (TextView)findViewById(R.id.settings_status_tv);
        mChangeImage = (Button)findViewById(R.id.settings_changeImage_btn);
        mChangeStatus = (Button)findViewById(R.id.settings_changeStatus_btn);


        // initialize firebase user and get the id
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mCurrentUser.getUid();

        // initialize firebase db and point to the current user db
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        // retrieve data from database using addValueEventListener
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get user's values from db
                String  name        = dataSnapshot.child("name").getValue().toString(),
                        image       = dataSnapshot.child("image").getValue().toString(),
                        thumbImage  = dataSnapshot.child("thumb_image").getValue().toString(),
                        status      = dataSnapshot.child("status").getValue().toString();

                // update the ui elements with data from db
                mDisplayName.setText(name);
                mStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });//end valueeventlistener

        // onClick listener for change status btn
        mChangeStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // get value of the current status
                String current_status = mStatus.getText().toString();

                // send user to Status Activity with status data
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("statusValue", current_status);
                startActivity(statusIntent);

            }//end onclick

        });//end status onclick listener



    }//--end of CONSTRUCTOR



}//--end SettingsActivity ACTIVITY
