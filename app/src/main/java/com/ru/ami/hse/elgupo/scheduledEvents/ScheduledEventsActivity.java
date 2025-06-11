package com.ru.ami.hse.elgupo.scheduledEvents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.eventFeed.adapter.EventsAdapter;
import com.ru.ami.hse.elgupo.eventFeed.adapter.RecyclerViewInterface;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.adapter.MyEventsAdapter;
import com.ru.ami.hse.elgupo.scheduledEvents.viewmodel.MyEventsViewModel;

import java.util.ArrayList;

public class ScheduledEventsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView bottomNavigationView;
    private Long userId;
    private MyEventsViewModel eventsViewModel;
    private MyEventsAdapter adapter;
    private final String TAG = "ScheduledEventsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreated");
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);
        Log.w(TAG, "userId: "+userId);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduled_events);

        setupNavigation();
        setupViewModel();
        setUpRecycleView();
        setUpObservers();
    }

    private void setUpRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.scheduledEventsActivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel(){
        Log.w(TAG, "setupViewModel");
        eventsViewModel = new ViewModelProvider(this).get(MyEventsViewModel.class);
        eventsViewModel.loadMyEvents(userId);
    }

    private void setUpObservers(){
        Log.w(TAG, "setUpObservers");
        eventsViewModel.getMyEventsList().observe(this, myEvents -> {
            if(myEvents == null){
                myEvents = new ArrayList<>();
            }
            adapter.updateEvents(myEvents);
        });
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.button_nav_menu_calendar) {
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_map) {
                navigateToActivity(MapActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_user) {
                navigateToActivity(ProfileActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_list) {
                navigateToActivity(EventFeedActivity.class);
                return true;
            }
            return false;
        });
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventsViewModel.loadMyEvents(userId);
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_calendar);
        });
    }

    @Override
    public void onItemClick(int position) {
        Event event = adapter.getEventAtPosition(position);
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putLong("userId", userId);

    }
}