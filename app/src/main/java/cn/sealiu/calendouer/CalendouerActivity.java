package cn.sealiu.calendouer;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;
import cn.sealiu.calendouer.fragment.MovieFragment;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.MovieContract;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class CalendouerActivity extends AppCompatActivity implements
        MovieFragment.LikeMovieListener {

    final static int STAR = 5;
    final static int MAX_COUNT = 100;
    final static int LOCATION_PERM = 100;
    public static SharedPreferences sharedPref, settingPref;
    DateFormat df_ymd, df_hm, df_ymd_hms;

    public static void setRatingStar(Context context, ViewGroup holder, String stars) {
        double stars_num = Double.parseDouble(stars) / 10;
        int full_star_num = (int) Math.floor(stars_num);
        int half_star_num = (int) (Math.floor((stars_num - full_star_num) * 2));
        int blank_star_num = STAR - full_star_num - half_star_num;

        holder.removeAllViews();

        while (full_star_num-- > 0) {
            ImageView star = new ImageView(context);
            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_16dp));
            holder.addView(star);
        }
        while (half_star_num-- > 0) {
            ImageView star = new ImageView(context);
            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_half_16dp));
            holder.addView(star);
        }

        while (blank_star_num-- > 0) {
            ImageView star = new ImageView(context);
            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_blank_16dp));
            holder.addView(star);
        }
    }

    public static String join(String[] array, String separator) {
        String result = "";
        if (array.length > 0) {
            for (String a : array) {
                result += a + separator;
            }
            result = result.substring(0, result.length() - separator.length());
        }

        return result;
    }

    public static String join(List<String> list, String separator) {
        String result = "";
        if (list.size() > 0) {
            for (String l : list) {
                result += l + separator;
            }
            result = result.substring(0, result.length() - separator.length());
        }

        return result;
    }

    public static String doInBackground(String params) {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);

        df_ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df_hm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        df_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    void setCustomTheme(
            int color,
            int colorDark,
            CollapsingToolbarLayout collapsingToolbarLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            collapsingToolbarLayout.setContentScrimColor(color);
            collapsingToolbarLayout.setBackgroundColor(color);
            this.getWindow().setNavigationBarColor(color);
            this.getWindow().setStatusBarColor(colorDark);
        }
    }

    boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    float getScreenWidthInPd() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;

        return outMetrics.widthPixels / density;
    }

    float getProgressOfYear() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_YEAR, 1);
        long start = calendar.getTimeInMillis();

        calendar.add(Calendar.YEAR, 1);
        long end = calendar.getTimeInMillis();

        return Math.round(1000 * (now - start) / (end - start)) / 10f;
    }

    float getProgressOfDay() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long end = calendar.getTimeInMillis();

        return Math.round(1000 * (now - start) / (end - start)) / 10f;
    }

    void setProgressInPd(View view) {
        float scale = getResources().getDisplayMetrics().density;
        float screenWidth = getScreenWidthInPd();

        float p = getProgressOfDay() / 100f;
        if (p <= 0.2) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.navyGrayDark));
        } else if (p <= 0.4) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else if (p <= 0.6) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.blueSkyDark));
        } else if (p <= 0.8) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.orangeDark));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.tomatoDark));
        }

        int widthInPx = (int) (Math.round(p * screenWidth) * scale);

        view.requestLayout();
        view.getLayoutParams().width = widthInPx;
    }

    boolean needUpdateWeather() {
        int frequency = Integer.parseInt(settingPref.getString("update_frequency", "2"));

        String lastWeatherUpdate = sharedPref.getString("update_datetime", "");
        if (!lastWeatherUpdate.equals("")) {
            try {
                Date lastUpdateDate = df_ymd_hms.parse(lastWeatherUpdate);
                return new Date().getTime() - lastUpdateDate.getTime() > frequency * 60 * 60 * 1000;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public String getTextDayNight(String text_day, String text_night) {

        if (text_day.equals(text_night)) {
            return text_day;
        } else {
            return String.format(getString(R.string.weather_info),
                    text_day,
                    text_night
            );
        }
    }

    public boolean checkEmpty(DBHelper dbHelper, String tableName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();
        db.close();
        return isEmpty;
    }

    public long insertMovieDB(DBHelper dbHelper, Object obj) {
        MovieBaseBean mbb = (MovieBaseBean) obj;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.COLUMN_NAME_ID, mbb.getId());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, mbb.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, mbb.getOriginal_title());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_IMAGES, mbb.getImages().getLarge());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_ALT, mbb.getAlt());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_YEAR, mbb.getYear());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_STARS, mbb.getRating().getStarts());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_AVERAGE, mbb.getRating().getAverage());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_SUMMARY, "");

        return db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
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

    @Override
    public void onLikeMovie(MovieBean movie) {
        DBHelper dbHelper = new DBHelper(this);

        if (checkEmpty(dbHelper, MovieContract.MovieEntry.TABLE_NAME)) {
            onConfirmDownloadMovie();
            return;
        }

        String info;
        if (movie != null && !movie.getId().equals("")) {
            if (insertMovieDB(dbHelper, movie) == -1) {
                info = getString(R.string.like_movie_already_exist);
            } else {
                info = getString(R.string.like_movie_success);
            }
        } else {
            info = getString(R.string.error);
        }

        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    }

    public void onConfirmDownloadMovie() {
    }

    public void doubanErrorAPI() {
        Toast.makeText(
                this,
                getString(R.string.douban_error),
                Toast.LENGTH_LONG
        ).show();
    }
}

