package cn.sealiu.calendouer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sealiu.calendouer.model.Thing;

/**
 * Created by liuyang
 * on 2017/3/5.
 */

public class ThingsItemAdapter extends RecyclerView.Adapter<ThingsItemAdapter.ViewHolder> {
    private List<Thing> dataSet;

    public ThingsItemAdapter(List<Thing> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTV.setText(dataSet.get(position).getTitle());
        holder.notificationTV.setText(dataSet.get(position).getNotification_datetime());
    }

    @Override
    public ThingsItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.things_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView titleTV;
        TextView notificationTV;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleTV = (TextView) itemView.findViewById(R.id.title);
            notificationTV = (TextView) itemView.findViewById(R.id.notification_datetime);
        }
    }
}
