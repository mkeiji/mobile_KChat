package com.example.keiji.kchat;

/**
 * Users MODEL
 * will pull data from firebase db
 * Created by keiji on 28/11/17.
 */

public class Users {

    /* Properties
    * ---------------------------------------------------*/
    public String   image,
                    name,
                    status,
            thumb_image;


    /* CONSTRUCTOR
    * ---------------------------------------------------*/
    /**
     * EMPTY CONSTRUCTOR
     */
    public Users () {

    }//--end of empty CONSTRUCTOR


    /**
     * OVERLOADED CONSTRUCTOR - overloads the empty constructor if there is
     * parameters needed
     * @param image
     * @param name
     * @param status
     * @param thumb_image
     */
    public Users(String image, String name, String status, String thumb_image) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.thumb_image = thumb_image;
    }//--end of overload CONSTRUCTOR



    /* CONSTANTS
    * ---------------------------------------------------*/

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumbImage) {
        this.thumb_image = thumbImage;
    }
}//--end of CLASS
