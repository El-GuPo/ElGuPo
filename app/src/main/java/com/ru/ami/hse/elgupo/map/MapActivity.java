package com.ru.ami.hse.elgupo.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.profile.ProfileActivity;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MapWindow mapWindow;
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))
                                || Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                            enableLocationFeatures();
                        }
                    });
    private MapViewModel viewModel;
    private boolean permissionRequested = false;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setupNavigation();
        mapInitialization();
        setupObservers();
        checkLocationPermissions();
    }

    /*
        Initializing
     */

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_map);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.button_nav_menu_map) {
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_calendar) {
                navigateToActivity(ScheduledEventsActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_list) {
                navigateToActivity(EventFeedActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_user) {
                navigateToActivity(ProfileActivity.class);
                return true;
            }
            return false;
        });

    }

    private void mapInitialization() {
        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapview);
        mapWindow = new MapWindow(mapView, this, viewModel);
    }


    private void setupObservers() {
        viewModel.getPlaces().observe(this, places -> {
            if (mapWindow != null) {
                mapWindow.updateMarkers(places);
            }
        });
    }

    /*
        Permission handling
    */

    private void checkLocationPermissions() {
        boolean hasFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean hasCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFineLocation || hasCoarseLocation) {
            enableLocationFeatures();
        } else if (!permissionRequested) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        permissionRequested = true;
        requestPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void enableLocationFeatures() {
        if (mapWindow != null) {
            try {
                mapWindow.enableLocationTracking();
            } catch (Exception e) {
                Log.e("MapActivity", "Location error: " + e.getMessage());
            }
        }
    }

    /*
        Lifecycle methods
     */

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mapWindow != null) {
            mapWindow.cleanup();
            mapWindow = null;
        }
        mapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_map);
        });
        if (!permissionRequested) {
            checkLocationPermissions();
        }
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}