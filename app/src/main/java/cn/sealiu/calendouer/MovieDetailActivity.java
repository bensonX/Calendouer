package cn.sealiu.calendouer;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import cn.sealiu.calendouer.bean.MovieBaseBean;

public class MovieDetailActivity extends CalendouerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        String movieJ = getIntent().getStringExtra("movie");
        MovieBaseBean movie = new Gson().fromJson(movieJ, MovieBaseBean.class);

        ImageView poster = (ImageView) findViewById(R.id.poster_detail);
        Glide.with(this).load(movie.getImages().getLarge()).into(poster);
    }
}
