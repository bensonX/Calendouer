package cn.sealiu.calendouer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/3/4.
 */

public class MovieFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private final static int STAR = 5;
    MovieListener listener;
    private ImageView imageIV;
    private TextView ratingTV;
    private TextView titleTV;
    private TextView celebrityTV;
    private TextView summaryTV;
    private TextView ratingsCountTV;
    private LinearLayout starsLayout;
    private String alt;
    private String image_uri;
    private String directors = "";
    private String casts = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MovieListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement MovieFragment");
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_movie_bottom_sheet, null);
        dialog.setContentView(view);

        view.findViewById(R.id.bs_goto_douban).setOnClickListener(this);
        AppCompatButton addThingsBtn = (AppCompatButton) view.findViewById(R.id.bs_add_things);

        Log.d("Bottom_Sheet", "setupDialog");
        SharedPreferences settingPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (settingPref.getBoolean("things_show", true)) {
            addThingsBtn.setOnClickListener(this);
        } else {
            addThingsBtn.setOnClickListener(null);
            addThingsBtn.setVisibility(View.GONE);
        }

        imageIV = (ImageView) view.findViewById(R.id.bs_image);
        titleTV = (TextView) view.findViewById(R.id.bs_movie_title);
        ratingTV = (TextView) view.findViewById(R.id.bs_rating__average);
        celebrityTV = (TextView) view.findViewById(R.id.bs_celebrity);
        summaryTV = (TextView) view.findViewById(R.id.bs_summary);
        starsLayout = (LinearLayout) view.findViewById(R.id.bs_rating__stars_holder);
        ratingsCountTV = (TextView) view.findViewById(R.id.bs_ratings_count);

        String movieJson = getArguments().getString("movie");
        if (movieJson != null && !movieJson.equals("")) {
            final MovieBean movieBean = new Gson().fromJson(movieJson, MovieBean.class);

            titleTV.setText(movieBean.getTitle());
            ratingTV.setText(Float.toString(movieBean.getRating().getAverage()));
            summaryTV.setText(movieBean.getSummary());

            ratingsCountTV.setText(String.format(getString(R.string.ratings_count), movieBean.getRatings_count() + ""));

            alt = movieBean.getAlt();
            image_uri = movieBean.getImages().getLarge();

            double stars_num = Double.parseDouble(movieBean.getRating().getStarts()) / 10;

            int full_star_num = (int) Math.floor(stars_num);
            int half_star_num = (int) (Math.floor((stars_num - full_star_num) * 2));
            int blank_star_num = STAR - full_star_num - half_star_num;

            starsLayout.removeAllViews();

            while (full_star_num-- > 0) {
                ImageView star = new ImageView(getActivity());
                star.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_16dp));
                starsLayout.addView(star);
            }
            while (half_star_num-- > 0) {
                ImageView star = new ImageView(getActivity());
                star.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_half_16dp));
                starsLayout.addView(star);
            }

            while (blank_star_num-- > 0) {
                ImageView star = new ImageView(getActivity());
                star.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_blank_16dp));
                starsLayout.addView(star);
            }

            Glide.with(this).load(image_uri).into(imageIV);

            for (int i = 0; i < movieBean.getDirectors().length; i++) {
                Log.d("douban", movieBean.getDirectors()[i].getName());
                directors += movieBean.getDirectors()[i].getName() + "/";
            }

            for (int i = 0; i < movieBean.getCasts().length; i++) {
                Log.d("douban", movieBean.getCasts()[i].getName());
                casts += movieBean.getCasts()[i].getName() + "/";
            }

            String celebrityStr = String.format(getString(R.string.directors), directors.substring(0, directors.length() - 1)) +
                    "\n" + String.format(getString(R.string.casts), casts.substring(0, casts.length() - 1));
            celebrityTV.setText(celebrityStr);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("Bottom_Sheet", "onCreateDialog");
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bs_goto_douban:
                if (alt != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(alt));
                    startActivity(intent);
                }
                break;
            case R.id.bs_add_things:
                listener.onAddThings();
                dismiss();
                break;
        }
    }

    public interface MovieListener {
        void onAddThings();
    }
}
