package com.ru.ami.hse.elgupo.eventFeed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.eventFeed.adapter.EventsAdapter;
import com.ru.ami.hse.elgupo.eventFeed.viewModel.EventFeedViewModel;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;

public class EventFeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private EventFeedViewModel eventFeedViewModel;
    private EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_feed);
        setupNavigation();
        Log.w("EventFeed1", "OnCreate");

        setUpViewModel();
        setUpRecyclerView();
        setUpObservers();

    }

    private void setUpViewModel() {
        eventFeedViewModel = new ViewModelProvider(this).get(EventFeedViewModel.class);
        eventFeedViewModel.loadEventsByCategory();
        Log.w("EventFeed1", "setUpViewModel");
    }

    private void setUpObservers() {
        try {
            eventFeedViewModel.getEventsByCategory().observe(this, eventsMap -> {
                if (eventsMap != null) {
                    adapter.updateEvents(eventsMap);
                    Log.w("EventFeed1", "Data loaded successfully");
                }
            });
        } catch (Exception e) {
            Log.w("EventFeed1", e.getMessage());
        }
    }

    private void setUpRecyclerView() {
        try {
            Log.w("EventFeed1", "setingUp recyclerView");
            RecyclerView recyclerView = findViewById(R.id.eventFeed_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.w("EventFeed1", "setingUp recyclerView2");
            adapter = new EventsAdapter();
            recyclerView.setAdapter(adapter);
            Log.w("EventFeed1", "setingUp recyclerView3");
        } catch (Exception e) {
            Log.w("EventFeed1", e.getMessage());
        }
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_list);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.button_nav_menu_list) {
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_map) {
                navigateToActivity(MapActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_user) {
                navigateToActivity(ProfileActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_calendar) {
                navigateToActivity(ScheduledEventsActivity.class);
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
        Log.w("EventFeed", "OnResume");
        super.onResume();
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_list);
        });
    }

}