package com.example.username.forecast;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity implements LocationListener  {
    private MainActivity local;
    private final String ENV_STRING = "store://datatables.org/alltableswithkeys";
    private final String FORECAST_QUERY_TEMPLATE = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")";
    private final String PHOTO_SEARCH_QUERY_TEMPLATE = "select * from flickr.photos.search where text=\"city\" and lat=\"%s\" and lon=\"%s\" and api_key=\"a2cfa360a1b6be0640025d8277b2859b\" ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        local = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listMainView);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getGps();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast();
            }
        });

    }

    private void getBackground() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://query.yahooapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IYahooApiService service = retrofit.create(IYahooApiService.class);
            service.CallStringApi(String.format(PHOTO_SEARCH_QUERY_TEMPLATE, _location.getLatitude(), _location.getLongitude()), "json", ENV_STRING)
                    .enqueue(new Callback<PhotoSearchResponse>() {
                        @Override
                        public void onResponse(Call<PhotoSearchResponse> call, Response<PhotoSearchResponse> response) {

                            Random r = new Random();
                            Photo[] list = response.body().getQuery().getResults().getPhoto();
                            Photo src = list[r.nextInt(list.length)];
                            Bitmap bmp = null;
                            try {
                                bmp = new PhotoLoader().execute(src).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
//                            ob.setGravity(Gravity.CENTER|Gravity.LEFT|Gravity.CLIP_HORIZONTAL);
                            ImageView iv = (ImageView) findViewById(R.id.imageView);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                Animation fadeIn = AnimationUtils.loadAnimation(local, R.anim.fade_in);
                                iv.setImageDrawable(ob);
                                iv.startAnimation(fadeIn);
                                startMoving(ob);
                            }
                        }

                        @Override
                        public void onFailure(Call<PhotoSearchResponse> call, Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final int RightToLeft = 1;
    private static final int LeftToRight = 2;
    private static final int DURATION = 500;

    private ValueAnimator mCurrentAnimator;
    private final Matrix mMatrix = new Matrix();
    private ImageView mImageView;
    private float mScaleFactor;
    private int mDirection = RightToLeft;
    private RectF mDisplayRect = new RectF();

    private void startMoving(final Drawable drawable) {
        mImageView = (ImageView) findViewById(R.id.imageView);

        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mScaleFactor = (float)  mImageView.getHeight() / (float) drawable.getIntrinsicHeight();
                mMatrix.postScale(mScaleFactor, mScaleFactor);
                mImageView.setImageMatrix(mMatrix);
                animate();
            }
        });
    }
    private void animate() {
        updateDisplayRect();
        if(mDirection == RightToLeft) {
            animate(mDisplayRect.left, mDisplayRect.left - (mDisplayRect.right - mImageView.getWidth()));
        } else {
            animate(mDisplayRect.left, 0.0f);
        }
    }

    private void animate(float from, float to) {
        mCurrentAnimator = ValueAnimator.ofFloat(from, to);
        mCurrentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();

                mMatrix.reset();
                mMatrix.postScale(mScaleFactor, mScaleFactor);
                mMatrix.postTranslate(value, 0);

                mImageView.setImageMatrix(mMatrix);

            }
        });
        mCurrentAnimator.setDuration(DURATION);
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mDirection == RightToLeft)
                    mDirection = LeftToRight;
                else
                    mDirection = RightToLeft;

                animate();
            }
        });
        mCurrentAnimator.setDuration(35000);
        mCurrentAnimator.start();
    }

    private void updateDisplayRect() {
        try {
            mDisplayRect.set(0, 0, mImageView.getDrawable().getIntrinsicWidth(), mImageView.getDrawable().getIntrinsicHeight());
            mMatrix.mapRect(mDisplayRect);
        }
        catch (Exception e){

        }
    }

    private static final int INITIAL_REQUEST=1337;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private void getGps() {
        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (!locationAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                return;
            }
        }
        Toast.makeText(local, "Gps access granted", Toast.LENGTH_LONG);
        _location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (_location!=null && _location.getAccuracy()<100)
        {
            getBackground();
            getForecast();

        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }


    private Location _location;
    @Override
    public void onLocationChanged(Location location) {
        _location = location;

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



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (locationAvailable()) {
            getGps();
        }

    }

    private boolean locationAvailable(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }




    private void getForecast() {

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://query.yahooapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IYahooApiService service = retrofit.create(IYahooApiService.class);
            service.CallApi(String.format(FORECAST_QUERY_TEMPLATE, "Saint Petersburg"), "json", ENV_STRING)
                    .enqueue(ForecastCallback());

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
    ListView listView;
    private void ShowList(List<Forecast> list) {
        ForecastAdapter adapter = new ForecastAdapter(getApplicationContext(), list);
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


    public class ForecastResponse {
        @SerializedName("query")
        public QueryResponse query;
    }

}

