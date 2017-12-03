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
public class RequestsFragment extends Fragment {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_ReqFrag";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // act recycle view
    private RecyclerView mRequestsList;

    // firebase db references
    private DatabaseReference mRequestsDatabase;
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
     * Empty CONSTRUCTOR
     */
    public RequestsFragment() {
        // Required empty public constructor
    }//--end of empty CONSTRUCTOR

    /**
     * CONSTRUCTOR
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        // initialize UI elements
        // recycler view
        mRequestsList = (RecyclerView) mMainView.findViewById(R.id.requests_list);

        // user auth (store in the current user ID container)
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // firebase db references
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // recycler view settings
        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

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
        FirebaseRecyclerAdapter<Request, RequestsViewHolder> requestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Request, RequestsViewHolder>(

                Request.class,
                R.layout.users_single_layout,
                RequestsViewHolder.class,
                mRequestsDatabase

        ) {

            @Override
            protected void populateViewHolder(final RequestsViewHolder requestsViewHolder, Request requests, int i) {

                // pass request type to the view holder
                requestsViewHolder.setRequestType(requests.getRequest_type());

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
                        requestsViewHolder.setName(userName);
                        requestsViewHolder.setUserImage(userThumb, getContext());

                        // set onclick listener
                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                // send user to profile activity and pass listUserId
                                Intent profileIntent = new Intent (getContext(), ProfileActivity.class);
                                profileIntent.putExtra("userId", listUserId);
                                startActivity(profileIntent);

                            }//--end of onclick method

                        });//--end of onclick listener

                    }//--end of ondatachange method

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //..empty

                    }//--end of oncancelled method

                });//--end of value event listener on musersdb

            }//--end of populateviewholder method

        };//--end of firebaseRecyclerAdapter

        // set the adapter
        mRequestsList.setAdapter(requestsRecyclerViewAdapter);

    }//--end of onstart FUNCTION


    /**************************************ADDITIONAL CLASSES *************************************/

    /**
     * class that will manage the users view
     */
    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

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
        public RequestsViewHolder(View itemView) {

            super(itemView);

            // initialize view
            mView = itemView;

        }//--end of CONSTRUCTOR



        /* setters
        * ---------------------------------------------------*/
        /**
         * sets the Request Type in the text view
         * @param requestType
         */
        public void setRequestType (String requestType){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status_tv);
            userStatusView.setText(requestType);

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


    }//--end of RequestsViewHolder nested CLASS

}//--END OF FRAGMENT
