package com.ru.ami.hse.elgupo.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.map.MapViewModel;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private PhotoViewModel photoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        setupNavigation();
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoViewModel.getPhotoUrl(1000L);
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_user);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.button_nav_menu_user) {
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_list) {
                navigateToActivity(EventFeedActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_map) {
                navigateToActivity(MapActivity.class);
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
    public void onResume() {
        super.onResume();
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_user);
        });
    }
}