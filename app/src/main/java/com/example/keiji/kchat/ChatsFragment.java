package com.example.keiji.kchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class ChatsFragment extends Fragment {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_FriendsFrag";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // act recycle view
    private RecyclerView mMessagesList;

    // firebase db references
    private DatabaseReference mMessagesDatabase;
    private DatabaseReference mUsersDatabase;

    // firebase user authentication ref
    private FirebaseAuth mAuth;

    // current user id container
    private String mCurrentUserId;

    // view ref
    private View mMainView;


    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    /**
     * Empty CONSTRUCTOR
     */
    public ChatsFragment() {
        // Required empty public constructor
    }//--end of empty CONSTRUCTOR


    /**
     * onCreate
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        // initialize UI elements
        // recycler view
        mMessagesList = (RecyclerView) mMainView.findViewById(R.id.chatsFragment_messages_list);

        // user auth (store in the current user ID container)
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // firebase db references
        mMessagesDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // recycler view settings
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(new LinearLayoutManager(getContext()));

        // return inflated view
        return mMainView;

    }//--end of oncreate



    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * loads recycler view with the adapter when the activity(fragment) starts
     */
    @Override
    public void onStart() {

        super.onStart();

        // create a firebase recycler adapter
        // note: needs a friends obj and a friendsviewholder (create it manually)
        FirebaseRecyclerAdapter<Messages, MessagesViewHolder> messagesRecyclerViewAdapter = new FirebaseRecyclerAdapter<Messages, MessagesViewHolder>(

                Messages.class,
                R.layout.users_single_layout,
                MessagesViewHolder.class,
                mMessagesDatabase

        ) {

            @Override
            protected void populateViewHolder(final MessagesViewHolder messagesViewHolder, Messages messages, int i) {

                // get the user id based on the position (i) of the Recycler view
                // needs to be final to be accessable in the onclick
                final String listUserId = getRef(i).getKey();

                // add value event listener to look for changes
                mUsersDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // get user details
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        // pass to viewholder
                        messagesViewHolder.setName(userName);
                        messagesViewHolder.setStatus(userStatus);
                        messagesViewHolder.setUserImage(userThumb, getContext());

                        // set onclick listener
                        messagesViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                // send user to chat activity and pass listUserId
                                Intent chatIntent = new Intent (getContext(), ChatActivity.class);
                                chatIntent.putExtra("userId", listUserId);
                                startActivity(chatIntent);

                            }//--end of onclick method

                        });//--end of onclick listener

                    }//--end of onDataChange method

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //..empty

                    }//--end of oncancelled method

                });//--end of addValueEventListener

            }//--end of populateViewHolder function

        };//--end of friendsRecyclerViewAdapter

        // set the adapter
        mMessagesList.setAdapter(messagesRecyclerViewAdapter);

    }//--end of onstart FUNCTION



    /**************************************ADDITIONAL CLASSES *************************************/
    /**
     * class that will manage the users view
     */
    public static class MessagesViewHolder extends RecyclerView.ViewHolder {

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
        public MessagesViewHolder(View itemView) {
            super(itemView);

            // initialize view
            mView = itemView;

        }//--end of CONSTRUCTOR


        /* Setters
        * ---------------------------------------------------*/
        /**
         * sets the date in the text view
         * @param date
         */
        public void setStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status_tv);
            userStatusView.setText(status);

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


    }//--end of MessagesViewHolder CLASS



}//--END OF FRAGMENT
