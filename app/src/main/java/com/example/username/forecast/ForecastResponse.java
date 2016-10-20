package com.example.username.forecast;

import com.google.gson.annotations.SerializedName;

public class ForecastResponse {
    @SerializedName("query")
    public QueryResponse query;
}
