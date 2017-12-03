package com.example.keiji.kchat;

/**
 * Created by keiji on 30/11/17.
 * Model for friends to be used when retrieving the date from firebase adaptor
 */

public class Friends {

    /* Properties
    * ---------------------------------------------------*/
    public String date;


    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    /**
     * EMPTY CONSTRUCTOR
     */
    public Friends() {

    }//--end of empty CONSTRUCTOR

    /**
     * overload CONSTRUCTOR
     * @param date
     */
    public Friends(String date) {
        this.date = date;
    }//--end of CONSTRUCTOR


    /* Getters and Setters
    * ---------------------------------------------------*/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}//--END OF CLASS
