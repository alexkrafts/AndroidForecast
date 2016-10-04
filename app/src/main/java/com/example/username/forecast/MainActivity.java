package com.example.username.forecast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MainActivity local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        local = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRss();
            }
        });
    }

    private void getRss() {

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://query.yahooapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            yahooForecastService service = retrofit.create(yahooForecastService.class);
            service.GetResponse().enqueue(ForecastCallback());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    private Callback<ForecastResponse> ForecastCallback() {
        return new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {

                List<Forecast> casts = response.body().query.getResults().getChannel().item.forecast;
                Log.d("forecast count", String.valueOf(casts.size()));

                ShowList(casts);
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void ShowList(List<Forecast> list) {
        ListView listView = (ListView) findViewById(R.id.listMainView);
        ForecastAdapter adapter = new ForecastAdapter(getApplicationContext(), list);
//        ArrayAdapter<Forecast> ad = new ArrayAdapter<Forecast>(local, R.layout.row_layout, R.id.text_main, list);
        listView.setAdapter(adapter);
        Log.e("List count", String.valueOf(listView.getCount()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings text", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface yahooForecastService {
        @GET("v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Saint%20Petersburg%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
        Call<ForecastResponse> GetResponse();
    }


    public class ForecastResponse {
        @SerializedName("query")
        public QueryResponse query;
    }

}

