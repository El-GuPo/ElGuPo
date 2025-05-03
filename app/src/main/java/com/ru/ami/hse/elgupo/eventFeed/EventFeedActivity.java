package com.ru.ami.hse.elgupo.eventFeed;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;

public class EventFeedActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_feed);
        setupNavigation();
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
}