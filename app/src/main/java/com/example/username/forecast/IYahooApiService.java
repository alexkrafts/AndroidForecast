package com.example.username.forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by username on 10/12/2016.
 */
public interface IYahooApiService {
    @GET("v1/public/yql")
    Call<ForecastResponse> CallApi(@Query(value = "q", encoded = true) String query,
                                                @Query(value = "format", encoded = true) String format,
                                                @Query(value = "env", encoded = true) String env);

    @GET("v1/public/yql")
    Call<PhotoSearchResponse> CallStringApi(@Query(value = "q", encoded = true) String query,
                                            @Query(value = "format", encoded = true) String format,
                                            @Query(value = "env", encoded = true) String env);
}
