package cn.sealiu.calendouer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import cn.sealiu.calendouer.adapter.MovieSubjectAdapter;
import cn.sealiu.calendouer.adapter.MovieWorkAdapter;
import cn.sealiu.calendouer.bean.CelebrityBaseBean;
import cn.sealiu.calendouer.bean.CelebrityBean;
import cn.sealiu.calendouer.bean.DoubanMovies;
import cn.sealiu.calendouer.bean.MovieBean;
import cn.sealiu.calendouer.fragment.MovieFragment;

public class CelebrityActivity extends CalendouerActivity implements
        MovieFragment.LikeMovieListener, View.OnClickListener {

    String type;
    MovieBean todayMovie;

    Toolbar toolbar;
    TextView celebrityNameTV, genderTV, bornPlaceTV, akaEnTV, akaTV, subjectTitleTV;
    ImageView celebrityImageIV;
    Button gotoDouban, subjectMoreBtn;
    ProgressBar subjectPB;

    RecyclerView recyclerView;
    LinearLayout celebrityInfo, ratingUs;

    AsyncTask getCelebrityTask, getGenreTask, getSameNameTask;

    NativeExpressAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrity);

        type = getIntent().getStringExtra("type");

        todayMovie = new Gson().fromJson(
                sharedPref.getString("movie_json", ""),
                MovieBean.class
        );
        if (todayMovie == null) {
            finish();
        }

        subjectPB = (ProgressBar) findViewById(R.id.subject_loading);

        celebrityImageIV = (ImageView) findViewById(R.id.celebrity_image);
        celebrityNameTV = (TextView) findViewById(R.id.celebrity_name);
        genderTV = (TextView) findViewById(R.id.gender);
        bornPlaceTV = (TextView) findViewById(R.id.born_place);
        akaEnTV = (TextView) findViewById(R.id.aka_en);
        akaTV = (TextView) findViewById(R.id.aka);
        subjectTitleTV = (TextView) findViewById(R.id.subject_title);
        gotoDouban = (Button) findViewById(R.id.goto_douban);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.subject_recycler_view);
        subjectMoreBtn = (Button) findViewById(R.id.subject_more);
        celebrityInfo = (LinearLayout) findViewById(R.id.celebrity_info);
        ratingUs = (LinearLayout) findViewById(R.id.rating_us_card);
        adView = (NativeExpressAdView) findViewById(R.id.adView);

        findViewById(R.id.after_ad_closed_rate).setOnClickListener(this);
        findViewById(R.id.after_ad_closed_restore).setOnClickListener(this);

        initView();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // ad
        if (settingPref.getBoolean("ad_show", true)) {
            adView.setVisibility(View.VISIBLE);
            ratingUs.setVisibility(View.GONE);
            initAd();
        } else {
            adView.setVisibility(View.GONE);
            ratingUs.setVisibility(View.VISIBLE);
        }
    }

    private void initAd() {
        ratingUs.setVisibility(View.GONE);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("43FE98603DD8DD9E449808D85C7DBD45")
                .build();
        adView.loadAd(request);
        adView.setVisibility(View.VISIBLE);
    }

    private void initView() {
        switch (type) {
            case "director":
                final CelebrityBaseBean director = todayMovie.getDirectors()[0];
                toolbar.setTitle(String.format(
                        getString(R.string.celebrity_toolbar_director_title),
                        director.getName()
                ));

                Glide.with(this).load(director.getAvatars().getLarge()).into(celebrityImageIV);
                celebrityNameTV.setText(director.getName());
                subjectTitleTV.setText(String.format(
                        getString(R.string.celebrity_title),
                        director.getName()
                ));
                completeCelebrityInfo(director.getId(), director.getAlt());
                break;
            case "casts1":
                CelebrityBaseBean casts1 = todayMovie.getCasts()[0];
                toolbar.setTitle(String.format(
                        getString(R.string.celebrity_toolbar_casts_title),
                        casts1.getName()
                ));

                Glide.with(this).load(casts1.getAvatars().getLarge()).into(celebrityImageIV);
                celebrityNameTV.setText(casts1.getName());
                subjectTitleTV.setText(String.format(
                        getString(R.string.celebrity_title),
                        casts1.getName()
                ));
                completeCelebrityInfo(casts1.getId(), casts1.getAlt());
                break;
            case "casts2":
                CelebrityBaseBean casts2 = todayMovie.getCasts()[1];
                toolbar.setTitle(String.format(
                        getString(R.string.celebrity_toolbar_casts_title),
                        casts2.getName()
                ));

                Glide.with(this).load(casts2.getAvatars().getLarge()).into(celebrityImageIV);
                celebrityNameTV.setText(casts2.getName());
                subjectTitleTV.setText(String.format(
                        getString(R.string.celebrity_title),
                        casts2.getName()
                ));
                completeCelebrityInfo(casts2.getId(), casts2.getAlt());
                break;
            case "genres":
                celebrityInfo.setVisibility(View.GONE);

                String geners = todayMovie.getGenres()[0];
                if (todayMovie.getGenres().length > 1) {
                    geners += "，" + todayMovie.getGenres()[1];
                }

                toolbar.setTitle(String.format(
                        getString(R.string.celebrity_toolbar_genres_title),
                        geners
                ));

                subjectTitleTV.setText(String.format(
                        getString(R.string.genres_title),
                        geners
                ));

                completeGenreInfo(geners);
                break;
            case "same_name":
                celebrityInfo.setVisibility(View.GONE);

                toolbar.setTitle(String.format(
                        getString(R.string.celebrity_toolbar_same_name_title),
                        todayMovie.getTitle()
                ));

                subjectTitleTV.setText(String.format(
                        getString(R.string.same_name_title),
                        todayMovie.getTitle()
                ));

                completeSameNameInfo(todayMovie.getTitle());
                break;
        }
    }

    private void completeCelebrityInfo(final String id, final String alt) {
        if (alt != null) {
            gotoDouban.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!alt.equals("")) {
                        openDouban(alt);
                    }
                }
            });
        }

        if (id != null) {
            getCelebrityTask = new GetCelebrityInfo().execute("http://api.douban.com/v2/movie/celebrity/" + id);
            subjectMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDouban("https://movie.douban.com/celebrity/" + id + "/movies");
                }
            });
        }
    }

    private void completeGenreInfo(final String tag) {
        getGenreTask = new GetGenreInfo().execute(
                "http://api.douban.com/v2/movie/search?tag=" + tag + "&count=20"
        );

        subjectMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDouban("https://movie.douban.com/tag/" + tag.replace("，", "%20"));
            }
        });
    }

    private void completeSameNameInfo(String search) {
        getSameNameTask = new GetSameNameInfo().execute(
                "http://api.douban.com/v2/movie/search?q=" + search + "&count=20"
        );
    }

    private void openDouban(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getCelebrityTask != null)
            getCelebrityTask.cancel(true);
        if (getGenreTask != null)
            getGenreTask.cancel(true);
        if (getSameNameTask != null)
            getSameNameTask.cancel(true);
    }

    private void setSubjectMoreBtn(int total) {
        if (total <= 20) {
            subjectMoreBtn.setText(getString(R.string.subject_no_more));
            subjectMoreBtn.setEnabled(false);
            subjectMoreBtn.setTextColor(ContextCompat.getColor(this, R.color.disabled));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.after_ad_closed_rate:
                Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    "http://play.google.com/store/apps/details?id=" +
                                            getApplication().getPackageName()
                            )
                    ));
                }
                break;
            case R.id.after_ad_closed_restore:
                settingPref.edit().putBoolean("ad_show", true).apply();
                initAd();
                break;
            default:
                break;
        }
    }

    private class GetCelebrityInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return CalendouerActivity.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                CelebrityBean celebrityBean = new Gson().fromJson(s, CelebrityBean.class);
                celebrityNameTV.setText(celebrityBean.getName() + " " + celebrityBean.getName_en());
                genderTV.setText(String.format(
                        getString(R.string.gender),
                        celebrityBean.getGender()
                ));
                bornPlaceTV.setText(String.format(
                        getString(R.string.born_place),
                        celebrityBean.getBorn_place()
                ));
                akaEnTV.setText(String.format(
                        getString(R.string.aka_en),
                        CalendouerActivity.join(celebrityBean.getAka_en(), "/")
                ));
                akaTV.setText(String.format(
                        getString(R.string.aka),
                        CalendouerActivity.join(celebrityBean.getAka(), "/")
                ));

                GridLayoutManager gridLayoutMgr = new GridLayoutManager(CelebrityActivity.this, 3);
                recyclerView.setLayoutManager(gridLayoutMgr);
                MovieWorkAdapter workAdapter = new MovieWorkAdapter(
                        Arrays.asList(celebrityBean.getWorks())
                );
                recyclerView.setAdapter(workAdapter);
            } else {
                Toast.makeText(
                        CelebrityActivity.this,
                        getString(R.string.douban_error),
                        Toast.LENGTH_LONG
                ).show();
            }
            subjectPB.setVisibility(View.GONE);
            subjectMoreBtn.setVisibility(View.VISIBLE);
        }
    }

    private class GetGenreInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return CalendouerActivity.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                DoubanMovies doubanMovies = new Gson().fromJson(s, DoubanMovies.class);
                final List<MovieBean> dataset = Arrays.asList(doubanMovies.getSubjects());

                GridLayoutManager gridLayoutMgr = new GridLayoutManager(CelebrityActivity.this, 4);
                recyclerView.setLayoutManager(gridLayoutMgr);
                MovieSubjectAdapter adapter = new MovieSubjectAdapter(dataset) {
                    @Override
                    public void onBindViewHolder(MovieSubjectAdapter.ViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);
                        MovieBean movie = dataset.get(position);
                        holder.title.setText(movie.getTitle() + "\n" + join(movie.getGenres(), "/"));
                    }
                };
                recyclerView.setAdapter(adapter);

                setSubjectMoreBtn(doubanMovies.getTotal());
            } else {
                Toast.makeText(
                        CelebrityActivity.this,
                        getString(R.string.douban_error),
                        Toast.LENGTH_LONG
                ).show();
            }
            subjectPB.setVisibility(View.GONE);
            subjectMoreBtn.setVisibility(View.VISIBLE);
        }
    }

    private class GetSameNameInfo extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return CalendouerActivity.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                DoubanMovies doubanMovies = new Gson().fromJson(s, DoubanMovies.class);
                final List<MovieBean> dataset = Arrays.asList(doubanMovies.getSubjects());

                GridLayoutManager gridLayoutMgr = new GridLayoutManager(CelebrityActivity.this, 4);
                recyclerView.setLayoutManager(gridLayoutMgr);

                MovieSubjectAdapter adapter = new MovieSubjectAdapter(dataset) {
                    @Override
                    public void onBindViewHolder(ViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);
                        MovieBean movie = dataset.get(position);
                        holder.title.setText(movie.getTitle());
                    }
                };
                recyclerView.setAdapter(adapter);

                setSubjectMoreBtn(doubanMovies.getTotal());
            } else {
                Toast.makeText(
                        CelebrityActivity.this,
                        getString(R.string.douban_error),
                        Toast.LENGTH_LONG
                ).show();
            }
            subjectPB.setVisibility(View.GONE);
            subjectMoreBtn.setVisibility(View.VISIBLE);
        }
    }
}
