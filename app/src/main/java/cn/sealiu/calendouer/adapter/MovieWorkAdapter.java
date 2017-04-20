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

import cn.sealiu.calendouer.CalendouerActivity;
import cn.sealiu.calendouer.CelebrityActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.WorksBean;

/**
 * Created by liuyang
 * on 2017/4/20.
 */

public class MovieWorkAdapter extends RecyclerView.Adapter<MovieWorkAdapter.ViewHolder> {

    private List<WorksBean> dataset;
    private Context context;

    public MovieWorkAdapter(List<WorksBean> dataset) {
        this.dataset = dataset;
    }

    @Override
    public MovieWorkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.work_item, parent, false);
        return new MovieWorkAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieWorkAdapter.ViewHolder holder, int position) {
        final WorksBean works = dataset.get(position);

        Glide.with(context).load(works.getSubject().getImages().getLarge()).into(holder.poster);
        holder.title.setText(
                works.getSubject().getTitle() + "\n" +
                        CalendouerActivity.join(works.getRoles(), "/")
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CelebrityActivity) context).openMovieFragment(new Gson().toJson(works.getSubject()), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title;

        public ViewHolder(View v) {
            super(v);
            poster = (ImageView) v.findViewById(R.id.poster_work);
            title = (TextView) v.findViewById(R.id.title_work);
        }
    }
}
