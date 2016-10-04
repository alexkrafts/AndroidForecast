package com.example.username.forecast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by username on 10/3/2016.
 */
public class Item {
    public Guid guid;
    public String pubDate;
    public String title;
    public List<Forecast> forecast;
    public Condition condition;
    public String description;
    public String link;
    @SerializedName("long")
    public String longValue;
    public String lat;
}
