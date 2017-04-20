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

import cn.sealiu.calendouer.CelebrityActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.MovieBean;

/**
 * Created by liuyang
 * on 2017/4/20.
 */

public class MovieSubjectAdapter extends RecyclerView.Adapter<MovieSubjectAdapter.ViewHolder> {

    private List<MovieBean> dataset;
    private Context context;

    public MovieSubjectAdapter(List<MovieBean> dataset) {
        this.dataset = dataset;
    }

    @Override
    public MovieSubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.work_item, parent, false);
        return new MovieSubjectAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieSubjectAdapter.ViewHolder holder, int position) {
        final MovieBean movie = dataset.get(position);

        Glide.with(context).load(movie.getImages().getLarge()).into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CelebrityActivity) context).openMovieFragment(new Gson().toJson(movie), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        ImageView poster;

        public ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.poster_work);
            title = (TextView) v.findViewById(R.id.title_work);
        }
    }
}
