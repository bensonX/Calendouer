package cn.sealiu.calendouer;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import cn.sealiu.calendouer.until.BitmapUtils;
import cn.sealiu.calendouer.until.FestivalCalendar;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract.MovieEntry;
import cn.sealiu.calendouer.until.MovieDBHelper;
import cn.sealiu.calendouer.until.SolarTermCalendar;
import cn.sealiu.calendouer.until.WeatherIcon;

import static android.Manifest.permission;

public class MainActivity extends AppCompatActivity implements
        AMapLocationListener,
        View.OnClickListener {

    private final static int STAR = 5;
    private final static int MAX_COUNT = 100;
    private final static int LOCATION_PERM = 100;
    Toolbar toolbar;
    TextView monthTV;
    TextView weekTV;
    TextView lunarTV;
    TextView dateTV;
    TextView solarTermTV;
    TextView festivalTV;
    RelativeLayout weatherHolder;
    LinearLayout movieRecommendedHolder;
    TextView getWeatherTV;
    TextView cityNameTV;
    TextView weatherTV;
    ImageView weatherIconIV;
    TextView doubanTitleTV;
    ImageView movieImageIV;
    TextView movieAverageTV;
    TextView movieTitleTV;
    TextView movieSummaryTV;
    LinearLayout starsHolderLL;
    ProgressBar loadingPB;
    AppCompatButton getTop250Btn;
    ProgressDialog mProgressDialog;
    MovieDBHelper dbHelper;
    SQLiteDatabase db;
    WeatherIcon icons;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    SharedPreferences sharedPref;
    DateFormat df;
    CardView movieCard;
    SharedPreferences settingPref;
    private ShowcaseView showcaseView;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        monthTV = (TextView) findViewById(R.id.month);
        weekTV = (TextView) findViewById(R.id.week_day);
        lunarTV = (TextView) findViewById(R.id.lunar_date);
        dateTV = (TextView) findViewById(R.id.date);
        solarTermTV = (TextView) findViewById(R.id.solar_term);
        festivalTV = (TextView) findViewById(R.id.festival);

        weatherHolder = (RelativeLayout) findViewById(R.id.weatherHolder);
        getWeatherTV = (TextView) findViewById(R.id.getWeatherInfo);
        cityNameTV = (TextView) findViewById(R.id.city_name);
        weatherTV = (TextView) findViewById(R.id.weather);
        weatherIconIV = (ImageView) findViewById(R.id.weather_icon);
        weatherIconIV.setOnClickListener(this);
        movieCard = (CardView) findViewById(R.id.movie_card);
        doubanTitleTV = (TextView) findViewById(R.id.douban_movie_title);
        movieImageIV = (ImageView) findViewById(R.id.movie_image);
        movieAverageTV = (TextView) findViewById(R.id.rating__average);
        movieTitleTV = (TextView) findViewById(R.id.movie_title);
        movieSummaryTV = (TextView) findViewById(R.id.movie_summary);
        starsHolderLL = (LinearLayout) findViewById(R.id.rating__stars_holder);

        loadingPB = (ProgressBar) findViewById(R.id.loading);
        getTop250Btn = (AppCompatButton) findViewById(R.id.getTop250_btn);
        movieRecommendedHolder = (LinearLayout) findViewById(R.id.movie_recommended_holder);

        loadingPB.setVisibility(View.VISIBLE);

        dbHelper = new MovieDBHelper(this);

        icons = new WeatherIcon();
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationClient.setLocationOption(mLocationOption);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private boolean checkEmpty() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();
        return isEmpty;
    }

    @Override
    protected void onResume() {
        Log.d("RUN", "onResume");
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        initCalendar();

        if (settingPref.getBoolean("weather_show", true)) {
            weatherHolder.setVisibility(View.VISIBLE);
            initWeather();
        } else {
            weatherHolder.setVisibility(View.GONE);
            // TODO: 2017/2/27 weather card hide and show notification
        }

        if (settingPref.getBoolean("movie_recommended_show", true)) {
            movieCard.setVisibility(View.VISIBLE);
            if (checkEmpty()) {
                loadingPB.setVisibility(View.GONE);
                getTop250Btn.setVisibility(View.VISIBLE);
                movieRecommendedHolder.setVisibility(View.GONE);
                getTop250Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getTop250Btn.setVisibility(View.GONE);
                        initMovieDB();
                    }
                });
            } else {
                Log.d("DB", "is not empty");
                String datePref = sharedPref.getString("DATE", "null");
                String idPref = sharedPref.getString("ID", "null");

                if (!datePref.equals(df.format(new Date()))) {

                    db = dbHelper.getWritableDatabase();
                    db.delete(
                            MovieEntry.TABLE_NAME,
                            MovieEntry.COLUMN_NAME_ID + "=?",
                            new String[]{idPref}
                    );
                }
                setMovieInfo();
            }
        } else {
            // TODO: 2017/2/27 movie card hide
            movieCard.setVisibility(View.GONE);
        }

        if (sharedPref.getBoolean("first_run", true)) {
            sharedPref.edit().putBoolean("first_run", false).apply();
            counter = 0;
            firstLaunch();
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

        // 设置节气
        String str = SolarTermCalendar.getSolarTermStr(now);
        if (str != null) {
            Log.d("SolarTerm", str);
            solarTermTV.setVisibility(View.VISIBLE);
            solarTermTV.setText(str);
        } else {
            solarTermTV.setVisibility(View.GONE);
        }

        // 设置节日
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        // 节日
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
        } else {
            festivalTV.setVisibility(View.GONE);
        }
    }

    private void initWeather() {

        if (!checkPermission()) {

            getWeatherTV.setVisibility(View.VISIBLE);
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
            Log.d("PERM", "已有授权");
            if (sharedPref.getString("weather_json", "").equals("")) {
                getWeather();
            } else {
                setWeather();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherTV.setText("Loading");
                getWeatherTV.setOnClickListener(null);

                Log.d("PERM", "授权成功，开始定位");
                mLocationClient.startLocation();
            } else {
                getWeatherTV.setText("Need Location Permission");
            }
        }
    }

    private void getWeather() {
        Log.d("PERM", "获取天气");

        SharedPreferences locationPref = getApplication()
                .getSharedPreferences("location", MODE_PRIVATE);

        String lat = locationPref.getString("Latitude", "");
        String lng = locationPref.getString("Longitude", "");

        getWeatherTV.setVisibility(View.GONE);
        weatherHolder.setVisibility(View.VISIBLE);
        if (!lat.equals("") && !lng.equals("")) {
            Log.d("PERM", "经纬度不为空");
            String apiStr = "https://api.thinkpage.cn/v3/weather/daily.json?key=txyws41isbyqnma5&" +
                    "location=" + lat + ":" + lng + "&language=zh-Hans&unit=c";
            new GetWeather().execute(apiStr);
        } else {
            Log.d("PERM", "经纬度为空");
            cityNameTV.setText(getResources().getString(R.string.location_error));
            weatherTV.setText(getResources().getString(R.string.unknown_weahter));
            weatherIconIV.setImageDrawable(ContextCompat.getDrawable(
                    MainActivity.this,
                    icons.map.get("99")
            ));
        }
    }

    private void initMovieDB() {
        showProgressDialog(getResources().getString(R.string.downloading));
        int start = sharedPref.getInt("START", 0);
        new GetTop250().execute("https://api.douban.com/v2/movie/top250?start=" + start + "&count=" + MAX_COUNT);
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
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
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

    private void setMovieInfo() {

        loadingPB.setVisibility(View.VISIBLE);

        db = dbHelper.getReadableDatabase();

        String[] projection = {
                MovieEntry.COLUMN_NAME_ID,
                MovieEntry.COLUMN_NAME_TITLE,
                MovieEntry.COLUMN_NAME_AVERAGE,
                MovieEntry.COLUMN_NAME_STARS,
                MovieEntry.COLUMN_NAME_IMAGES,
                MovieEntry.COLUMN_NAME_ALT,
                MovieEntry.COLUMN_NAME_SUMMARY
        };

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                "1"
        );

        String id = "null";
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_TITLE));
            String images = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_IMAGES));
            final String alt = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_ALT));
            String stars = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_STARS));
            float average = cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_AVERAGE));

            //通过top250无法获取
            String summary = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_SUMMARY));
            //需要借助getMovieInfo（仅一次）
            if (summary.equals("") && id != null) {
                new GetMovieInfo().execute("https://api.douban.com/v2/movie/subject/" + id);
                return;
            }

            if (loadingPB.getVisibility() == View.VISIBLE) {
                loadingPB.setVisibility(View.GONE);
            }

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

            BitmapUtils bitmapUtils = new BitmapUtils();
            bitmapUtils.disPlay(movieImageIV, images);

            movieImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(alt));
                    startActivity(intent);
                }
            });
        }

        cursor.close();

        sharedPref.edit().putString("DATE", df.format(new Date())).apply();
        sharedPref.edit().putString("ID", id).apply();
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                String lat = aMapLocation.getLatitude() + "";
                String lng = aMapLocation.getLongitude() + "";

                Log.d("AMap", lat + "/" + lng);

                SharedPreferences locationPref = getApplication()
                        .getSharedPreferences("location", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = locationPref.edit();
                prefsEditor.putString("Latitude", lat);
                prefsEditor.putString("Longitude", lng);
                prefsEditor.apply();

                Log.d("PERM", "定位成功，准备获取天气");

                // 这时重新取天气预报，不从shardPref中读取
                getWeather();

                mLocationOption.setInterval(30000);//5分钟刷新定位
                mLocationClient.setLocationOption(mLocationOption);

            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因
                // errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.weather_icon) {

            String weatherJson = sharedPref.getString("weather_json", "");
            if (!weatherJson.equals("")) {
                WeatherFragment weatherFragment = new WeatherFragment();
                Bundle bundle = new Bundle();
                bundle.putString("weather", weatherJson);

                weatherFragment.setArguments(bundle);
                weatherFragment.show(getSupportFragmentManager(), "Weather_Preview");
            }
        }
    }

    private void firstLaunch() {

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        Button customButton = (Button) getLayoutInflater().inflate(R.layout.view_custom_button, null);
        MultiEventListener multiEventListener = new MultiEventListener(new ShakeButtonListener(customButton));

        showcaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget(findViewById(R.id.date)))
                .setContentTitle(getString(R.string.case_date_title))
                .setContentText(getString(R.string.case_date_text))
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(multiEventListener)
                .replaceEndButton(customButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (counter) {
                            case 0:
                                showcaseView.setShowcase(new ViewTarget(getWeatherTV), true);
                                showcaseView.setContentTitle(getString(R.string.case_weather_title));
                                showcaseView.setContentText(getString(R.string.case_weather_text));
                                break;
                            case 1:
                                showcaseView.setShowcase(new ViewTarget(doubanTitleTV), true);
                                showcaseView.setContentTitle(getString(R.string.case_douban_movie_title));
                                showcaseView.setContentText(getString(R.string.case_douban_movie_text));
                                break;
                            case 2:
                                showcaseView.setTarget(Target.NONE);
                                showcaseView.setContentTitle(getString(R.string.app_name));
                                showcaseView.setContentText(getString(R.string.welcome));
                                showcaseView.setButtonText(getString(R.string.close));
                                break;
                            case 3:
                                showcaseView.hide();
                                break;
                        }
                        counter++;
                    }
                })
                .build();
        showcaseView.setButtonPosition(lps);
    }

    /*
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
            toolbar.animate().alpha(1).setDuration(600);
        } else {
            toolbar.animate().alpha(0).setDuration(600);
        }
    }
    */

    private void setWeather() {
        String s = sharedPref.getString("weather_json", "");
        XzBean xzBean = new Gson().fromJson(s, XzBean.class);
        XzResultsBean resultsBean = xzBean.getResults()[0];
        XzLocationBean locationBean = resultsBean.getLocation();
        XzWeatherBean[] weatherBeans = resultsBean.getDaily();
        String lastUpdate = resultsBean.getLast_update();

        Log.d("XZ", "location: " + locationBean.getPath());
        Log.d("XZ", "weather: " + weatherBeans[0].toString());
        Log.d("XZ", "last_update: " + lastUpdate);

        cityNameTV.setText(locationBean.getName());
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

        weatherIconIV.setImageDrawable(
                ContextCompat.getDrawable(
                        MainActivity.this,
                        icons.map.get(nowWeather.getCode_day())
                )
        );
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
                    // 无法通过top250获得：
                    values.put(MovieEntry.COLUMN_NAME_SUMMARY, "");

                    db.insert(MovieEntry.TABLE_NAME, null, values);
                }

                int start = sharedPref.getInt("START", 0) + top250Bean.getCount();
                sharedPref.edit().putInt("START", start).apply();
                movieRecommendedHolder.setVisibility(View.VISIBLE);
                setMovieInfo();
            } else {
                getTop250Btn.setVisibility(View.VISIBLE);
                Toast.makeText(
                        MainActivity.this,
                        getResources().getString(R.string.api_error),
                        Toast.LENGTH_LONG
                ).show();

                // TODO: 2017/2/26 修改top250显示信息，或者重试
            }

            hideProgressDialog();
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

            MovieBean movieBean = new Gson().fromJson(s, MovieBean.class);

            if (movieBean != null) {
                db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_NAME_SUMMARY, movieBean.getSummary());

                String selection = MovieEntry.COLUMN_NAME_ID + "=?";
                String[] selectionArgs = {movieBean.getId()};

                db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
            } else {
                Toast.makeText(
                        MainActivity.this,
                        getResources().getString(R.string.api_error),
                        Toast.LENGTH_LONG
                ).show();
            }

            setMovieInfo();
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
                // TODO: 2017/2/27 处理请求天气信息错误的情况
            }
        }
    }

    private class ShakeButtonListener extends SimpleShowcaseEventListener {
        private final Button button;

        ShakeButtonListener(Button button) {
            this.button = button;
        }

        @Override
        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
            int translation = getResources().getDimensionPixelOffset(R.dimen.touch_button_wobble);
            ViewCompat.animate(button)
                    .translationXBy(translation)
                    .setInterpolator(new WobblyInterpolator(2));
        }
    }

    private class WobblyInterpolator implements Interpolator {

        private final double CONVERT_TO_RADS = 2 * Math.PI;
        private final int cycles;

        WobblyInterpolator(int cycles) {
            this.cycles = cycles;
        }

        @Override
        public float getInterpolation(float proportion) {
            double sin = Math.sin(cycles * proportion * CONVERT_TO_RADS);
            return (float) sin;
        }
    }
}