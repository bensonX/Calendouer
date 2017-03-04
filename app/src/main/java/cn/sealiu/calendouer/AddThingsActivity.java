package cn.sealiu.calendouer;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddThingsActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_things);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            //window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String str = String.format(getString(R.string.add_things_do_content), extras.getString("movie_title"));
            contentET.setText(str);
            contentET.setSelection(str.length());
        }

        dateBTN.setOnClickListener(this);
        timeBTN.setOnClickListener(this);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 30);

        mYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(now.getTime());
        mMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(now.getTime());
        mDay = new SimpleDateFormat("dd", Locale.getDefault()).format(now.getTime());

        mWeek = new SimpleDateFormat("E", Locale.getDefault()).format(now.getTime());

        mHour = new SimpleDateFormat("HH", Locale.getDefault()).format(now.getTime());
        mMinute = new SimpleDateFormat("mm", Locale.getDefault()).format(now.getTime());

        String date = String.format(getResources().getString(R.string.default_date_picker), mYear, mMonth, mDay, mWeek);
        dateBTN.setText(date);
        String time = String.format(getResources().getString(R.string.default_time_picker), mHour, mMinute);
        timeBTN.setText(time);
    }

    @Override
    public void onClick(View v) {

    }
}
