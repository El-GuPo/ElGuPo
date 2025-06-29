package com.ru.ami.hse.elgupo.scheduledEvents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.eventFeed.adapter.RecyclerViewInterface;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.adapter.MyEventsAdapter;
import com.ru.ami.hse.elgupo.scheduledEvents.fragment.MyEventFragment;
import com.ru.ami.hse.elgupo.scheduledEvents.viewmodel.MyEventsViewModel;

import java.util.ArrayList;

public class ScheduledEventsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private final String TAG = "ScheduledEventsActivity";
    private BottomNavigationView bottomNavigationView;
    private Long userId;
    private MyEventsViewModel eventsViewModel;
    private MyEventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduled_events);

        setupNavigation();
        setupViewModel();
        setUpRecycleView();
        setUpObservers();
    }

    private void setUpRecycleView() {
        RecyclerView recyclerView = findViewById(R.id.scheduledEventsActivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        eventsViewModel = new ViewModelProvider(this).get(MyEventsViewModel.class);
        eventsViewModel.loadMyEvents(userId);
    }

    private void setUpObservers() {
        eventsViewModel.getMyEventsList().observe(this, myEvents -> {
            if (myEvents == null) {
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
        bottomNavigationView.post(() -> bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_calendar));
    }

    @Override
    public void onItemClick(int position) {
        Event event = adapter.getEventAtPosition(position);
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putLong("userId", userId);

        findViewById(R.id.scheduledEventsActivity_recyclerView).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        MyEventFragment myEventFragment = new MyEventFragment();
        myEventFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left)
                .replace(R.id.fragment_container, myEventFragment)
                .addToBackStack("event_detail")
                .commit();
    }

    public void returnToScheduledEventsActivity() {
        findViewById(R.id.scheduledEventsActivity_recyclerView).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        eventsViewModel.loadMyEvents(userId);
    }
}