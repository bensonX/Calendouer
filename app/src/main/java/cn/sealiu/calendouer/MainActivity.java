package cn.sealiu.calendouer;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.sealiu.calendouer.adapter.MovieComingSoonAdapter;
import cn.sealiu.calendouer.adapter.MovieInTheatersAdapter;
import cn.sealiu.calendouer.bean.CelebrityBean;
import cn.sealiu.calendouer.bean.DoubanMovies;
import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;
import cn.sealiu.calendouer.bean.XzBean;
import cn.sealiu.calendouer.bean.XzLocationBean;
import cn.sealiu.calendouer.bean.XzResultsBean;
import cn.sealiu.calendouer.bean.XzWeatherBean;
import cn.sealiu.calendouer.fragment.MovieFragment;
import cn.sealiu.calendouer.fragment.WeatherFragment;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.FestivalCalendar;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract.MovieEntry;
import cn.sealiu.calendouer.until.SolarTermCalendar;
import cn.sealiu.calendouer.until.WeatherIcon;

import static android.Manifest.permission;

public class MainActivity extends CalendouerActivity implements
        AMapLocationListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        WeatherFragment.UpdateWeatherListener {

    Toolbar toolbar;
    TextView monthTV;
    TextView weekTV;
    TextView lunarTV;
    TextView dateTV;
    TextView solarTermTV;
    TextView festivalTV;
    RelativeLayout weatherHolder;
    AppCompatButton getWeatherBtn;
    TextView cityNameTV;
    TextView weatherTV;
    ImageView weatherIconIV;
    ImageView movieCardCover;
    ImageView movieImageIV;
    TextView movieAverageTV;
    TextView movieTitleTV;
    TextView movieSummaryTV;
    LinearLayout starsHolderLL;
    AppCompatButton getTop250Btn;
    ProgressDialog mProgressDialog;
    DBHelper dbHelper;
    SQLiteDatabase db;
    WeatherIcon icons;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    LocationManager locationMgr;
    CollapsingToolbarLayout collapsingToolbarLayout;
    NestedScrollView nestedScrollView;
    View progressOfDay;
    TextView directorTV, castsTV1, castsTV2, genresTV1, genresTV2, sameNameTV;

    LinearLayout weatherCard, movieCard, todayMovieCard,
            inTheatersCard, comingSoonCard, youMayLikeCard;
    LinearLayout ratingUs;
    NativeExpressAdView adView;

    RecyclerView inTheatersRecyclerHolder;
    RecyclerView comingSoonRecyclerHolder;

    int color, colorDark;
    private int festival = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weatherCard = (LinearLayout) findViewById(R.id.weather_card);
        movieCard = (LinearLayout) findViewById(R.id.movie_card);
        todayMovieCard = (LinearLayout) findViewById(R.id.today_movie_card);
        inTheatersCard = (LinearLayout) findViewById(R.id.in_theaters_card);
        comingSoonCard = (LinearLayout) findViewById(R.id.coming_soon_card);
        youMayLikeCard = (LinearLayout) findViewById(R.id.you_may_like_card);
        ratingUs = (LinearLayout) findViewById(R.id.rating_us_card);
        adView = (NativeExpressAdView) findViewById(R.id.adView);

        inTheatersRecyclerHolder = (RecyclerView) findViewById(R.id.in_theaters_recycler_holder);
        comingSoonRecyclerHolder = (RecyclerView) findViewById(R.id.coming_soon_recycler_holder);
        inTheatersRecyclerHolder.setNestedScrollingEnabled(false);
        comingSoonRecyclerHolder.setNestedScrollingEnabled(false);

        directorTV = (TextView) findViewById(R.id.director);
        castsTV1 = (TextView) findViewById(R.id.casts_1);
        castsTV2 = (TextView) findViewById(R.id.casts_2);
        genresTV1 = (TextView) findViewById(R.id.genres_1);
        genresTV2 = (TextView) findViewById(R.id.genres_2);
        sameNameTV = (TextView) findViewById(R.id.same_name);

        progressOfDay = findViewById(R.id.progress_day);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nest_scroll_view);

        monthTV = (TextView) findViewById(R.id.month);
        weekTV = (TextView) findViewById(R.id.week_day);
        lunarTV = (TextView) findViewById(R.id.lunar_date);
        dateTV = (TextView) findViewById(R.id.date);
        solarTermTV = (TextView) findViewById(R.id.solar_term);
        festivalTV = (TextView) findViewById(R.id.festival);
        weatherHolder = (RelativeLayout) findViewById(R.id.weatherHolder);
        getWeatherBtn = (AppCompatButton) findViewById(R.id.getWeatherInfo);
        cityNameTV = (TextView) findViewById(R.id.city_name);
        weatherTV = (TextView) findViewById(R.id.weather);
        weatherIconIV = (ImageView) findViewById(R.id.weather_icon);
        weatherIconIV.setOnClickListener(this);

        movieCardCover = (ImageView) findViewById(R.id.movie_card_cover);
        movieImageIV = (ImageView) findViewById(R.id.movie_image);
        movieAverageTV = (TextView) findViewById(R.id.rating__average);
        movieTitleTV = (TextView) findViewById(R.id.movie_title);
        movieSummaryTV = (TextView) findViewById(R.id.movie_summary);
        starsHolderLL = (LinearLayout) findViewById(R.id.rating__stars_holder);

        getTop250Btn = (AppCompatButton) findViewById(R.id.getTop250_btn);
        dbHelper = new DBHelper(this);
        icons = new WeatherIcon();
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        settingPref.registerOnSharedPreferenceChangeListener(this);
        locationMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        color = ContextCompat.getColor(this, R.color.colorPrimary);
        colorDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);

        findViewById(R.id.after_ad_closed_rate).setOnClickListener(this);
        findViewById(R.id.after_ad_closed_restore).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setCustomTheme(color, colorDark, collapsingToolbarLayout);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationClient.setLocationOption(mLocationOption);

        //Calendar
        initCalendar();

        //Weather
        if (settingPref.getBoolean("weather_show", true)) {
            weatherHolder.setVisibility(View.VISIBLE);
            initWeather();
        } else {
            weatherHolder.setVisibility(View.GONE);
            restoreTheme();
        }

        //Movie Recommendation
        if (checkEmpty(dbHelper, MovieEntry.TABLE_NAME)) {
            movieCard.setVisibility(View.VISIBLE);
            todayMovieCard.setVisibility(View.GONE);
            getTop250Btn.setOnClickListener(this);
        } else {
            if (settingPref.getBoolean("movie_recommended_show", true)) {
                todayMovieCard.setVisibility(View.VISIBLE);

                String datePref = sharedPref.getString("DATE", "null");
                String idPref = sharedPref.getString("ID", "null");

                if (datePref.equals("") || idPref.equals("")) {
                    setMovieInfoRandom();
                } else {
                    if (!datePref.equals(df_ymd.format(new Date()))) {
                        //new day
                        db = dbHelper.getWritableDatabase();
                        db.delete(
                                MovieEntry.TABLE_NAME,
                                MovieEntry.COLUMN_NAME_ID + "=?",
                                new String[]{idPref}
                        );
                        setMovieInfoRandom();
                    } else {
                        //same day
                        setMovieInfoRepeat(idPref);
                    }
                }
            } else {
                todayMovieCard.setVisibility(View.GONE);
            }
        }

        //Movie in_theaters
        if (settingPref.getBoolean("movie_in_theaters", true)) {
            inTheatersCard.setVisibility(View.VISIBLE);
            initMovieInTheaters();
        } else {
            inTheatersCard.setVisibility(View.GONE);
        }

        //Movie coming soon
        if (settingPref.getBoolean("movie_coming_soon", true)) {
            comingSoonCard.setVisibility(View.VISIBLE);
            initMovieComingSoon();
        } else {
            comingSoonCard.setVisibility(View.GONE);
        }

        // TODO: 2017/4/18 you may like card init
        if (settingPref.getBoolean("movie_you_may_like", true)) {
            youMayLikeCard.setVisibility(View.VISIBLE);
        } else {
            youMayLikeCard.setVisibility(View.GONE);
        }

        setProgressInPd(progressOfDay);

        initAd();
    }

    private void initAd() {
        if (settingPref.getBoolean("ad_show", true)) {
            adView.setVisibility(View.VISIBLE);
            ratingUs.setVisibility(View.GONE);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("43FE98603DD8DD9E449808D85C7DBD45")
                    .build();
            adView.loadAd(request);
        } else {
            adView.setVisibility(View.GONE);
            ratingUs.setVisibility(View.VISIBLE);
        }
    }

    private void initCalendar() {

        Date now = new Date();

        List<String> solarCalendarStrs = LunarCalendar.getLunarCalendarStr(now);

        monthTV.setText(solarCalendarStrs.get(6));

        weekTV.setText(solarCalendarStrs.get(4));
        lunarTV.setText(
                String.format(
                        getResources().getString(R.string.lunar_date),
                        solarCalendarStrs.get(1),
                        solarCalendarStrs.get(2)
                )
        );

        dateTV.setText(solarCalendarStrs.get(8));

        // set solar term
        String str = SolarTermCalendar.getSolarTermStr(now);
        if (str != null) {
            solarTermTV.setVisibility(View.VISIBLE);
            solarTermTV.setText(str);
        } else {
            solarTermTV.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        // fest
        String solarFestStr = FestivalCalendar.getSolarFest(calendar);
        String lunarFestStr = FestivalCalendar.getLunarFest(calendar);
        calendar.setTime(now);
        String weekFestStr = FestivalCalendar.getWeekFest(calendar);

        String festStr = "";
        if (solarFestStr != null) {
            festStr += solarFestStr;
        }
        if (lunarFestStr != null) {
            festStr += " " + lunarFestStr;
        }
        if (weekFestStr != null) {
            festStr += " " + weekFestStr;
        }
        if (!festStr.equals("")) {
            festivalTV.setVisibility(View.VISIBLE);
            festivalTV.setText(festStr);
            color = ContextCompat.getColor(this, R.color.tomato);
            colorDark = ContextCompat.getColor(this, R.color.tomatoDark);
            setCustomTheme(color, colorDark, collapsingToolbarLayout);
            festival = 1;
        } else {
            festivalTV.setVisibility(View.GONE);
            festival = 0;
        }
    }

    private void initWeather() {

        if (!checkLocationPermission()) {

            weatherCard.setVisibility(View.VISIBLE);
            weatherHolder.setVisibility(View.GONE);

            getWeatherBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.permission))
                            .setMessage(getString(R.string.rationale_location))
                            .setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(
                                            MainActivity.this,
                                            new String[]{
                                                    permission.ACCESS_FINE_LOCATION,
                                                    permission.ACCESS_COARSE_LOCATION,
                                                    permission.ACCESS_NETWORK_STATE,
                                                    permission.ACCESS_WIFI_STATE,
                                                    permission.CHANGE_WIFI_STATE,
                                                    permission.INTERNET
                                            },
                                            LOCATION_PERM
                                    );
                                }
                            })
                            .setNegativeButton(getString(R.string.deny), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            });
        } else {
            weatherCard.setVisibility(View.GONE);
            if (sharedPref.getString("weather_json", "").equals("")) {
                getWeather();
            } else {
                if (needUpdateWeather()) {
                    getWeather();
                } else {
                    setWeather();
                }
            }
        }
    }

    private void initMovieDB() {
        showProgressDialog(getResources().getString(R.string.downloading));
        int start = sharedPref.getInt("START", 0);
        new GetTop250().execute("https://api.douban.com/v2/movie/top250?start=" + start + "&count=" + MAX_COUNT);
    }

    private void initMovieInTheaters() {
        String inTheatersStr = sharedPref.getString("in_theaters", "");
        if (inTheatersStr.equals("")) {
            new GetInTheaters().execute("https://api.douban.com/v2/movie/in_theaters?count=10");
        } else {
            DoubanMovies movies = new Gson().fromJson(inTheatersStr, DoubanMovies.class);
            setMovieInTheaters(movies);
        }
    }

    private void initMovieComingSoon() {
        String comingSoonStr = sharedPref.getString("coming_soon", "");
        if (comingSoonStr.equals("")) {
            new GetComingSoon().execute("https://api.douban.com/v2/movie/coming_soon?count=10");
        } else {
            DoubanMovies movies = new Gson().fromJson(comingSoonStr, DoubanMovies.class);
            setMovieComingSoon(movies);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherBtn.setOnClickListener(null);

                mLocationClient.startLocation();
            } else {
                getWeatherBtn.setText(getString(R.string.need_location_premission));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals("Latitude") || key.equals("Longitude") &&
                sharedPref.getString("weather_json", "").equals("")) {
            getWeather();
        }

        if (key.equals("update_frequency") && needUpdateWeather()) {
            getWeather();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                String lat = aMapLocation.getLatitude() + "";
                String lng = aMapLocation.getLongitude() + "";

                sharedPref.edit().putString("Latitude", lat).apply();
                sharedPref.edit().putString("Longitude", lng).apply();

            } else {
                displaySnackBar(
                        nestedScrollView,
                        aMapLocation.getErrorInfo(),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLocationClient.startLocation();
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("color", color);
            intent.putExtra("colorDark", colorDark);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getWeather() {
        Log.d("weather", "getWeather");
        weatherHolder.setVisibility(View.VISIBLE);

        String lat = sharedPref.getString("Latitude", "");
        String lng = sharedPref.getString("Longitude", "");

        if (!lat.equals("") && !lng.equals("")) {
            String apiStr = "https://api.thinkpage.cn/v3/weather/daily.json?key=txyws41isbyqnma5&" +
                    "location=" + lat + ":" + lng + "&language=zh-Hans&unit=c";
            new GetWeather().execute(apiStr);
            Log.d("weather", "Location already know");
        } else {
            Log.d("weather", "startLocation");
            mLocationClient.startLocation();
        }
    }

    private String doInBackground(String params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * execute only once per day, select a new movie randomly
     */
    private void setMovieInfoRandom() {
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " +
                MovieEntry.TABLE_NAME +
                " WHERE " + MovieEntry.COLUMN_NAME_ID +
                " IN (SELECT " + MovieEntry.COLUMN_NAME_ID + " FROM " +
                MovieEntry.TABLE_NAME +
                " ORDER BY RANDOM() LIMIT 1)";
        Cursor cursor = db.rawQuery(
                sql,
                null
        );

        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_ID));
            sharedPref.edit().putString("DATE", df_ymd.format(new Date())).apply();
            sharedPref.edit().putString("ID", id).apply();
            sharedPref.edit().putString("movie_json", "").apply();
            setMovieInfoRepeat(id);
        }
        cursor.close();
    }

    /**
     * show the same movie in the same day
     */
    private void setMovieInfoRepeat(String id) {
        String movieJson = sharedPref.getString("movie_json", "");
        if (movieJson.equals("")) {
            new GetMovieInfo().execute("http://api.douban.com/v2/movie/subject/" + id);
        } else {
            MovieBean todayMovie = new Gson().fromJson(movieJson, MovieBean.class);
            setMovieInfo(todayMovie);
        }
    }

    private void setMovieInfo(MovieBean todayMovie) {
        movieTitleTV.setText(todayMovie.getTitle());
        movieAverageTV.setText(Float.toString(todayMovie.getRating().getAverage()));

        setRatingStar(starsHolderLL, todayMovie.getRating().getStarts());

        movieSummaryTV.setText(todayMovie.getSummary());
        movieSummaryTV.setOnClickListener(this);

        Glide.with(this).load(todayMovie.getImages().getLarge()).into(movieImageIV);

        movieImageIV.setOnClickListener(this);

        CelebrityBean[] directors = todayMovie.getDirectors();
        CelebrityBean[] casts = todayMovie.getCasts();
        String[] genres = todayMovie.getGenres();
        if (directors.length > 0)
            directorTV.setText(String.format(getString(R.string.chip_director), directors[0].getName()));
        else
            directorTV.setVisibility(View.GONE);
        if (casts.length > 0)
            castsTV1.setText(String.format(getString(R.string.chip_casts), casts[0].getName()));
        else
            castsTV1.setVisibility(View.GONE);
        if (casts.length > 1)
            castsTV2.setText(String.format(getString(R.string.chip_casts), casts[1].getName()));
        else
            castsTV2.setVisibility(View.GONE);
        if (genres.length > 0)
            genresTV1.setText(String.format(getString(R.string.chip_genres), genres[0]));
        else
            genresTV1.setVisibility(View.GONE);
        if (genres.length > 1)
            genresTV2.setText(String.format(getString(R.string.chip_genres), genres[1]));
        else
            genresTV2.setVisibility(View.GONE);

        directorTV.setOnClickListener(this);
        castsTV1.setOnClickListener(this);
        castsTV2.setOnClickListener(this);
        genresTV1.setOnClickListener(this);
        genresTV2.setOnClickListener(this);
        genresTV2.setOnClickListener(this);
        sameNameTV.setOnClickListener(this);

        movieCard.setVisibility(View.GONE);
        todayMovieCard.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog(String content) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(content);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * set movie in theaters
     */
    private void setMovieInTheaters(DoubanMovies moviesBean) {
        if (moviesBean != null) {
            inTheatersCard.setVisibility(View.VISIBLE);
            TextView allInTheaters = (TextView) findViewById(R.id.all_in_theaters);
            allInTheaters.setText(String.format(
                    getString(R.string.all_movie),
                    moviesBean.getTotal() + ""
            ));
            allInTheaters.setOnClickListener(this);

            LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(this);
            linearLayoutMgr.setOrientation(LinearLayoutManager.HORIZONTAL);
            inTheatersRecyclerHolder.setLayoutManager(linearLayoutMgr);
            final List<MovieBean> dataset = Arrays.asList(moviesBean.getSubjects());
            MovieInTheatersAdapter theatersAdapter = new MovieInTheatersAdapter(dataset);

            inTheatersRecyclerHolder.setAdapter(theatersAdapter);
        } else {
            sharedPref.edit().putString("in_theaters", "").apply();
            inTheatersCard.setVisibility(View.GONE);
        }
    }

    /**
     * set movie coming soon
     */
    private void setMovieComingSoon(DoubanMovies moviesBean) {
        if (moviesBean != null) {
            comingSoonCard.setVisibility(View.VISIBLE);
            TextView allComingSoon = (TextView) findViewById(R.id.all_coming_soon);
            allComingSoon.setText(String.format(
                    getString(R.string.all_movie),
                    moviesBean.getTotal() + ""
            ));
            allComingSoon.setOnClickListener(this);

            LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(this);
            linearLayoutMgr.setOrientation(LinearLayoutManager.HORIZONTAL);
            comingSoonRecyclerHolder.setLayoutManager(linearLayoutMgr);
            final List<MovieBean> dataset = Arrays.asList(moviesBean.getSubjects());
            MovieComingSoonAdapter comingAdapter = new MovieComingSoonAdapter(dataset);

            comingSoonRecyclerHolder.setAdapter(comingAdapter);
        } else {
            sharedPref.edit().putString("coming_soon", "").apply();
            comingSoonCard.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        MovieBean todayMovie = new Gson().fromJson(
                sharedPref.getString("movie_json", ""),
                MovieBean.class
        );
        switch (v.getId()) {
            case R.id.getTop250_btn:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.download_movie_data))
                        .setMessage(getString(R.string.download_tips))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initMovieDB();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            case R.id.weather_icon:
                String weatherJson = sharedPref.getString("weather_json", "");
                if (!weatherJson.equals("")) {
                    WeatherFragment weatherFragment = new WeatherFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("weather", weatherJson);

                    weatherFragment.setArguments(bundle);
                    weatherFragment.show(getSupportFragmentManager(), "Weather_Preview");
                } else {
                    mLocationClient.startLocation();
                }
                break;
            case R.id.all_in_theaters:
                Log.d("MainActivity", "all_in_theaters");
                // TODO: 2017/4/18
                break;
            case R.id.all_coming_soon:
                Log.d("MainActivity", "all_coming_soon");
                // TODO: 2017/4/19
                break;
            case R.id.director:
                startCelebrityActivity("director", todayMovie.getDirectors()[0].getId());
                break;
            case R.id.casts_1:
                startCelebrityActivity("casts", todayMovie.getCasts()[0].getId());
                break;
            case R.id.casts_2:
                startCelebrityActivity("casts", todayMovie.getDirectors()[0].getId());
                break;
            case R.id.same_name:
                // TODO: 2017/4/19
                break;
            case R.id.city_name:
                mLocationClient.startLocation();
                break;
            case R.id.movie_image:
            case R.id.movie_summary:
                openMovieFragment(sharedPref.getString("movie_json", ""), "");
                break;
            case R.id.after_ad_closed_rate:
                Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    "http://play.google.com/store/apps/details?id=" +
                                            getApplication().getPackageName()
                            )
                    ));
                }
                break;
            case R.id.after_ad_closed_restore:
                settingPref.edit().putBoolean("ad_show", true).apply();
                initAd();
                break;
            default:
                break;
        }
    }

    private void setWeather() {
        String s = sharedPref.getString("weather_json", "");
        XzBean xzBean = new Gson().fromJson(s, XzBean.class);
        XzResultsBean resultsBean = xzBean.getResults()[0];
        XzLocationBean locationBean = resultsBean.getLocation();
        XzWeatherBean[] weatherBeans = resultsBean.getDaily();

        cityNameTV.setText(
                locationBean.getName() + "\n" +
                        String.format(
                                getResources().getString(R.string.last_update),
                                sharedPref.getString("update_time", "")
                        )
        );
        XzWeatherBean nowWeather = weatherBeans[0];

        String weathersText = getTextDayNight(
                nowWeather.getText_day(),
                nowWeather.getText_night()
        );

        String weather = String.format(
                getResources().getString(R.string.weather),
                weathersText,
                nowWeather.getHigh(),
                nowWeather.getLow()
        );

        weatherTV.setText(weather);

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) { //day
            String weather_code = nowWeather.getCode_day();
            weatherIconIV.setImageResource(icons.map.get(weather_code));
            if (festival == 0) {
                changeTheme(weather_code);
            }
        } else {//night
            String weather_code = nowWeather.getCode_night();
            weatherIconIV.setImageResource(icons.map.get(weather_code));
            if (festival == 0) {
                changeTheme(weather_code);
            }
        }
    }

    public void openMovieFragment(String movieJson, String from) {

        if (movieJson.equals("")) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        } else {
            MovieFragment movieFragment = new MovieFragment();
            Bundle bundle = new Bundle();
            bundle.putString("movie", movieJson);
            bundle.putString("from", from);

            movieFragment.setArguments(bundle);
            movieFragment.show(getSupportFragmentManager(), movieFragment.getTag());
        }
    }

    private void startCelebrityActivity(String type, String celebrityName) {
        Intent intent = new Intent(MainActivity.this, CelebrityActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("name", celebrityName);
        startActivity(intent);
    }

    private void changeTheme(String weather_code) {
        switch (icons.getWeather(weather_code)) {
            case 1://sunny
                color = ContextCompat.getColor(this, R.color.colorPrimary);
                colorDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
                setCustomTheme(color, colorDark, collapsingToolbarLayout);
                break;
            case 2://cloud
                color = ContextCompat.getColor(this, R.color.gray);
                colorDark = ContextCompat.getColor(this, R.color.grayDark);
                setCustomTheme(color, colorDark, collapsingToolbarLayout);
                break;
            case 3://rain
                color = ContextCompat.getColor(this, R.color.navyGray);
                colorDark = ContextCompat.getColor(this, R.color.navyGrayDark);
                setCustomTheme(color, colorDark, collapsingToolbarLayout);
                break;
            case 4://snow
                color = ContextCompat.getColor(this, R.color.blueSky);
                colorDark = ContextCompat.getColor(this, R.color.blueSkyDark);
                setCustomTheme(color, colorDark, collapsingToolbarLayout);
                break;
            case 5://wind_sand
                color = ContextCompat.getColor(this, R.color.orange);
                colorDark = ContextCompat.getColor(this, R.color.orangeDark);
                setCustomTheme(color, colorDark, collapsingToolbarLayout);
                break;
            default:
                break;
        }
    }

    private void restoreTheme() {
        color = ContextCompat.getColor(this, R.color.colorPrimary);
        colorDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        setCustomTheme(color, colorDark, collapsingToolbarLayout);
    }

    @Override
    public void onUpdateWeather() {
        String lastWeatherUpdate = sharedPref.getString("update_datetime", "");

        try {
            Date lastUpdateDate = df_ymd_hms.parse(lastWeatherUpdate);
            // 刷新频率最高：5min
            if (new Date().getTime() - lastUpdateDate.getTime() > 5 * 60 * 1000) {
                Snackbar.make(
                        nestedScrollView,
                        getString(R.string.weather_updating),
                        Snackbar.LENGTH_SHORT
                ).show();
                getWeather();
            } else {
                Snackbar.make(
                        nestedScrollView,
                        getString(R.string.weather_update_too_often),
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            getWeather();
        }
    }

    private class GetTop250 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return MainActivity.this.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.setMessage(getResources().getString(R.string.downloaded));

            DoubanMovies top250Bean = new Gson().fromJson(s, DoubanMovies.class);
            if (top250Bean != null) {
                MovieBaseBean[] movieBaseBeans = top250Bean.getSubjects();

                db = dbHelper.getWritableDatabase();

                mProgressDialog.setMessage(getResources().getString(R.string.createDB));

                for (MovieBaseBean mbb : movieBaseBeans) {
                    ContentValues values = new ContentValues();

                    values.put(MovieEntry.COLUMN_NAME_ID, mbb.getId());
                    values.put(MovieEntry.COLUMN_NAME_TITLE, mbb.getTitle());
                    values.put(MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, mbb.getOriginal_title());
                    values.put(MovieEntry.COLUMN_NAME_IMAGES, mbb.getImages().getLarge());
                    values.put(MovieEntry.COLUMN_NAME_ALT, mbb.getAlt());
                    values.put(MovieEntry.COLUMN_NAME_YEAR, mbb.getYear());
                    values.put(MovieEntry.COLUMN_NAME_STARS, mbb.getRating().getStarts());
                    values.put(MovieEntry.COLUMN_NAME_AVERAGE, mbb.getRating().getAverage());
                    values.put(MovieEntry.COLUMN_NAME_SUMMARY, "");

                    db.insert(MovieEntry.TABLE_NAME, null, values);
                }

                int start = sharedPref.getInt("START", 0) + top250Bean.getCount();
                sharedPref.edit().putInt("START", start).apply();

                hideProgressDialog();

                setMovieInfoRandom();
            } else {
                hideProgressDialog();
                displaySnackBar(
                        nestedScrollView,
                        getString(R.string.douban_error),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initMovieDB();
                            }
                        });
            }

        }
    }

    private class GetMovieInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return MainActivity.this.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                sharedPref.edit().putString("movie_json", s).apply();
                MovieBean todayMovie = new Gson().fromJson(s, MovieBean.class);
                db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_NAME_SUMMARY, todayMovie.getSummary());

                String selection = MovieEntry.COLUMN_NAME_ID + "=?";
                String[] selectionArgs = {todayMovie.getId()};

                if (db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs) == 1) {
                    setMovieInfo(todayMovie);
                }
            } else {
                Log.d("douban", "getMovieInfo: null");
                displaySnackBar(
                        nestedScrollView,
                        getString(R.string.douban_error),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id = sharedPref.getString("ID", "");
                                if (!id.equals("")) {
                                    setMovieInfoRepeat(id);
                                }
                            }
                        });
            }
        }
    }

    private class GetWeather extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return MainActivity.this.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                sharedPref.edit().putString("weather_json", s).apply();
                sharedPref.edit().putString("update_time", df_hm.format(new Date())).apply();
                sharedPref.edit().putString("update_datetime", df_ymd_hms.format(new Date())).apply();

                //set weather
                setWeather();
            } else {

                cityNameTV.setText(getResources().getString(R.string.location_error));
                weatherTV.setText(getResources().getString(R.string.unknown_weahter));
                weatherIconIV.setImageDrawable(ContextCompat.getDrawable(
                        MainActivity.this,
                        icons.map.get("99")
                ));

                displaySnackBar(
                        nestedScrollView,
                        getString(R.string.weather_error),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getWeather();
                            }
                        });
            }
        }
    }

    private class GetInTheaters extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return MainActivity.this.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                sharedPref.edit().putString("in_theaters", s).apply();
                DoubanMovies moviesBean = new Gson().fromJson(s, DoubanMovies.class);

                setMovieInTheaters(moviesBean);
            } else {
                hideProgressDialog();
                displaySnackBar(
                        nestedScrollView,
                        getString(R.string.douban_error),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initMovieInTheaters();
                            }
                        });
            }
        }
    }

    private class GetComingSoon extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return MainActivity.this.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                sharedPref.edit().putString("coming_soon", s).apply();
                DoubanMovies moviesBean = new Gson().fromJson(s, DoubanMovies.class);

                setMovieComingSoon(moviesBean);
            } else {
                hideProgressDialog();
                displaySnackBar(
                        nestedScrollView,
                        getString(R.string.douban_error),
                        getString(R.string.retry),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initMovieComingSoon();
                            }
                        });
            }
        }
    }
}