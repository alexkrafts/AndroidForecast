package com.example.username.forecast;

import com.google.gson.annotations.SerializedName;

/**
 * Created by username on 10/3/2016.
 */
public class Item {
    private Guid guid;

    private String pubDate;

    private String title;

    private Forecast[] forecast;

    private Condition condition;

    private String description;

    private String link;

    @SerializedName("long")
    private String longValue;

    private String lat;
}
