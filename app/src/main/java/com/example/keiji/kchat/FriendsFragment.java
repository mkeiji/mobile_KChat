package com.example.keiji.kchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_FriendsFrag";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // act recycle view
    private RecyclerView mFriendsList;

    // firebase db references
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    // firebase user authentication ref
    private FirebaseAuth mAuth;

    // current user id container
    private String mCurrentUserId;

    // view ref
    private View mMainView;



    /* FUNCTIONS
    * ---------------------------------------------------*/

    /**
     * empty CONSTRUCTOR
     */
    public FriendsFragment() {
        // Required empty public constructor
    }//--end of empty CONSTRUCTOR


    /**
     * initialize elements on load
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        // initialize UI elements
        // recycler view
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);

        // user auth (store in the current user ID container)
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // firebase db references
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // recycler view settings
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // return inflated view
        return mMainView;

    }//--end of CONSTRUCTOR


    /**
     * loads recycler view with the adapter when the activity(fragment) starts
     */
    @Override
    public void onStart() {

        super.onStart();

        // create a firebase recycler adapter
        // note: needs a friends obj and a friendsviewholder (create it manually)
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {

            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {

                // pass date to the view holder
                friendsViewHolder.setDate(friends.getDate());

                // get the user id based on the position (i) of the Recycler view
                // needs to be final to be accessable in the onclick
                final String listUserId = getRef(i).getKey();

                // add value event listener to look for changes
                mUsersDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // get user details
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        // pass to viewholder
                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, getContext());

                        // set onclick listener
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                // creating options when clicking the user item
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};

                                // create dialog box to store the options
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                // set dialog box title and body
                                builder.setTitle("Select Options");
                                // onclick listener for the options
                                builder.setItems(options, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // click event for each item
                                        switch (i) {

                                            // for first option (open profile)
                                            case 0:
                                                // send user to profile activity and pass listUserId
                                                Intent profileIntent = new Intent (getContext(), ProfileActivity.class);
                                                profileIntent.putExtra("userId", listUserId);
                                                startActivity(profileIntent);
                                                break;

                                            // for second option (message)
                                            case 1:
//                                                // send user to chat activity and pass listuserid and username
                                                Intent chatIntent = new Intent (getContext(), ChatActivity.class);
                                                chatIntent.putExtra("userId", listUserId);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);
                                                break;

                                        }//--end of switch for dialoginterface options

                                    }//--end of onclick method

                                });//--end of onclick listener for options

                                // show the dialog
                                builder.show();

                            }//--end of onclick method

                        });//--end of setonclicklistnerer in firendsviewholder

                    }//--end of onDataChange method

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //...empty

                    }//--end of onCancelled method

                });//--end of value event listener for listuserid

            }//--end of populateViewHolder function

        };//--end of friendsRecyclerViewAdapter

        // set the adapter
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }//--end of onstart FUNCTION


    /**************************************ADDITIONAL CLASSES *************************************/

    /**
     * class that will manage the users view
     */
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        /* Global properties vars and instances
        * ---------------------------------------------------*/
        // view obj
        View mView;


        /* CONSTRUCTOR
        * ---------------------------------------------------*/
        /**
         * class CONSTRUCTOR
         * @param itemView
         */
        public FriendsViewHolder(View itemView) {

            super(itemView);

            // initialize view
            mView = itemView;

        }//--end of CONSTRUCTOR



        /* setters
        * ---------------------------------------------------*/
        /**
         * sets the date in the text view
         * @param date
         */
        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status_tv);
            userStatusView.setText(date);

        }

        /**
         * sets the name in the text view
         * @param name
         */
        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name_tv);
            userNameView.setText(name);

        }

        /**
         * sets the image in the circular image view
         * @param thumb_image
         * @param ctx
         */
        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image_cv);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        /**
         * sets the user online status
         * @param online_status
         */
//        public void setUserOnline(String online_status) {
//
//            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);
//
//            if(online_status.equals("true")){
//
//                userOnlineView.setVisibility(View.VISIBLE);
//
//            } else {
//
//                userOnlineView.setVisibility(View.INVISIBLE);
//
//            }
//
//        }


    }//--end of friendsviewholder CLASS


}//--END OF FRAGMENT
