package cn.sealiu.calendouer.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.sealiu.calendouer.CalendouerActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.DoubanMovies;
import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/3/4.
 */

public class MovieFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    MovieBean movie;
    String from;
    LikeMovieListener listener;
    AsyncTask task;
    private ImageView imageIV;
    private TextView ratingTV;
    private TextView titleTV;
    private TextView celebrityTV;
    private TextView summaryTV;
    private TextView ratingsCountTV;
    private LinearLayout starsLayout;
    private String alt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LikeMovieListener) context;
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
        view.findViewById(R.id.bs_like_movie).setOnClickListener(this);


        imageIV = (ImageView) view.findViewById(R.id.bs_image);
        titleTV = (TextView) view.findViewById(R.id.bs_movie_title);
        ratingTV = (TextView) view.findViewById(R.id.bs_rating__average);
        celebrityTV = (TextView) view.findViewById(R.id.bs_celebrity);
        summaryTV = (TextView) view.findViewById(R.id.bs_summary);
        starsLayout = (LinearLayout) view.findViewById(R.id.bs_rating__stars_holder);
        ratingsCountTV = (TextView) view.findViewById(R.id.bs_ratings_count);

        String movieJson = getArguments().getString("movie");
        from = getArguments().getString("from");

        if (movieJson != null && !movieJson.equals("")) {
            movie = new Gson().fromJson(movieJson, MovieBean.class);
            if (movie.getSummary() == null || movie.getSummary().equals("")) {
                completeMovieInfo(movie.getId());
            } else {
                setMovieInfo(movie);
            }
        } else {
            dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
                LikeMovieListener likeMovieListener = (LikeMovieListener) getActivity();
                likeMovieListener.onLikeMovie(movie);
                dismiss();
                break;
        }
    }

    private void completeMovieInfo(String id) {
        task = new GetMovieInfo().execute("http://api.douban.com/v2/movie/subject/" + id);
    }

    private void setMovieInfo(MovieBean movieBean) {
        List<String> directors = new ArrayList<>();
        List<String> casts = new ArrayList<>();

        titleTV.setText(movieBean.getTitle());
        ratingTV.setText(Float.toString(movieBean.getRating().getAverage()));
        summaryTV.setText(movieBean.getSummary());

        ratingsCountTV.setText(String.format(
                getString(R.string.ratings_count), movieBean.getRatings_count() + ""
        ));

        alt = movieBean.getAlt();

        CalendouerActivity.setRatingStar(
                getActivity(),
                starsLayout,
                movieBean.getRating().getStarts()
        );

        Glide.with(this).load(movieBean.getImages().getLarge()).into(imageIV);

        for (int i = 0; i < movieBean.getDirectors().length; i++) {
            directors.add(movieBean.getDirectors()[i].getName());
        }

        for (int i = 0; i < movieBean.getCasts().length; i++) {
            casts.add(movieBean.getCasts()[i].getName());
        }

        String celebrityStr = String.format(
                getString(R.string.directors),
                CalendouerActivity.join(directors, "/")
        ) + "\n" + String.format(
                getString(R.string.casts),
                CalendouerActivity.join(casts, "/")
        );
        celebrityTV.setText(celebrityStr);
    }

    private void updateSharedPref(MovieBean movieBean, String pref) {
        String prefStr = CalendouerActivity.sharedPref.getString(pref, "");
        DoubanMovies doubanMovies = new Gson().fromJson(prefStr, DoubanMovies.class);

        for (int i = 0; i < doubanMovies.getSubjects().length; i++) {
            if (doubanMovies.getSubjects()[i].getId().equals(movieBean.getId())) {
                doubanMovies.getSubjects()[i] = movieBean;
            }
        }

        CalendouerActivity.sharedPref.edit()
                .putString(pref, new Gson().toJson(doubanMovies))
                .apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null)
            task.cancel(true);
    }

    public interface LikeMovieListener {
        void onLikeMovie(MovieBean movie);
    }

    private class GetMovieInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("MovieFragment", "GetMovieInfo");
            if (s != null && !s.equals("")) {
                MovieBean movieBean = new Gson().fromJson(s, MovieBean.class);
                setMovieInfo(movieBean);
                if (!from.equals(""))
                    updateSharedPref(movieBean, from);
            } else {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.douban_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

        }
    }
}
