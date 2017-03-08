package cn.sealiu.calendouer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cn.sealiu.calendouer.model.Thing;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.ThingsContract;

public class AddThingsActivity extends CalendouerActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private Button dateBTN;
    private Button timeBTN;
    private EditText contentET;

    private String mContent;
    private String mYear;
    private String mMonth;
    private String mDay;
    private String mWeek;

    private String mHour;
    private String mMinute;
    private Thing thing;
    private String movieTitle;
    private boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_things);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dateBTN = (Button) findViewById(R.id.date);
        timeBTN = (Button) findViewById(R.id.time);
        contentET = (EditText) findViewById(R.id.content);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        dateBTN.setOnClickListener(this);
        timeBTN.setOnClickListener(this);

        movieTitle = getIntent().getStringExtra("movie_title");
        thing = (Thing) getIntent().getSerializableExtra("thing");
        int color = getIntent().getIntExtra("color", ContextCompat.getColor(this, R.color.colorPrimary));
        int colorDark = getIntent().getIntExtra("colorDark", ContextCompat.getColor(this, R.color.colorPrimaryDark));

        setCustomTheme(color, colorDark, collapsingToolbarLayout);

        initView();
    }

    private void initView() {
        Calendar calendar = Calendar.getInstance();

        if (thing != null) {
            isNew = false;
            contentET.setText(thing.getTitle());
            contentET.setSelection(thing.getTitle().length());
            try {
                Date notificationDatetime = df_ymd_hms.parse(thing.getNotification_datetime());
                calendar.setTime(notificationDatetime);
            } catch (ParseException e) {
                e.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            }

        } else if (movieTitle != null && !movieTitle.equals("")) {
            String str = String.format(getString(R.string.add_things_do_movie), movieTitle);
            contentET.setText(str);
            contentET.setSelection(str.length());
            calendar.add(Calendar.MINUTE, 30);
        }

        mYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        mMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
        mDay = new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime());

        mWeek = new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime());

        mHour = new SimpleDateFormat("HH", Locale.getDefault()).format(calendar.getTime());
        mMinute = new SimpleDateFormat("mm", Locale.getDefault()).format(calendar.getTime());

        String date = String.format(getResources().getString(R.string.default_date_picker), mYear, mMonth, mDay, mWeek);
        dateBTN.setText(date);
        String time = String.format(getResources().getString(R.string.default_time_picker), mHour, mMinute);
        timeBTN.setText(time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            mContent = contentET.getText().toString();
            if (TextUtils.isEmpty(mContent)) {
                contentET.setError(getString(R.string.content_require));
                Snackbar.make(contentET, getString(R.string.content_require), Snackbar.LENGTH_SHORT).show();
                return true;
            }

            if (saveThings()) {
                // set new notification alarm
                setThingAlarm(
                        thing.getId(),
                        thing.getNotification_datetime(),
                        thing.getRequest_code()
                );

                Intent resultIntent = new Intent();
                resultIntent.putExtra("thing", thing);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                setResult(RESULT_CANCELED);
                Snackbar.make(contentET, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveThings() {
        String dateStr = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":" + 0;

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (isNew) {
            thing = new Thing(
                    UUID.randomUUID().toString(),
                    mContent,
                    df_ymd_hms.format(new Date()),
                    dateStr,
                    0,
                    0,
                    (int) (new Date().getTime())
            );
            Log.d("Things", "new thing: " + thing.toString());
        } else {
            thing.setTitle(mContent);
            thing.setDatetime(df_ymd_hms.format(new Date()));
            thing.setNotification_datetime(dateStr);
            Log.d("Things", "non-new thing: " + thing.toString());
        }

        ContentValues values = new ContentValues();
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_ID, thing.getId());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_TITLE, thing.getTitle());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_DATETIME, thing.getDatetime());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME, thing.getNotification_datetime());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_TIME_ADVANCE, thing.getTime_advance());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_DONE, thing.getDone());
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_REQUEST_CODE, thing.getRequest_code());

        if (isNew) {
            return db.insert(ThingsContract.ThingsEntry.TABLE_NAME, null, values) != -1;
        } else {
            if (db.update(
                    ThingsContract.ThingsEntry.TABLE_NAME,
                    values,
                    ThingsContract.ThingsEntry.COLUMN_NAME_ID + "=?",
                    new String[]{thing.getId()}) != 0) {
                //cancel old notification alarm
                cancelThingAlarm(thing.getId(), thing.getRequest_code());
                return true;
            }
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        AddThingsActivity.this,
                        Integer.parseInt(mYear),
                        Integer.parseInt(mMonth) - 1,
                        Integer.parseInt(mDay)
                );
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.show(getFragmentManager(), "DATE_PICKER");
                break;
            case R.id.time:
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        AddThingsActivity.this,
                        Integer.parseInt(mHour),
                        Integer.parseInt(mMinute),
                        true
                );
                timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
                timePickerDialog.show(getFragmentManager(), "TIME_PICKER");
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        try {
            final Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            mYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
            mMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
            mDay = new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime());

            mWeek = new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime());

            String str = String.format(getResources().getString(R.string.default_date_picker), mYear, mMonth, mDay, mWeek);
            dateBTN.setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateStr = mYear + "-" + mMonth + "-" + mDay + " " + hourOfDay + ":" + minute;

        final Date date;
        try {
            date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            mHour = new SimpleDateFormat("HH", Locale.getDefault()).format(calendar.getTime());
            mMinute = new SimpleDateFormat("mm", Locale.getDefault()).format(calendar.getTime());

            String str = String.format(getResources().getString(R.string.default_time_picker), mHour, mMinute);
            timeBTN.setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
