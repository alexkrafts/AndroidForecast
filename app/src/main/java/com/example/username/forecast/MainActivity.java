package com.example.username.forecast;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.*;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import me.itangqi.waveloadingview.WaveLoadingView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MainActivity local;
    private final String ENV_STRING = "store://datatables.org/alltableswithkeys";
    private final String FORECAST_QUERY_TEMPLATE = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"(%s,%s)\")  and u='c'";
    private final String PHOTO_SEARCH_QUERY_TEMPLATE = "select * from flickr.photos.search where text=\"city\" and lat=\"%s\" and lon=\"%s\" and api_key=\"a2cfa360a1b6be0640025d8277b2859b\" ";

    private WaveLoadingView progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        local = this;
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);
        listView = (RecyclerView) findViewById(R.id.listMainView);
        progressBar = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        imageView = (ImageView) findViewById(R.id.imageView);
        fadeIn = AnimationUtils.loadAnimation(local, R.anim.fade_in);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        getGps();
    }


    public void getBackground() {
        lm.removeUpdates(ls);
        //Toast.makeText(getApplicationContext(), "getBackground", Toast.LENGTH_LONG).show();
        progressBar.setProgressValue(33);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IYahooApiService service = retrofit.create(IYahooApiService.class);
        service.CallStringApi(String.format(PHOTO_SEARCH_QUERY_TEMPLATE, _location.getLatitude(), _location.getLongitude()), "json", ENV_STRING)
                .enqueue(new Callback<PhotoSearchResponse>() {
                    @Override
                    public void onResponse(Call<PhotoSearchResponse> call, Response<PhotoSearchResponse> response) {
                        //Toast.makeText(getApplicationContext(), "Image received", Toast.LENGTH_LONG).show();
                        progressBar.setProgressValue(66);
                        getForecast();
                        setBackground(response);
                    }

                    @Override
                    public void onFailure(Call<PhotoSearchResponse> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });

    }
    ImageView imageView;
    Animation fadeIn;

    private void setBackground(Response<PhotoSearchResponse> response) {
        Random r = new Random();
        Photo[] list = response.body().getQuery().getResults().getPhoto();


        new PhotoLoader().execute(list[r.nextInt(list.length)], getResources());


    }

    private class PhotoLoader extends AsyncTask<Object, Void, BitmapDrawable> {

        private final String PHOTO_URL_TEMPLATE = "https://farm%s.staticflickr.com/%s/%s_%s_h.jpg";
        @Override
        protected BitmapDrawable doInBackground(Object... params) {
            Photo src = (Photo) params[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(String.format(PHOTO_URL_TEMPLATE, src.getFarm(), src.getServer(), src.getId(), src.getSecret()));
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }


            return new BitmapDrawable(getResources(), bitmap);

        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            super.onPostExecute(bitmapDrawable);
            imageView.setImageDrawable(bitmapDrawable);
            imageView.startAnimation(fadeIn);
            new Animator(imageView).startMoving(bitmapDrawable);
        }
    }


    LocationManager lm;
    LocationListener ls;
    private void getGps() {
        //Toast.makeText(getApplicationContext(), "getGps", Toast.LENGTH_LONG).show();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = getProviderName();
        ls = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (location!=null) {
                    _location = location;
                    getBackground();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
//        lm.requestLocationUpdates(provider, 0, 0, ls);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ls);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ls);
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc!=null) {
            _location = loc;
            getBackground();
        }



    }
    String getProviderName() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)
        return lm.getBestProvider(criteria, true);
    }




    private Location _location;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (locationAvailable()) {
            getGps();
        }

    }

    private boolean locationAvailable() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void getForecast() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IYahooApiService service = retrofit.create(IYahooApiService.class);
        service.CallApi(String.format(FORECAST_QUERY_TEMPLATE, _location.getLatitude(), _location.getLongitude()), "json", ENV_STRING)
                .enqueue(ForecastCallback());

    }

    @NonNull
    public Callback<ForecastResponse> ForecastCallback() {
        return new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                //Toast.makeText(getApplicationContext(), "ForecastCallback", Toast.LENGTH_LONG).show();
                progressBar.setProgressValue(100);
                Hide(progressBar);
                Results res = response.body().query.getResults();
                if (res != null) {
                    Channel casts = res.getChannel();
                    ShowList(casts);
                } else {
                    Crashlytics.log("null forecast response request - " + call.request().body().toString());
                }


            }



            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Crashlytics.logException(t);
            }
        };
    }
    private void Hide(final View view) {
        Animation fadeOut = AnimationUtils.loadAnimation(local, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeOut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    RecyclerView listView;

    private void ShowList(Channel channel) {
        ArrayList<Object> source = new ArrayList<Object>();
        source.add(channel.location);
        source.add(channel.item.condition);
        source.addAll(channel.item.forecast);
        ForecastAdapter adapter = new ForecastAdapter(getApplicationContext(), source);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {

        imageView.clearAnimation();
        listView.setVisibility(View.GONE);
        imageView.setImageDrawable(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //Toast.makeText(this, "Settings text", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}

