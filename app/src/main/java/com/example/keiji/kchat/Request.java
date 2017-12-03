package com.example.keiji.kchat;

/**
 * Created by keiji on 30/11/17.
 * Model for request to be used when retrieving the request_type from firebase adaptor
 */

public class Request {

    /* Properties
    * ---------------------------------------------------*/
    public String request_type;


    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    /**
     * EMPTY CONSTRUCTOR
     */
    public Request() {
        //..empty
    }//--end of empty CONSTRUCTOR


    /**
     * Overloaded CONSTRUCTOR
     * @param request_type
     */
    public Request(String request_type) {
        this.request_type = request_type;
    }//--end of overloaded CONSTRUCTOR



    /* Getters and Setters
    * ---------------------------------------------------*/

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

}//--end of MODEL
