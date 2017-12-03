package com.example.keiji.kchat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the Messages class
 * needs to extends Recycler view adapter
 * Created by keiji on 02/12/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    /* Properties
    * ---------------------------------------------------*/
    // list to hold the messages
    private List<Messages> mMessageList;

    // firebase auth ref for user
    FirebaseAuth mAuth;

    // database reference for the user
    private DatabaseReference mUsersDatabase;



    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }//--end of CONSTRUCTOR



    /* Functions
    * ---------------------------------------------------*/

    /**
     * view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // add the custom layout for single msg
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }//--end of onCreateViewHolder FUNCTION


    /**
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        // initialize mauth to get user info
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        // gets the message and the sender
        Messages message = mMessageList.get(i);
        String fromUser = message.getFrom();
        long longMessageDate = message.getTime();
        String strMessageDate = convertLongDate(longMessageDate);

        // check if the message is from current user
        if (fromUser.equals(currentUserId)) {

            // change text color to blue
            viewHolder.messageText.setTextColor(Color.BLUE);

        }
        // else it is from the other user
        else {

            // change text color to Red
            viewHolder.messageText.setTextColor(Color.RED);

        }

        // - get from user data -
        // initialize the db ref and point to the right child
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);
        // put display name and image in view holder
        mUsersDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                // set displayname and image on viewholder
                viewHolder.displayName.setText(name);
                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }//--end of onDataChange method

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //..empty for now

            }//--end of oncancelled method

        });//--end of addValueEventListener

        // put message in the view holder
        viewHolder.messageText.setText(message.getMessage());
        // put date in the view holder
        viewHolder.timeText.setText(strMessageDate);

    }//--end of onBindViewHolder FUNCTION


    /**
     * returns the number of items in the message list
     * @return
     */
    @Override
    public int getItemCount() {

        return mMessageList.size();

    }//--end of getItemCount FUNCTION


    /**
     * Converts long values to date format
     * @param time
     * @return
     */
    private String convertLongDate (long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;

    }//--end of convertLongDate FUNCTION


    /**************************************ADDITIONAL CLASSES *************************************/

    /**
     * class that will manage the messages view
     */
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        /* Properties
        * ---------------------------------------------------*/
        public CircleImageView profileImage;
        public TextView messageText,
                        displayName,
                        timeText;
//        public ImageView messageImage;


        /* CONSTRUCTOR
        * ---------------------------------------------------*/

        /**
         * CONSTRUCTOR
         * @param view
         */
        public MessageViewHolder(View view) {
            super(view);

            displayName = (TextView) view.findViewById(R.id.name_text_layout_tv);
            timeText = (TextView) view.findViewById(R.id.time_text_layout_tv);
            messageText = (TextView) view.findViewById(R.id.message_text_layout_tv);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout_civ);
//            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }//--end of CONSTRUCTOR

    }//--end of messageViewHolder nested CLASS




}//--end of messageadapter ADAPTER CLASS
