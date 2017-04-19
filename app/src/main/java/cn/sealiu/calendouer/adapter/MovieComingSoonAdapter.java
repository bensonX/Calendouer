package cn.sealiu.calendouer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import cn.sealiu.calendouer.MainActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.MovieBaseBean;
import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/4/18.
 */

public class MovieComingSoonAdapter extends RecyclerView.Adapter<MovieComingSoonAdapter.ViewHolder> {

    private List<MovieBean> dataset;
    private Context context;

    public MovieComingSoonAdapter(List<MovieBean> dataset) {
        this.dataset = dataset;
    }

    @Override
    public MovieComingSoonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.movie_coming_soon_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieComingSoonAdapter.ViewHolder holder, int position) {
        final MovieBaseBean movie = dataset.get(position);
        Glide.with(context).load(movie.getImages().getLarge()).into(holder.poster);
        holder.title.setText(movie.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).openMovieFragment(new Gson().toJson(movie), "coming_soon");
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

        ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.poster_coming_soon);
            title = (TextView) v.findViewById(R.id.title_coming_soon);
        }
    }
}
