package com.example.keiji.kchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_USERS";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // app tool bar
    private Toolbar mToolbar;
    // act recycle view
    private RecyclerView mUserList;
    // firebase db reference
    private DatabaseReference mUsersDatabase;



    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // initializing UI elements
        // initializing the toolbar
        mToolbar = (Toolbar)findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // point the db reference to inside the user obj in the db
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // initializing the recycler view (also, create the model class: users)
        mUserList = (RecyclerView) findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }//--end of CONSTRUCTOR


    /**
     * to retrieve data on real time when the activity starts
     */
    @Override
    protected void onStart() {

        super.onStart();

        // create the firebase adapter from the firebase-ui dependency
        // obs: needs the view holder class
        // needs to pass 4 parameters (users class, a layout, a view holder, reference of firebase db)
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder> (

                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase

        ) {

            /**
             * sets the values for the Recycler view items
             * @param usersViewHolder
             * @param users
             * @param position
             */
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {

                // pass name to the view holder so it can store in the layout (using Users model)
                usersViewHolder.setName(users.getName());
                // getting the status
                usersViewHolder.setUserStatus(users.getStatus());
                // getting image
                usersViewHolder.setUserImage(users.getThumbImage(), UsersActivity.this);

                // get the user id based on the position (i) of the Recycler view
                // needs to be final to be accessable in the onclick
                final String userId = getRef(position).getKey();

                // set onclick listener to the user entry
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("userId", userId);
                        startActivity(profileIntent);

                    }//--end of onclick nested function

                });//--end of setonclicklistener anonymous function

            }//--end of populateViewHolder nested function

        };//--end of firebaseRecyclerAdapter

        // set the adapter to populate the view
        mUserList.setAdapter(firebaseRecyclerAdapter);

    }//--end of onStart FUNCTION


    /**************************************ADDITIONAL CLASSES *************************************/

    /**
     * class that will manage the users view
     */
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        /* Global properties vars and instances
        * ---------------------------------------------------*/
        // view that will be used by the firebase adapter
        // will be used to set onclick listener
        View mView;


        /**
         * CONSTRUCTOR
         * @param itemView
         */
        public UsersViewHolder(View itemView) {

            super(itemView);

            // set the mview to the item view
            mView = itemView;

        }//--end of usersviewholder CONSTRUCTOR


        /**
         * sets the name in the layout
         */
        public void setName (String name) {

            TextView userNameView = mView.findViewById(R.id.users_single_name_tv);
            userNameView.setText(name);

        }//--end of setName FUNCTION


        /**
         * sets the status in the layout
         */
        public void setUserStatus (String status) {

            TextView userStatusView = mView.findViewById(R.id.users_single_status_tv);
            userStatusView.setText(status);

        }//--end of setUserStatus FUNCTION


        /**
         * sets the image in the layout
         */
        public void setUserImage (String thumbImage, Context context) {

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image_cv);

            // use picasso to get the image
            Picasso.with(context).load(thumbImage).placeholder(R.drawable.default_avatar).into(userImageView);

        }//--end of setUserStatus FUNCTION


    }//--end of usersViewHolder CLASS

}//--end of UsersActivity ACTIVITY
