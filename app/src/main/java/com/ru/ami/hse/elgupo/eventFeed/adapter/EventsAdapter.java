package com.ru.ami.hse.elgupo.eventFeed.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Category;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.utils.CategoryUtils;
import com.ru.ami.hse.elgupo.map.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> implements Filterable {
    private final RecyclerViewInterface recyclerViewInterface;
    private final List<Event> eventsList;
    private final List<Event> eventsListFiltered;
    private Map<Integer, List<Event>> eventsByCategory;

    public EventsAdapter(RecyclerViewInterface recyclerViewInterface) {
        this.eventsList = new ArrayList<>();
        this.eventsListFiltered = new ArrayList<>();
        this.eventsByCategory = new HashMap<>();
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evet_feed_item, parent, false);
        return new EventViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        Event event = eventsListFiltered.get(position);

        setUpEventCategory(holder, event);

        holder.eventName.setText(event.getName());
        StringBuilder sb = new StringBuilder("Начало: ");
        sb.append(DateUtils.convertTimestampToTime(event.getDateStart()));
        holder.eventDateStart.setText(sb.toString());
        sb = new StringBuilder("Окончание: ");
        sb.append(DateUtils.convertTimestampToTime(event.getDateEnd()));
        holder.eventDateEnd.setText(sb.toString());

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
        return eventsListFiltered.size();
    }

    public void updateEvents(Map<Integer, List<Event>> eventsByCategory) {
        this.eventsByCategory = eventsByCategory == null ? new HashMap<>() : eventsByCategory;
        eventsList.clear();
        for (var elem : eventsByCategory.values()) {
            if (elem != null) {
                eventsList.addAll(elem);
                eventsListFiltered.addAll(elem);
            }
        }
        eventsListFiltered.clear();
        eventsListFiltered.addAll(eventsList);
        notifyDataSetChanged();
    }

    private void setUpEventCategory(@NonNull EventsAdapter.EventViewHolder holder, Event event) {
        Category category = Category.getCategoryById(event.getCatId());
        holder.category.setText(category.getTitle());

        int colorResId = CategoryUtils.categoryColor(category);
        GradientDrawable background = (GradientDrawable) holder.category.getBackground();
        background.setColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));
    }

    public Event getEventAtPosition(int position) {
        return eventsListFiltered.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Event> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(eventsList);
                } else {
                    String searchChars = constraint.toString().toLowerCase().trim();
                    for (Event event : eventsListFiltered) {
                        if (event.getName().toLowerCase().contains(searchChars) || Category.getCategoryById(event.getCatId()).getTitle().toLowerCase().contains(searchChars)) {
                            filtered.add(event);
                        }
                    }

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventsListFiltered.clear();
                eventsListFiltered.addAll((List<Event>) results.values);
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView category;
        TextView eventName;
        TextView eventDateStart;
        TextView eventDateEnd;

        public EventViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            logo = itemView.findViewById(R.id.eventFeedItemLogo);
            category = itemView.findViewById(R.id.event_category);
            eventName = itemView.findViewById(R.id.event_name);
            eventDateStart = itemView.findViewById(R.id.event_date_start);
            eventDateEnd = itemView.findViewById(R.id.event_date_end);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
