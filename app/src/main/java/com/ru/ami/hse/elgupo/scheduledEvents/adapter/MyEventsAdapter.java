package com.ru.ami.hse.elgupo.scheduledEvents.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ru.ami.hse.elgupo.eventFeed.adapter.RecyclerViewInterface;
import com.ru.ami.hse.elgupo.eventFeed.utils.CategoryUtils;
import com.ru.ami.hse.elgupo.map.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private List<Event> eventsList;

    public MyEventsAdapter(RecyclerViewInterface recyclerViewInterface) {
        this.eventsList = new ArrayList<>();
        this.recyclerViewInterface = recyclerViewInterface;
    }


    private void setUpEventCategory(@NonNull MyEventsAdapter.MyViewHolder holder, Event event) {
        Category category = Category.getCategoryById(event.getCatId());
        holder.category.setText(category.getTitle());

        int colorResId = CategoryUtils.categoryColor(category);
        GradientDrawable background = (GradientDrawable) holder.category.getBackground();
        background.setColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evet_feed_item, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = eventsList.get(position);

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
        return eventsList.size();
    }

    public void updateEvents(List<Event> events) {
        this.eventsList = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Event getEventAtPosition(int position) {
        return eventsList.get(position);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView category;
        TextView eventName;
        TextView eventDateStart;
        TextView eventDateEnd;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
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
