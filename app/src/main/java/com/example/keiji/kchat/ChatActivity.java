package com.example.keiji.kchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_CHAT";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // user id
    private String mChatUser;

    // UI elements
    private Toolbar mChatToolbar;
    private TextView mTitleView;
    private CircleImageView mProfileImage;
    private ImageButton mChatAddBtn,
                        mChatSendBtn;
    private EditText mChatMessageView;

    // - recycler view for messages -
    // recycler view
    private RecyclerView mMessagesList;
    // container for messages
    private final List<Messages> messagesList = new ArrayList<>();
    // linear layout manager (for the recycler view)
    private LinearLayoutManager mLinearLayout;
    // message adapter (for recycler view)
    private MessageAdapter mAdapter;

    // firebase db references
    private DatabaseReference mRootRef;

    // firebase auth ref (to get current userid)
    private FirebaseAuth mAuth;
    private String mCurrentUserId;



    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get current user id
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // get user id and name from the intent of Friends Fragment
        mChatUser = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        // - initialize elements -
        // firebase db reference
        mRootRef = FirebaseDatabase.getInstance().getReference();

        // toolbar
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        // define the custom actionbar (chat_custom_bar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // add custom view to the action bar
        actionBar.setDisplayShowCustomEnabled(true);

        // - recycler view to hold messages -
        // initialize adapter for recycler view
        mAdapter = new MessageAdapter(messagesList);
        // initiate the recycler view and linear layout manager
        mMessagesList = (RecyclerView) findViewById(R.id.messages_list_rv);
        mLinearLayout = new LinearLayoutManager(this);
        // recycler view config
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        // set the adapter
        mMessagesList.setAdapter(mAdapter);
        // load the messages (call custom function)
        loadMessages();

        // - bottom bar -
        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view_et);

        // inflate the layout in a view
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

        // put the action bar in the created view
        actionBar.setCustomView(actionBarView);

        // after inflating, populate the custom action bar
        // initialize tv and civ from the custom action bar
        mTitleView = (TextView) findViewById(R.id.custom_bar_title_tv);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image_civ);
        // - populate -
        mTitleView.setText(userName);
        // get image
        mRootRef.child("Users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get image
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                // load image
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

            }//--end of onDataChange method

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//--end of oncancelled method

        });//--end of addListenerForSingleValueEvent

        // add click listener to the send btn
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sendMessage();

            }//--end of onclick method

        });//--end of setOnClickListener



    }//--end of CONSTRUCTOR

    /**
     * retrieves the messages from the db
     */
    private void loadMessages() {

        // point db to messages obj current user
        // use childeventlistener since we need to manipulate it
        mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // note: all the messages will be in datasnapshot
                // get messages
                Messages message = dataSnapshot.getValue(Messages.class);

                // add message to msg list
                messagesList.add(message);

                // notify on change
                mAdapter.notifyDataSetChanged();

            }//--end of onChildAdded method

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }//--end of onChildChanged method

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }//--end of onChildRemoved method

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }//--end of onChildMoved method

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//--end of oncancelled method

        });//--end of addChildEventListener

    }//--end of loadMessages FUNCTION


    /**
     * store string message in the db
     */
    private void sendMessage() {

        // get message from the et view
        String message = mChatMessageView.getText().toString();

        // db references string (store in the messages table and create an entry for both users)
        String currentUserRef = "messages" + "/" + mCurrentUserId + "/" + mChatUser;
        String otherUserRef   = "messages" + "/" + mChatUser + "/" + mCurrentUserId;

        // - get the message push id (message id) -
        // db ref
        DatabaseReference userMessagePush = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
        String pushId = userMessagePush.getKey();

        // CTE - check if the text is not empty
        if (!TextUtils.isEmpty(message)) {

            // add message to db if there is one
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type"   , "text");
            messageMap.put("time"   , ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            // create a map of the users to store the messagemap above (for both users in the chat)
            // note: needs to pass the push id
            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(otherUserRef + "/" + pushId, messageMap);

            // clear the edit text field
            mChatMessageView.setText("");

            // store the msg
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    // check for errors
                    if (databaseError != null) {

                        Log.d(TAG, databaseError.getMessage().toString());

                    }

                }//--end of oncomplete method

            });//--end of updateChildren

        }//--end of CTE

    }//--end of sendmessage FUNCTION


}//--end of ACTIVITY
