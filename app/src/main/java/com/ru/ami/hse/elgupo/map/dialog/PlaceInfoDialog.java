package com.ru.ami.hse.elgupo.map.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.eventFeed.adapter.RecyclerViewInterface;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.map.adapter.EventsAdapter;

import java.util.List;

public class PlaceInfoDialog extends Dialog implements RecyclerViewInterface {
    private final Place place;
    private final MapActivity activity;
    private EventsAdapter adapter;

    public PlaceInfoDialog(@NonNull Context context, Place place, MapActivity activity) {
        super(context, R.style.RoundedDialogTheme);
        this.place = place;
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState != null
                ? savedInstanceState
                : new Bundle());


        setContentView(R.layout.popup_layout);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        TextView title = findViewById(R.id.popup_title);
        title.setText(place.getName());

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView eventsRecycler = findViewById(R.id.events_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        eventsRecycler.setLayoutManager(layoutManager);

        adapter = new EventsAdapter(place.getEvents(), this);
        eventsRecycler.setAdapter(adapter);
    }

    public void updateEvents() {
        List<Event> newEvents = place.getEvents();
        if (adapter != null) {
            adapter.updateEvents(newEvents);
        }
    }

    @Override
    public void onItemClick(int position) {
        Event event = adapter.getEventAtPosition(position);
        dismiss();
        activity.navigateToFragment(event);
    }

}
