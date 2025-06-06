package com.ru.ami.hse.elgupo.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.map.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> events;

    public EventsAdapter(List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_popup_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        Context context = holder.itemView.getContext();

        holder.title.setText(event.getName());

        String sb = "Даты: " + DateUtils.convertTimestampToDate(event.getDateStart()) +
                " - " +
                DateUtils.convertTimestampToDate(event.getDateEnd());
        holder.time.setText(sb);

        if (event.getLogo() != null && !event.getLogo().isEmpty()) {
            Glide.with(context)
                    .load(event.getLogo())
                    .circleCrop()
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

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents != null ? newEvents : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView title;
        TextView time;
        ImageView moreButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.photoImageView);
            title = itemView.findViewById(R.id.event_title);
            time = itemView.findViewById(R.id.event_time);
            moreButton = itemView.findViewById(R.id.imageViewButton);
        }
    }
}
