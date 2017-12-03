package com.example.keiji.kchat;

/**
 * Messages MODEL
 * will pull data from firebase db
 * Created by keiji on 02/12/17.
 */

public class Messages {

    /* Properties
    * ---------------------------------------------------*/
    public String   message, type, from;
    public long time;


    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    /**
     * EMPTY CONSTRUCTOR
     */
    public Messages() {
        //..empty
    }//--end of empty CONSTRUCTOR


    /**
     * Overloaded CONSTRUCTOR
     * @param message
     * @param type
     * @param from
     * @param time
     */
    public Messages(String message, String type, String from, long time) {

        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;

    }//--end of overloaded CONSTRUCTOR


    /* Getters & Setters
    * ---------------------------------------------------*/

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}//--end of messages MODEL CLASS
