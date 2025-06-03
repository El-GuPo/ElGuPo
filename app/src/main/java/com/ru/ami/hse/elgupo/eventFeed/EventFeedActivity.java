package com.ru.ami.hse.elgupo.eventFeed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.adapter.EventsAdapter;
import com.ru.ami.hse.elgupo.eventFeed.adapter.RecyclerViewInterface;
import com.ru.ami.hse.elgupo.eventFeed.fragment.EventFragment;
import com.ru.ami.hse.elgupo.eventFeed.viewModel.EventFeedViewModel;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;

public class EventFeedActivity extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView bottomNavigationView;
    private EventFeedViewModel eventFeedViewModel;
    private EventsAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_feed);

        Toolbar toolbar = findViewById(R.id.searchTool);
        setSupportActionBar(toolbar);

        setupNavigation();

        setUpViewModel();
        setUpRecyclerView();
        setUpObservers();

    }

    private void setUpViewModel() {
        eventFeedViewModel = new ViewModelProvider(this).get(EventFeedViewModel.class);
        eventFeedViewModel.loadEventsByCategory();
    }

    private void setUpObservers() {
        eventFeedViewModel.getEventsByCategory().observe(this, eventsMap -> {
            if (eventsMap != null) {
                adapter.updateEvents(eventsMap);
            }
        });
    }

    private void setUpRecyclerView() {
        try {
            RecyclerView recyclerView = findViewById(R.id.eventFeed_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new EventsAdapter(this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.w("Error setting recyclerView eventsFeed", e.getMessage());
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
        super.onResume();
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_list);
        });
    }

    @Override
    public void onItemClick(int position) {
        Event event = adapter.getEventAtPosition(position);
        Bundle args = new Bundle();
        args.putString("event_name", event.getName());

        Log.w("OnItemClick", "onItemClick called");
        findViewById(R.id.eventFeed_recyclerView).setVisibility(View.GONE);
        findViewById(R.id.searchTool).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left)
                .replace(R.id.fragment_container, eventFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id == R.id.eventFeedSearch){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_event_feed, menu);
        MenuItem menuItem = menu.findItem(R.id.eventFeedSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchStr = newText;
                adapter.getFilter().filter(newText );
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

}