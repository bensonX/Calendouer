package cn.sealiu.calendouer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sealiu.calendouer.model.Thing;
import co.dift.ui.SwipeToAction;

/**
 * Created by liuyang
 * on 2017/3/5.
 */

public class ThingsItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Thing> dataSet;

    public ThingsItemAdapter(List<Thing> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.things_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Thing thing = dataSet.get(position);
        ViewHolder vh = (ViewHolder) holder;
        vh.titleTV.setText(thing.getTitle());
        vh.notificationTV.setText(dataSet.get(position).getNotification_datetime().substring(0, 16));
        vh.data = thing;
    }

    public class ViewHolder extends SwipeToAction.ViewHolder<Thing> {

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
