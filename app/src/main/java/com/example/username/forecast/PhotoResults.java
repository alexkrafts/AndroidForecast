package com.example.username.forecast;

/**
 * Created by username on 10/5/2016.
 */
public class PhotoResults {
    private Photo[] photo;

    public Photo[] getPhoto ()
    {
        return photo;
    }

    public void setPhoto (Photo[] photo)
    {
        this.photo = photo;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [photo = "+photo+"]";
    }
}
