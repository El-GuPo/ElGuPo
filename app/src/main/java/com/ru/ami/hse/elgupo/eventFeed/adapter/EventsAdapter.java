package com.ru.ami.hse.elgupo.eventFeed.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.map.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Map<Integer, List<Event>> eventsByCategory;
    private List<Event> events;

    public EventsAdapter() {
        this.events = new ArrayList<>();
        this.eventsByCategory = new HashMap<>();
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evet_feed_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.eventName.setText(event.getName());
        holder.eventDateStart.setText("Начало: " + DateUtils.convertTimestampToTime(event.getDateStart()));
        holder.eventDateEnd.setText("Окончание: " + DateUtils.convertTimestampToTime(event.getDateEnd()));

        if (event.getLogo() != null && !event.getLogo().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getLogo())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .into(holder.logo);
        } else {
            holder.logo.setImageResource(R.drawable.ic_default_image);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(Map<Integer, List<Event>> eventsByCategory) {
        this.eventsByCategory = eventsByCategory == null ? new HashMap<>() : eventsByCategory;
        events.clear();
        for (var elem : eventsByCategory.values()) {
            if (elem != null) {
                events.addAll(elem);
            }
        }
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView eventName;
        TextView eventDateStart;
        TextView eventDateEnd;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.eventFeedItemLogo);
            eventName = itemView.findViewById(R.id.event_name);
            eventDateStart = itemView.findViewById(R.id.event_date_start);
            eventDateEnd = itemView.findViewById(R.id.event_date_end);
        }
    }
}
