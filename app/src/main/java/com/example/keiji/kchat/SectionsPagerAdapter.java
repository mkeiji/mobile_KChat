package com.example.keiji.kchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by keiji on 13/11/17.
 * Adapter for the view pager
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // tab view fragments
    RequestsFragment requestsFragment;
    ChatsFragment chatsFragment;
    FriendsFragment friendsFragment;


    /**
     * CONSTRUCTOR
     * @param fm
     */
    public SectionsPagerAdapter(FragmentManager fm) {

        super(fm);

    }//--end of CONSTRUCTOR

    /**
     * gets the item of the pager based on the item position
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {

        // giving action for each tab
        switch (position) {

            // request fragment
            case 0:
                chatsFragment = new ChatsFragment();
                return chatsFragment;

            // chats fragment
            case 1:
                friendsFragment = new FriendsFragment();
                return friendsFragment;

            // friends fragment
            case 2:
                requestsFragment = new RequestsFragment();
                return requestsFragment;

            // default
            default:
                return null;

        }

    }//--end getItem FUNCTION

    /**
     * returns the amount of fragmentson the tab
     * @return
     */
    @Override
    public int getCount() {

        // the getCount should return the same amount of number of tabs you have
        // in your tab view
        // since we have 3 tabs the count returns 3
        return 3;

    }//--end getCount FUNCTION


    /**
     * Set titles in the tabs
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {

            // requests tab
            case 0:
                return "Chat";

            // chat tab
            case 1:
                return "Paired";

            // friends tab
            case 2:
                return "Requests";

            // default
            default:
                return null;

        }

    }//--end getPageTitle FUNCTION

}//--end of CLASS
