package cn.sealiu.calendouer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import cn.sealiu.calendouer.CalendouerActivity;
import cn.sealiu.calendouer.MainActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/4/18.
 */

public class MovieInTheatersAdapter extends RecyclerView.Adapter<MovieInTheatersAdapter.ViewHolder> {

    private List<MovieBean> dataset;
    private Context context;

    public MovieInTheatersAdapter(List<MovieBean> dataset) {
        this.dataset = dataset;
    }

    @Override
    public MovieInTheatersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.movie_in_theaters_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieInTheatersAdapter.ViewHolder holder, int position) {
        final MovieBaseBean movie = dataset.get(position);
        Glide.with(context).load(movie.getImages().getLarge()).into(holder.poster);
        holder.title.setText(movie.getTitle());
        final MainActivity mainActivity = (MainActivity) context;
        CalendouerActivity.setRatingStar(context, holder.stars, movie.getRating().getStarts());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openMovieFragment(new Gson().toJson(movie), "in_theaters");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView poster;
        TextView title;
        LinearLayout stars;

        ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.poster_in_theaters);
            title = (TextView) v.findViewById(R.id.title_in_theaters);
            stars = (LinearLayout) v.findViewById(R.id.rating_stars_in_theaters);
        }
    }
}
