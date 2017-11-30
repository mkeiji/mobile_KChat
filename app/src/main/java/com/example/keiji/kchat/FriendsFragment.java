package com.example.keiji.kchat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

    @Override
    public void onStart() {
        super.onStart();

        // create a firebase recycler adapter
        // note: needs a friends obj and a friendsviewholder (create it manually)

    }//--end of onstart FUNCTION


    /**************************************ADDITIONAL CLASSES *************************************/

    /**
     * class that will manage the users view
     */


}//--END OF FRAGMENT
