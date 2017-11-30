package com.example.keiji.kchat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;


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
    private RecyclerView mReqList;
    // firebase db reference
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

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
        return inflater.inflate(R.layout.fragment_requests, container, false);

        // point the db reference to inside the user obj in the db

    }//--end of CONSTRUCTOR

}//--END OF FRAGMENT
