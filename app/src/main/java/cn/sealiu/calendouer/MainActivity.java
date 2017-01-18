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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;
import cn.sealiu.calendouer.bean.Top250Bean;
import cn.sealiu.calendouer.bean.WeatherBean;
import cn.sealiu.calendouer.until.BitmapUtils;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract.MovieEntry;
import cn.sealiu.calendouer.until.MovieDBHelper;

import static android.Manifest.permission;

public class MainActivity extends AppCompatActivity {

    private final static int STAR = 5;
    private final static int COUNT = 20;
    TextView monthTV;
    TextView weekTV;
    TextView lunarTV;
    TextView dateTV;
    TextView monthYearTV;
    TextView weatherTV;
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
    String[] movieIds = new String[COUNT];
    int index = 0;
    private String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
            "九月", "十月", "十一月", "十二月"};
    private String[] lunar_months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
            "九月", "十月", "冬月", "腊月"};
    private String[] days = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九",
            "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    };
    private Calendar calendar;

    private LocationManager locationMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        monthTV = (TextView) findViewById(R.id.month);
        weekTV = (TextView) findViewById(R.id.week_day);
        lunarTV = (TextView) findViewById(R.id.lunar_date);
        dateTV = (TextView) findViewById(R.id.date);
        monthYearTV = (TextView) findViewById(R.id.month_year);

        weatherTV = (TextView) findViewById(R.id.weather);

        movieImageIV = (ImageView) findViewById(R.id.movie_image);
        movieAverageTV = (TextView) findViewById(R.id.rating__average);
        movieTitleTV = (TextView) findViewById(R.id.movie_title);
        movieSummaryTV = (TextView) findViewById(R.id.movie_summary);
        starsHolderLL = (LinearLayout) findViewById(R.id.rating__stars_holder);

        loadingPB = (ProgressBar) findViewById(R.id.loading);
        getTop250Btn = (AppCompatButton) findViewById(R.id.getTop250_btn);

        loadingPB.setVisibility(View.VISIBLE);

        dbHelper = new MovieDBHelper(this);

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
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        initWeather();
        initCalendar();

        if (checkEmpty()) {
            loadingPB.setVisibility(View.GONE);
            getTop250Btn.setVisibility(View.VISIBLE);
            getTop250Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initMovieDB();
                }
            });
        } else {
            Log.d("DB", "is not empty");

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

            String datePref = sharedPref.getString("DATE", "null");
            String idPref = sharedPref.getString("ID", "null");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
    }

    private void initCalendar() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        Log.d("SOLAR", "" + year + month + day + week);


        int[] lunar = LunarCalendar.solarToLunar(year, month, day);

        Log.d("LUNAR", "" + lunar[0] + lunar[1] + lunar[2]);

        monthTV.setText(months[month]);
        weekTV.setText(weeks[week]);
        lunarTV.setText(
                String.format(
                        getResources().getString(R.string.lunar_date),
                        lunar_months[lunar[1] - 1],
                        days[lunar[2] - 1]
                )
        );

        dateTV.setText(Integer.toString(day));

        monthYearTV.setText(
                String.format(
                        getResources().getString(R.string.month_year),
                        year,
                        month
                )
        );
        if (week == 0 || week == 6) {
            dateTV.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            dateTV.setTextColor(ContextCompat.getColor(this, R.color.secondaryText));
        }
    }

    private void initWeather() {

        locationMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            weatherTV.setText(getResources().getString(R.string.getWeatherInfo));
            weatherTV.setOnClickListener(new View.OnClickListener() {
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
                                            new String[]{permission.ACCESS_FINE_LOCATION},
                                            100
                                    );
                                    weatherTV.setOnClickListener(null);
                                }
                            })
                            .setNegativeButton(getString(R.string.deny), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            });
            return;
        }
        locationMgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

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
                });

        Location location = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            weatherTV.setText(getResources().getString(R.string.location_error));
        } else {
            double lat = Math.round(location.getLatitude() * 100) / 100.0;
            double lng = Math.round(location.getLongitude() * 100) / 100.0;

            Log.d("WEATHER", lat + "");
            Log.d("WEATHER", lng + "");

            new GetWeather().execute("http://api.openweathermap.org/data/2.5/weather?lat=" +
                    lat + "&lon=" + lng + "&lang=zh_cn&units=metric&appid=" +
                    getResources().getString(R.string.openWeather));
        }

    }

    private void initMovieDB() {
        showProgressDialog(getResources().getString(R.string.downloading));
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int start = sharedPref.getInt("START", 0);
        new GetTop250().execute("https://api.douban.com/v2/movie/top250?start=" + start + "&count=" + COUNT);
    }

    private void arrangeMovieDB() {
        mProgressDialog.setMessage(getResources().getString(R.string.arrangeData));

        if (movieIds.length != 0) {
            new GetMovieInfo().execute("https://api.douban.com/v2/movie/subject/" + movieIds[index]);
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
            float average = cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_AVERAGE));
            String stars = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_STARS));
            String images = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_IMAGES));
            final String alt = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_ALT));
            String summary = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_NAME_SUMMARY));

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

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
    protected void onDestroy() {
        db.close();
        dbHelper.close();
        super.onDestroy();
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
                int index = 0;
                MovieBaseBean[] movieBaseBeans = top250Bean.getSubjects();

                db = dbHelper.getWritableDatabase();

                mProgressDialog.setMessage(getResources().getString(R.string.createDB));

                for (MovieBaseBean mbb : movieBaseBeans) {

                    movieIds[index++] = mbb.getId();

                    ContentValues values = new ContentValues();

                    values.put(MovieEntry.COLUMN_NAME_ID, mbb.getId());
                    values.put(MovieEntry.COLUMN_NAME_TITLE, mbb.getTitle());
                    values.put(MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, mbb.getOriginal_title());
                    values.put(MovieEntry.COLUMN_NAME_IMAGES, mbb.getImages().getLarge());
                    values.put(MovieEntry.COLUMN_NAME_AVERAGE, 0.0);
                    values.put(MovieEntry.COLUMN_NAME_STARS, "0");
                    values.put(MovieEntry.COLUMN_NAME_ALT, mbb.getAlt());
                    values.put(MovieEntry.COLUMN_NAME_YEAR, mbb.getYear());
                    values.put(MovieEntry.COLUMN_NAME_SUMMARY, "");

                    db.insert(MovieEntry.TABLE_NAME, null, values);
                }

                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                int start = sharedPref.getInt("START", 0) + COUNT;
                sharedPref.edit().putInt("START", start).apply();

                arrangeMovieDB();
            } else {
                Toast.makeText(
                        MainActivity.this,
                        getResources().getString(R.string.api_error),
                        Toast.LENGTH_LONG
                ).show();
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

            MovieBean movieBean = new Gson().fromJson(s, MovieBean.class);

            if (movieBean != null) {
                db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_NAME_AVERAGE, movieBean.getRating().getAverage());
                values.put(MovieEntry.COLUMN_NAME_STARS, movieBean.getRating().getStarts());
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


            if (++index < COUNT) {
                new GetMovieInfo().execute("https://api.douban.com/v2/movie/subject/" + movieIds[index]);
            } else {
                hideProgressDialog();
                getTop250Btn.setVisibility(View.GONE);
                setMovieInfo();
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
            final WeatherBean weatherBean = new Gson().fromJson(s, WeatherBean.class);
            String weathers = "";
            int length = weatherBean.getWeather().length;
            for (int i = 0; i < length; i++) {
                weathers += weatherBean.getWeather()[i].getDescription() + " ";
            }

            String weather = String.format(
                    getResources().getString(R.string.weather),
                    weatherBean.getName(),
                    weathers,
                    weatherBean.getMain().getTemp());

            weatherTV.setText(weather);
        }
    }
}