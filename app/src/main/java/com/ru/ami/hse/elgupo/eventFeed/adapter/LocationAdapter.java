package com.ru.ami.hse.elgupo.eventFeed.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ru.ami.hse.elgupo.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final List<String> locationList;

    public LocationAdapter(List<String> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.locationText.setText(locationList.get(position));

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }


    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationText;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationText = itemView.findViewById(R.id.location_text);
        }
    }
}
