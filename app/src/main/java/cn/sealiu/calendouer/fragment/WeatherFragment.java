package cn.sealiu.calendouer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.XzBean;
import cn.sealiu.calendouer.bean.XzLocationBean;
import cn.sealiu.calendouer.bean.XzResultsBean;
import cn.sealiu.calendouer.bean.XzWeatherBean;
import cn.sealiu.calendouer.until.WeatherIcon;

public class WeatherFragment extends DialogFragment {

    SharedPreferences sharedPref;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPref = getActivity().getSharedPreferences("calendouer", Context.MODE_PRIVATE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weather, null);

        WeatherIcon icons = new WeatherIcon();

        TextView city = (TextView) view.findViewById(R.id.city_name);
        TextView last_update = (TextView) view.findViewById(R.id.last_update);
        TextView wind = (TextView) view.findViewById(R.id.wind);
        TextView weather_info = (TextView) view.findViewById(R.id.weather_info);

        TextView weather_high_low_0 = (TextView) view.findViewById(R.id.weather_high_low_0);
        TextView weather_high_low_1 = (TextView) view.findViewById(R.id.weather_high_low_1);
        TextView weather_high_low_2 = (TextView) view.findViewById(R.id.weather_high_low_2);
        List<TextView> weather_high_low = new ArrayList<>();
        weather_high_low.add(weather_high_low_0);
        weather_high_low.add(weather_high_low_1);
        weather_high_low.add(weather_high_low_2);

        TextView date_0 = (TextView) view.findViewById(R.id.date_0);
        TextView date_1 = (TextView) view.findViewById(R.id.date_1);
        TextView date_2 = (TextView) view.findViewById(R.id.date_2);
        List<TextView> date = new ArrayList<>();
        date.add(date_0);
        date.add(date_1);
        date.add(date_2);

        ImageView weather_icon_0 = (ImageView) view.findViewById(R.id.weather_icon_0);
        ImageView weather_icon_1 = (ImageView) view.findViewById(R.id.weather_icon_1);
        ImageView weather_icon_2 = (ImageView) view.findViewById(R.id.weather_icon_2);
        List<ImageView> weather_icon = new ArrayList<>();
        weather_icon.add(weather_icon_0);
        weather_icon.add(weather_icon_1);
        weather_icon.add(weather_icon_2);

        String weatherJson = getArguments().getString("weather");
        if (weatherJson != null && !weatherJson.equals("")) {
            XzBean xzBean = new Gson().fromJson(weatherJson, XzBean.class);
            XzResultsBean resultsBean = xzBean.getResults()[0];
            XzLocationBean locationBean = resultsBean.getLocation();
            XzWeatherBean[] weatherBeans = resultsBean.getDaily();

            city.setText(locationBean.getName());
            last_update.setText(
                    String.format(
                            getResources().getString(R.string.last_update),
                            sharedPref.getString("update_time", "")
                    )
            );
            wind.setText(
                    weatherBeans[0].getWind_speed() + "kph " +
                            weatherBeans[0].getWind_direction()
            );

            weather_info.setText(getTextDayNight(
                    weatherBeans[0].getText_day(),
                    weatherBeans[0].getText_night()
            ));

            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) { //day
                weather_icon_0.setImageDrawable(
                        ContextCompat.getDrawable(
                                getActivity(),
                                icons.map.get(weatherBeans[0].getCode_day())
                        )
                );
            } else {//night
                weather_icon_0.setImageDrawable(
                        ContextCompat.getDrawable(
                                getActivity(),
                                icons.map.get(weatherBeans[0].getCode_night())
                        )
                );
            }

            weather_high_low_0.setText(
                    String.format(
                            getResources().getString(R.string.weather_high_low),
                            weatherBeans[0].getHigh(),
                            weatherBeans[0].getLow()
                    )
            );

            date_0.setText(weatherBeans[0].getDate());

            for (int i = 1; i < 3; i++) {
                weather_icon.get(i).setImageDrawable(
                        ContextCompat.getDrawable(
                                getActivity(),
                                icons.map.get(weatherBeans[i].getCode_day())
                        )
                );

                weather_high_low.get(i).setText(
                        getTextDayNight(
                                weatherBeans[i].getText_day(),
                                weatherBeans[i].getText_night()
                        ) + String.format(
                                getResources().getString(R.string.weather_high_low),
                                weatherBeans[i].getHigh(),
                                weatherBeans[i].getLow()
                        )
                );

                date.get(i).setText(weatherBeans[i].getDate());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setTitle(getString(R.string.weather_preview));


        return builder.create();
    }

    private String getTextDayNight(String text_day, String text_night) {

        if (text_day.equals(text_night)) {
            return text_day;
        } else {
            return String.format(getString(R.string.weather_info),
                    text_day,
                    text_night
            );
        }
    }
}