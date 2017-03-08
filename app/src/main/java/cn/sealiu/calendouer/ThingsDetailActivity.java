package cn.sealiu.calendouer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.sealiu.calendouer.model.Thing;

public class ThingsDetailActivity extends CalendouerActivity implements View.OnClickListener {

    private TextView titleTV;
    private TextView dateTV;
    private TextView timeTV;
    private FloatingActionButton fab;
    private Thing thing;
    private int color, colorDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_detail);

        titleTV = (TextView) findViewById(R.id.title);
        dateTV = (TextView) findViewById(R.id.date);
        timeTV = (TextView) findViewById(R.id.time);
        fab = (FloatingActionButton) findViewById(R.id.modify_fab);

        thing = (Thing) getIntent().getSerializableExtra("thing");
        color = getIntent().getIntExtra("color", ContextCompat.getColor(this, R.color.colorPrimary));
        colorDark = getIntent().getIntExtra("colorDark", ContextCompat.getColor(this, R.color.colorPrimaryDark));

        if (thing != null) {
            initView();
        } else {
            finish();
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setCustomTheme(color, colorDark, fab, collapsingToolbarLayout);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.modify_fab) {
            Intent intent = new Intent(this, AddThingsActivity.class);
            intent.putExtra("thing", thing);
            intent.putExtra("color", color);
            intent.putExtra("colorDark", colorDark);
            startActivityForResult(intent, MODIFY_THINGS_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_THINGS_CODE && resultCode == RESULT_OK) {
            Thing newThing = (Thing) data.getSerializableExtra("thing");
            if (newThing != null) {
                thing = newThing;
                initView();
            }
        }
    }

    private void initView() {
        fab.setOnClickListener(this);

        titleTV.setText(thing.getTitle());

        try {
            Date notificationDatetime = df_ymd_hms.parse(thing.getNotification_datetime());
            Calendar nCalendar = Calendar.getInstance();

            nCalendar.setTime(notificationDatetime);

            String month = new SimpleDateFormat("MM", Locale.getDefault()).format(nCalendar.getTime());
            String day = new SimpleDateFormat("dd", Locale.getDefault()).format(nCalendar.getTime());

            String week = new SimpleDateFormat("E", Locale.getDefault()).format(nCalendar.getTime());

            String hour = new SimpleDateFormat("HH", Locale.getDefault()).format(nCalendar.getTime());
            String minute = new SimpleDateFormat("mm", Locale.getDefault()).format(nCalendar.getTime());

            dateTV.setText(
                    String.format(getString(R.string.things_date_show),
                            month,
                            day,
                            week)
            );

            timeTV.setText(String.format(getString(R.string.things_time_show), hour, minute));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
