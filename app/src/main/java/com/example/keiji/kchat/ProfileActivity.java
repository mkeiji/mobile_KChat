package com.example.keiji.kchat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {


    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_Profile";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // UI elements
    private ImageView mProfileImage;
    private TextView    mProfileName,
                        mProfileStatus,
                        mProfileFriendsCount;
    private Button  mProfileSendReqBtn,
                    mProfileDeclineBtn;

    // database reference for the user
    private DatabaseReference mUsersDatabase;
    // db ref for the friend REQUEST
    private DatabaseReference mFriendReqDatabase;
    // db ref for the friends
    private DatabaseReference mFriendDatabase;

    // firebase auth for user
    private FirebaseUser mCurrentUser;

    // progress dialog
    private ProgressDialog mProgressDialog;

    // friend request state
    private String mCurrentState;


    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get user Id of the clicked account
        final String userId = getIntent().getStringExtra("userId");

        // initialize the db ref and point to the right child
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        // initialize the firebase user
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // initialize ui elements
        mProfileImage = (ImageView) findViewById(R.id.profile_image_civ);
        mProfileName = (TextView) findViewById(R.id.profile_display_name_tv);
        mProfileStatus = (TextView) findViewById(R.id.profile_status_tv);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_friendsCount_tv);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_sendRequest_btn);
        mProfileDeclineBtn = (Button) findViewById(R.id.profile_declineRequest_btn);

        /* set friend state
         * 0 - not_friends
         * 1 - req_sent
         * 2 - req_received
         * 3 - friends
         */
        mCurrentState = "not_friends";

        // load progress bar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        // adding the value event listener
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get data fom db
                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                // update fields in the layout
                mProfileName.setText(displayName);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);


                // ------------------ FRIEND LIST / REQUEST FEATURE ---------------------
                // check if there is a friend request entry in the db
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // check if current user obj has any child (request entry), if yes, then get the type of the request
                        if (dataSnapshot.hasChild(userId)) {

                            String reqType = dataSnapshot.child(userId).child("request_type").getValue().toString();

                            // check the request type
                            if (reqType.equals("received")) {

                                // change the friend state
                                mCurrentState = "req_received";

                                // update the text on the send req btn
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                // show the decline button
                                mProfileDeclineBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineBtn.setEnabled(true);

                            }
                            else if (reqType.equals("sent")) {

                                // change the friend state
                                mCurrentState = "req_sent";

                                // update the text on the send req btn
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                // make the decline btn invisible and only visible in case that there is a friend request
                                mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineBtn.setEnabled(false);

                            }
                            else {

                                // change the friend state
                                mCurrentState = "friends";

                                // update the text on the send req btn
                                mProfileSendReqBtn.setText("Unfriend this person");

                                // make the decline btn invisible and only visible in case that there is a friend request
                                mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineBtn.setEnabled(false);

                            }//--end of check req type

                            // end the progressbar
                            mProgressDialog.dismiss();

                        }//--end check if there is a request
                        // if there is no request check if there is a friend entry
                        else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // check if the profile we are currently on has anything inside
                                    if (dataSnapshot.hasChild(userId)) {

                                        // change the friend state
                                        mCurrentState = "friends";

                                        // update the text on the send req btn
                                        mProfileSendReqBtn.setText("Unfriend this person");

                                        // make the decline btn invisible and only visible in case that there is a friend request
                                        mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineBtn.setEnabled(false);

                                    }

                                    // end the progressbar
                                    mProgressDialog.dismiss();

                                }//--end of ondatachange method

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    // end the progressbar
                                    mProgressDialog.dismiss();

                                }//--end of oncancelled method

                            });//--end of db check for friend

                        }//--end of friend entry check

                        // end the progressbar
                        mProgressDialog.dismiss();

                    }//--end ondata change function

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }//--end onCancelled function

                });//--end of check if there is a friend request

                // end the progressbar
                mProgressDialog.dismiss();

            }//--end of onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//--end of onCancelled

        });//--end of addvalue event listener


        // set onclick listener for the request button
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // disabling the button once the user sent the request
                mProfileSendReqBtn.setEnabled(false);

                // ------------------ NOT FRIENDS STATE ---------------------

                // CFS - check the current friend status
                if (mCurrentState.equals("not_friends")) {

                    // set request type in the FriendRequests table in the db
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // check if is successfull
                            if (task.isSuccessful()) {

                                // if OK then set the requesttype for the person receiving the request
                                mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // change the friend state
                                        mCurrentState = "req_sent";

                                        // update the text on the send req btn
                                        mProfileSendReqBtn.setText("Cancel Friend Request");

                                        Toast.makeText(ProfileActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();

                                        // make the decline btn invisible and only visible in case that there is a friend request
                                        mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineBtn.setEnabled(false);

                                    }

                                });//--end of set req type for the person receiving

                            }
                            else {

                                Toast.makeText(ProfileActivity.this, "Send Request failed =[", Toast.LENGTH_SHORT).show();

                            }//--end of issuccessfull check

                            /*
                            * NOTE: user onFailure() to handle errors and you can re-enable the btn on failure as well
                            * */
                            // re-enabling the button if the request was sent successfully
                            mProfileSendReqBtn.setEnabled(true);

                        }//--end of oncomplete method

                    });//--end of setting request type

                }//--end CFS


                // ------------------- CANCEL REQUEST STATE ---------------------
                // CSR - check if the request state is sent
                if (mCurrentState.equals("req_sent")) {

                    // deleting the friend request in the db
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            // removing the entry from the other user as well (receiver)
                            mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {

                                    // update the friend status and the button text
                                    // re-enabling the button if the request was sent successfully
                                    mProfileSendReqBtn.setEnabled(true);

                                    // change the friend state
                                    mCurrentState = "not_friends";

                                    // update the text on the send req btn
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    // make the decline btn invisible and only visible in case that there is a friend request
                                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineBtn.setEnabled(false);

                                }//--end of on success function

                            });//--end of removing the entry in receiver entry

                        }//--end of onsuccess function

                    });//--end of deleting the friend request

                }//--end of CSR


                // ------------------- REQUEST RECEIVED STATE ---------------------
                // CRR - check if the request state is received
                if (mCurrentState.equals("req_received")) {

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    // add the other user to the current user's column in the friend table
                    mFriendDatabase.child(mCurrentUser.getUid()).child(userId).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            // store the current id in the other user's column
                            mFriendDatabase.child(userId).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {

                                    // remove the request from db
                                    // deleting the friend request in the db
                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // removing the entry from the other user as well (receiver)
                                            mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    // update the friend status and the button text
                                                    // re-enabling the button if the request was sent successfully
                                                    mProfileSendReqBtn.setEnabled(true);

                                                    // change the friend state
                                                    mCurrentState = "friends";

                                                    // update the text on the send req btn
                                                    mProfileSendReqBtn.setText("Unfriend this person");

                                                    // make the decline btn invisible and only visible in case that there is a friend request
                                                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineBtn.setEnabled(false);

                                                }//--end of on success function

                                            });//--end of removing the entry in receiver entry

                                        }//--end of onsuccess function

                                    });//--end of deleting the friend request

                                }//--end of onsuccess method

                            });//--end of storig the current id in the other user's column

                        }//--end of onsuccess function

                    });//--end of adding other user to current user's column

                }//--end of CRR

                // ------------------- FRIENDS STATE ---------------------
                // CRF - check if the request state is friends
                if (mCurrentState.equals("friends")) {

                    // remove the friend from db
                    // deleting the friend entry in the db
                    mFriendDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            // removing the entry from the other user as well (receiver)
                            mFriendDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {

                                    // update the friend status and the button text
                                    // re-enabling the button if the request was sent successfully
                                    mProfileSendReqBtn.setEnabled(true);

                                    // change the friend state
                                    mCurrentState = "not_friends";

                                    // update the text on the send req btn
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    // make the decline btn invisible and only visible in case that there is a friend request
                                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineBtn.setEnabled(false);

                                }//--end of on success function

                            });//--end of removing the entry in receiver entry

                        }//--end of onsuccess function

                    });//--end of deleting the friend entry

                }//--end CRF

            }//--end of onclick function

        });//--end of onclick Listener

    }//--end of CONSTRUCTOR







}//--end of ACTIVITY
