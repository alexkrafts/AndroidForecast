package com.example.username.forecast;

/**
 * Created by username on 10/5/2016.
 */
public class PhotoSearchResponse {
    private PhotoQuery query;

    public PhotoQuery getQuery ()
    {
        return query;
    }

    public void setQuery (PhotoQuery query)
    {
        this.query = query;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [query = "+query+"]";
    }
}
