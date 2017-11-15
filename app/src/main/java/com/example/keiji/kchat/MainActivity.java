package com.example.keiji.kchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_MAIN";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // firebase authentication object
    private FirebaseAuth mAuth;

    // toolbar object
    private Toolbar mToolbar;

    // for TABS
    // view pager (obs: needs a view pager adaptor)
    private ViewPager mViewPager;
    // view pager adaptor:
    private SectionsPagerAdapter mSectionsPagerAdapter;
    // tab layout
    private TabLayout mTabLayout;


    /* FUNCTIONS
    * ---------------------------------------------------*/
    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing the firebase authentication object
        mAuth = FirebaseAuth.getInstance();

        // initializing the toolbar
        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KChat");

        // initializing tabs
        // initializing view pager
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        // initializing section pager adapter (pass getSupportFragmentManager())
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // set the adapter in the viewpager and pass the created adapter
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // reference the tab layout
        mTabLayout = (TabLayout) findViewById(R.id.main_tab_bar);
        // set the tablayout in the view pager
        mTabLayout.setupWithViewPager(mViewPager);

    }//--end of CONSTRUCTOR


    /**
     * onStart function runs on aplication start
     */
    @Override
    public void onStart() {

        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // note: if the user is not signed in, the currentUser var will be null
        if (currentUser == null) {

            // send user to the login page
            mySendUserToStartActivity();

        }

    }//--end of onstart FUNCTION


    /**
     * Creates an intent and send the user to the start activity
     * killing the current one disabling the ability to go back
     * by using the back button
     */
    private void mySendUserToStartActivity() {

        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        // call finish so the user cannot use the back button
        finish();

    }//--end of mySendUserToStartActivity FUNCTION


    /**
     * inflates the main_menu.xml resource file inside the toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;

    }//--end of the oncreateoptionsmenu FUNCTION


    /**
     * event listener for the buttons in the menu in the toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.main_logout_btn:
                // logout using firebase auth code
                FirebaseAuth.getInstance().signOut();
                mySendUserToStartActivity();
                break;

            case R.id.main_settings_btn:
                // go to settings acctivity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }//switch

        return true;

    }//--end of the onOptionsItemSelected FUNCTION

}//--end of MAIN ACTIVITY
