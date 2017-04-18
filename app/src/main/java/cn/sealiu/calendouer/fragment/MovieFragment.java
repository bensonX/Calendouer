package cn.sealiu.calendouer.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/3/4.
 */

public class MovieFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private final static int STAR = 5;
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
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_movie_bottom_sheet, null);
        dialog.setContentView(view);

        view.findViewById(R.id.bs_goto_douban).setOnClickListener(this);
        view.findViewById(R.id.bs_like_movie).setOnClickListener(this);


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
            case R.id.bs_like_movie:
                // TODO: 2017/4/18 add this movie to movie db and record this movie's features
                break;
        }
    }
}
