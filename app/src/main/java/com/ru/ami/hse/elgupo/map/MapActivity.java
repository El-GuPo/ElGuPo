package com.ru.ami.hse.elgupo.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.eventFeed.fragment.EventFragment;
import com.ru.ami.hse.elgupo.map.dialog.PlaceInfoDialog;
import com.ru.ami.hse.elgupo.map.viewModel.MapViewModel;
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

    private final ActivityResultLauncher<String[]> locationButtonPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))
                                || Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                            mapWindow.moveToUserLocation();
                        }
                    });


    private MapViewModel viewModel;
    private boolean permissionRequested = false;
    private BottomNavigationView bottomNavigationView;

    private Long userId;
    private PlaceInfoDialog currentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        ImageButton btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(v -> handleLocationButtonClick());

        FrameLayout dialogFragmentContainer = findViewById(R.id.dialog_fragment_container);
        dialogFragmentContainer.setVisibility(View.GONE);

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
        mapWindow = new MapWindow(mapView, this, viewModel, this);
    }


    private void setupObservers() {
        viewModel.getPlaces().observe(this, places -> {
            if (mapWindow != null) {
                mapWindow.updateMarkers(places);
                if (currentDialog != null && currentDialog.isShowing()) {
                    currentDialog.updateEvents();
                }
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

    private void handleLocationButtonClick() {
        if (hasLocationPermission()) {
            mapWindow.moveToUserLocation();
        } else {
            if (isPermissionPermanentlyDenied()) {
                showSettingsRedirectDialog();
            } else {
                locationButtonPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }
    }

    private boolean isPermissionPermanentlyDenied() {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) && !hasLocationPermission();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showSettingsRedirectDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Необходима разрешение к отслеживанию геолокации")
                .setMessage("Доступ к геолокации был отклонён, перейдите в настройки приложения и разрешите доступ к отслеживанию геолокации")
                .show();
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

    public void showDialogWindow(Place place) {
        currentDialog = new PlaceInfoDialog(this,
                place, this);
        currentDialog.show();
    }

    public void navigateToFragment(Event event) {
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putLong("userId", userId);

        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        findViewById(R.id.statusBarSpacer).setVisibility(View.VISIBLE);
        findViewById(R.id.btnMyLocation).setVisibility(View.GONE);
        findViewById(R.id.mapview).setVisibility(View.GONE);

        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left)
                .replace(R.id.fragment_container, eventFragment)
                .addToBackStack("event_detail")
                .commit();
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void returnToMap() {
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        findViewById(R.id.statusBarSpacer).setVisibility(View.GONE);
        findViewById(R.id.btnMyLocation).setVisibility(View.VISIBLE);
        findViewById(R.id.mapview).setVisibility(View.VISIBLE);
    }
}