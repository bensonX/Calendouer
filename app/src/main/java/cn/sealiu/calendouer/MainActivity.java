package cn.sealiu.calendouer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;
import cn.sealiu.calendouer.bean.Top250Bean;
import cn.sealiu.calendouer.bean.XzBean;
import cn.sealiu.calendouer.bean.XzLocationBean;
import cn.sealiu.calendouer.bean.XzResultsBean;
import cn.sealiu.calendouer.bean.XzWeatherBean;
import cn.sealiu.calendouer.model.Thing;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.FestivalCalendar;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract.MovieEntry;
import cn.sealiu.calendouer.until.SolarTermCalendar;
import cn.sealiu.calendouer.until.ThingsContract.ThingsEntry;
import cn.sealiu.calendouer.until.WeatherIcon;

import static android.Manifest.permission;

public class MainActivity extends AppCompatActivity implements
        AMapLocationListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MovieFragment.MovieListener {

    private final static int STAR = 5;
    private final static int MAX_COUNT = 100;
    private final static int LOCATION_PERM = 100;
    private final static int THINGS_MAX_LINE = 5;

    private final static int WEATHER_REQUEST_CODE = 114;
    private final static int ADD_THINGS_CODE = 200;
    Toolbar toolbar;
    TextView monthTV;
    TextView weekTV;
    TextView lunarTV;
    TextView dateTV;
    TextView solarTermTV;
    TextView festivalTV;
    RelativeLayout weatherHolder;
    LinearLayout movieRecommendedHolder;
    AppCompatButton getWeatherTV;
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
    DateFormat df;
    LinearLayout weatherCard;
    LinearLayout thingsCard;
    LinearLayout movieCard;
    AppCompatButton thingsAllBtn;
    RecyclerView thingsRecyclerView;
    TextView thingsEmpty;
    SharedPreferences sharedPref;
    SharedPreferences settingPref;
    LocationManager locationMgr;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private int festival = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        monthTV = (TextView) findViewById(R.id.month);
        weekTV = (TextView) findViewById(R.id.week_day);
        lunarTV = (TextView) findViewById(R.id.lunar_date);
        dateTV = (TextView) findViewById(R.id.date);
        solarTermTV = (TextView) findViewById(R.id.solar_term);
        festivalTV = (TextView) findViewById(R.id.festival);
        weatherHolder = (RelativeLayout) findViewById(R.id.weatherHolder);
        getWeatherTV = (AppCompatButton) findViewById(R.id.getWeatherInfo);

        weatherCard = (LinearLayout) findViewById(R.id.weather_card);
        cityNameTV = (TextView) findViewById(R.id.city_name);
        weatherTV = (TextView) findViewById(R.id.weather);
        weatherIconIV = (ImageView) findViewById(R.id.weather_icon);
        weatherIconIV.setOnClickListener(this);

        thingsCard = (LinearLayout) findViewById(R.id.things_card);
        thingsEmpty = (TextView) findViewById(R.id.things_empty);
        thingsRecyclerView = (RecyclerView) findViewById(R.id.things_recycler_view);
        thingsAllBtn = (AppCompatButton) findViewById(R.id.things_all_btn);

        movieCardCover = (ImageView) findViewById(R.id.movie_card_cover);
        movieCard = (LinearLayout) findViewById(R.id.movie_card);
        movieImageIV = (ImageView) findViewById(R.id.movie_image);
        movieAverageTV = (TextView) findViewById(R.id.rating__average);
        movieTitleTV = (TextView) findViewById(R.id.movie_title);
        movieSummaryTV = (TextView) findViewById(R.id.movie_summary);
        starsHolderLL = (LinearLayout) findViewById(R.id.rating__stars_holder);

        getTop250Btn = (AppCompatButton) findViewById(R.id.getTop250_btn);
        movieRecommendedHolder = (LinearLayout) findViewById(R.id.movie_recommended_holder);

        dbHelper = new DBHelper(this);

        icons = new WeatherIcon();

        sharedPref = this.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPref.registerOnSharedPreferenceChangeListener(this);

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        locationMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationClient.setLocationOption(mLocationOption);

        initCalendar();

        if (settingPref.getBoolean("weather_show", true)) {
            weatherHolder.setVisibility(View.VISIBLE);
            initWeather();
        } else {
            weatherHolder.setVisibility(View.GONE);
        }

        if (settingPref.getBoolean("things_show", true)) {
            thingsCard.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            if (checkAllDone()) {
                thingsEmpty.setVisibility(View.VISIBLE);
            } else {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                thingsRecyclerView.setLayoutManager(linearLayoutManager);
                initThings();
            }
        } else {
            thingsCard.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }

        if (settingPref.getBoolean("movie_recommended_show", true)) {
            movieCard.setVisibility(View.VISIBLE);
            if (checkEmpty(MovieEntry.TABLE_NAME)) {
                getTop250Btn.setVisibility(View.VISIBLE);
                movieCardCover.setVisibility(View.VISIBLE);
                movieRecommendedHolder.setVisibility(View.GONE);
                getTop250Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });
            } else {
                String datePref = sharedPref.getString("DATE", "null");
                String idPref = sharedPref.getString("ID", "null");

                if (datePref.equals("") || idPref.equals("")) {
                    setMovieInfoRandom();
                } else {
                    if (!datePref.equals(df.format(new Date()))) {
                        //new day
                        db = dbHelper.getWritableDatabase();
                        if (db.delete(
                                MovieEntry.TABLE_NAME,
                                MovieEntry.COLUMN_NAME_ID + "=?",
                                new String[]{idPref}) == 1) {
                            setMovieInfoRandom();
                        }
                    } else {
                        //same day
                        setMovieInfoRepeat(idPref);
                    }
                }
            }
        } else {
            movieCard.setVisibility(View.GONE);
        }
    }

    private boolean checkEmpty(String tableName) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();
        return isEmpty;
    }

    private boolean checkAllDone() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ThingsEntry.TABLE_NAME, //table
                null, //columns
                ThingsEntry.COLUMN_NAME_DONE + " = ?", //selection
                new String[]{"0"}, //selectionArgs
                null, //groupBy
                null, //having
                ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME, //orderBy
                null //limit
        );
        boolean isAllDone = !cursor.moveToFirst();
        cursor.close();
        return isAllDone;
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
            int tomato = ContextCompat.getColor(this, R.color.tomato);
            int tomatoDark = ContextCompat.getColor(this, R.color.tomatoDark);
            setCustomTheme(tomato, tomatoDark);
            festival = 1;
        } else {
            festivalTV.setVisibility(View.GONE);
            festival = 0;
        }
    }

    private void initWeather() {

        if (!checkPermission()) {

            weatherCard.setVisibility(View.VISIBLE);
            weatherHolder.setVisibility(View.GONE);

            getWeatherTV.setOnClickListener(new View.OnClickListener() {
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
                setWeather();
            }
        }
    }

    private void initThings() {

        final List<Thing> dataSet = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String[] projection = {
                ThingsEntry.COLUMN_NAME_ID,
                ThingsEntry.COLUMN_NAME_TITLE,
                ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME
        };

        Cursor cursor = db.query(
                ThingsEntry.TABLE_NAME, //table
                projection, //columns
                ThingsEntry.COLUMN_NAME_DONE + " = ?", //selection
                new String[]{"0"}, //selectionArgs
                null, //groupBy
                null, //having
                ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME, //orderBy
                String.valueOf(THINGS_MAX_LINE) //limit
        );

        if (cursor.moveToFirst()) {
            thingsEmpty.setVisibility(View.GONE);
            do {
                String id = cursor.getString(cursor.getColumnIndex(ThingsEntry.COLUMN_NAME_ID));
                String title = cursor.getString(cursor.getColumnIndex(ThingsEntry.COLUMN_NAME_TITLE));
                String notification_datetime = cursor.getString(
                        cursor.getColumnIndex(ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME)
                );
                dataSet.add(new Thing(id, title, notification_datetime));
            } while (cursor.moveToNext());

            if (cursor.getCount() == THINGS_MAX_LINE) {
                thingsAllBtn.setVisibility(View.VISIBLE);
                thingsAllBtn.setOnClickListener(this);
            } else {
                thingsAllBtn.setVisibility(View.GONE);
            }
            cursor.close();

            thingsRecyclerView.setAdapter(new ThingsItemAdapter(dataSet) {
                @Override
                public void onBindViewHolder(ViewHolder holder, int position) {
                    final Thing thing = dataSet.get(position);
                    super.onBindViewHolder(holder, position);

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: 2017/3/5 handle click things event
                            Toast.makeText(MainActivity.this, thing.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            thingsEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initMovieDB() {
        showProgressDialog(getResources().getString(R.string.downloading));
        int start = sharedPref.getInt("START", 0);
        new GetTop250().execute("https://api.douban.com/v2/movie/top250?start=" + start + "&count=" + MAX_COUNT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherTV.setOnClickListener(null);

                mLocationClient.startLocation();
            } else {
                getWeatherTV.setText(getString(R.string.need_location_premission));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals("Latitude") || key.equals("Longitude") &&
                sharedPref.getString("weather_json", "").equals("")) {
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
                Snackbar.make(coordinatorLayout, aMapLocation.getErrorInfo(), Snackbar.LENGTH_LONG)
                        .setAction("重试", new View.OnClickListener() {
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
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getWeather() {
        weatherHolder.setVisibility(View.VISIBLE);

        String lat = sharedPref.getString("Latitude", "");
        String lng = sharedPref.getString("Longitude", "");

        if (!lat.equals("") && !lng.equals("")) {
            String apiStr = "https://api.thinkpage.cn/v3/weather/daily.json?key=txyws41isbyqnma5&" +
                    "location=" + lat + ":" + lng + "&language=zh-Hans&unit=c";
            new GetWeather().execute(apiStr);
        } else {
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
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
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
            sharedPref.edit().putString("DATE", df.format(new Date())).apply();
            sharedPref.edit().putString("ID", id).apply();
            setMovieInfoRepeat(id);
        }
        cursor.close();
    }

    /**
     * show the same movie in the same day
     */
    private void setMovieInfoRepeat(String id) {

        db = dbHelper.getReadableDatabase();

        String[] projection = {
                MovieEntry.COLUMN_NAME_ID,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_AVERAGE,
                MovieEntry.COLUMN_NAME_STARS,
                MovieEntry.COLUMN_NAME_IMAGES,
                MovieEntry.COLUMN_NAME_SUMMARY
        };

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME, //table
                projection, //columns
                MovieEntry.COLUMN_NAME_ID + " = ?", //selection
                new String[]{id}, //selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                "1" //limit
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_TITLE));
            String images = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_IMAGES));
            String stars = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_STARS));
            float average = cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_AVERAGE));
            String summary = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_SUMMARY));

            if (summary.equals("")) {
                Log.d("douban", "get_movie_info: " + id);
                new GetMovieInfo().execute("http://api.douban.com/v2/movie/subject/" + id);
            }

            setMovieInfo(title, images, stars, average, summary);
        }
        cursor.close();
    }

    private void setMovieInfo(String title, String images, String stars, float average, String summary) {

        movieTitleTV.setText(title);
        movieAverageTV.setText(Float.toString(average));
        double stars_num = Double.parseDouble(stars) / 10;

        int full_star_num = (int) Math.floor(stars_num);
        int half_star_num = (int) (Math.floor((stars_num - full_star_num) * 2));
        int blank_star_num = STAR - full_star_num - half_star_num;

        starsHolderLL.removeAllViews();

        while (full_star_num-- > 0) {
            ImageView star = new ImageView(MainActivity.this);
            star.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_star_16dp));
            starsHolderLL.addView(star);
        }
        while (half_star_num-- > 0) {
            ImageView star = new ImageView(MainActivity.this);
            star.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_star_half_16dp));
            starsHolderLL.addView(star);
        }

        while (blank_star_num-- > 0) {
            ImageView star = new ImageView(MainActivity.this);
            star.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_star_blank_16dp));
            starsHolderLL.addView(star);
        }

        movieSummaryTV.setText(summary);
        movieSummaryTV.setOnClickListener(this);

        Glide.with(this).load(images).into(movieImageIV);

        movieImageIV.setOnClickListener(this);
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

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
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
            case R.id.city_name:
                mLocationClient.startLocation();
                break;
            case R.id.movie_image:
            case R.id.movie_summary:
                openMovieFragment();
                break;
            case R.id.fab:
                startActivityForResult(new Intent(this, AddThingsActivity.class), ADD_THINGS_CODE);
                break;
            case R.id.things_all_btn:
                // TODO: 2017/3/5 check all things
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
                                resultsBean.getLast_update().substring(11, 16)
                        )
        );
        XzWeatherBean nowWeather = weatherBeans[0];

        String weathersText;
        if (nowWeather.getText_night().equals(nowWeather.getText_day())) {
            weathersText = nowWeather.getText_day();
        } else {
            weathersText = nowWeather.getText_day() + ", " + nowWeather.getText_night();
        }
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(
                Calendar.HOUR_OF_DAY,
                Integer.parseInt(settingPref.getString("update_frequency", "2"))
        );
        setTrigger(WEATHER_REQUEST_CODE, calendar.getTimeInMillis());
    }

    private void setTrigger(int requestCode, long triggerAtMillis) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateWeatherReceiver.class);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmMgr.set(
                AlarmManager.RTC,
                triggerAtMillis,
                alarmIntent
        );
    }

    @Override
    public void onAddThings() {
        Intent intent = new Intent(this, AddThingsActivity.class);

        intent.putExtra("movie_title", movieTitleTV.getText());
        startActivityForResult(intent, ADD_THINGS_CODE);
    }

    private void openMovieFragment() {
        String movieJson = sharedPref.getString("movie_json", "");

        if (movieJson.equals("")) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        } else {
            MovieFragment movieFragment = new MovieFragment();
            Bundle bundle = new Bundle();
            bundle.putString("movie", movieJson);

            movieFragment.setArguments(bundle);
            movieFragment.show(getSupportFragmentManager(), movieFragment.getTag());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_THINGS_CODE && resultCode == RESULT_OK) {
            Log.d("Things", "add things success");
            initThings();
        }
    }

    private void changeTheme(String weather_code) {
        switch (icons.getWeather(weather_code)) {
            case 1://sunny
                int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
                int colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
                setCustomTheme(colorPrimary, colorPrimaryDark);
                break;
            case 2://cloud
                int gray = ContextCompat.getColor(this, R.color.gray);
                int grayDark = ContextCompat.getColor(this, R.color.grayDark);
                setCustomTheme(gray, grayDark);
                break;
            case 3://rain
                int navyGray = ContextCompat.getColor(this, R.color.navyGray);
                int navyGrayDark = ContextCompat.getColor(this, R.color.navyGrayDark);
                setCustomTheme(navyGray, navyGrayDark);
                break;
            case 4://snow
                int blueSky = ContextCompat.getColor(this, R.color.blueSky);
                int blueSkyDark = ContextCompat.getColor(this, R.color.blueSkyDark);
                setCustomTheme(blueSky, blueSkyDark);
                break;
            case 5://wind_sand
                int orange = ContextCompat.getColor(this, R.color.orange);
                int orangeDark = ContextCompat.getColor(this, R.color.orangeDark);
                setCustomTheme(orange, orangeDark);
                break;
            default:
                break;
        }
    }

    private void setCustomTheme(int color, int colorDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setBackgroundTintList(ColorStateList.valueOf(color));
            collapsingToolbarLayout.setContentScrimColor(color);
            collapsingToolbarLayout.setBackgroundColor(color);
            this.getWindow().setNavigationBarColor(color);
            this.getWindow().setStatusBarColor(colorDark);
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

            Top250Bean top250Bean = new Gson().fromJson(s, Top250Bean.class);
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
                movieRecommendedHolder.setVisibility(View.VISIBLE);

                hideProgressDialog();
                getTop250Btn.setVisibility(View.GONE);
                movieCardCover.setVisibility(View.GONE);
                setMovieInfoRandom();
            } else {
                hideProgressDialog();
                getTop250Btn.setVisibility(View.VISIBLE);
                movieCardCover.setVisibility(View.VISIBLE);

                Snackbar.make(coordinatorLayout, "豆瓣服务器开小差了", Snackbar.LENGTH_LONG)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initMovieDB();
                            }
                        }).show();
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
                Log.d("douban", "getMovieInfo: " + s);
                sharedPref.edit().putString("movie_json", s).apply();
                final MovieBean movieBean = new Gson().fromJson(s, MovieBean.class);
                db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_NAME_SUMMARY, movieBean.getSummary());

                String selection = MovieEntry.COLUMN_NAME_ID + "=?";
                String[] selectionArgs = {movieBean.getId()};

                if (db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs) == 1) {
                    setMovieInfo(
                            movieBean.getTitle(),
                            movieBean.getImages().getLarge(),
                            movieBean.getRating().getStarts(),
                            movieBean.getRating().getAverage(),
                            movieBean.getSummary()
                    );
                }
            } else {
                Log.d("douban", "getMovieInfo: null");
                Snackbar.make(coordinatorLayout, "豆瓣服务器开小差了", Snackbar.LENGTH_LONG)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id = sharedPref.getString("ID", "");
                                if (!id.equals("")) {
                                    setMovieInfoRepeat(id);
                                }
                            }
                        }).show();
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
                setWeather();
            } else {

                cityNameTV.setText(getResources().getString(R.string.location_error));
                weatherTV.setText(getResources().getString(R.string.unknown_weahter));
                weatherIconIV.setImageDrawable(ContextCompat.getDrawable(
                        MainActivity.this,
                        icons.map.get("99")
                ));

                Snackbar.make(coordinatorLayout, "请求天气失败", Snackbar.LENGTH_LONG)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getWeather();
                            }
                        });
            }
        }
    }
}