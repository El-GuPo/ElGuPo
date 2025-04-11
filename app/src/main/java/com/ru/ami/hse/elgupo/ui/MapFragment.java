package com.ru.ami.hse.elgupo.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.map.MapWindow;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapWindow mapWindow;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean permissionRequested = false;

    /*
        Initializing functions
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))
                            || Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                        enableLocationFeatures();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapview);
        mapWindow = new MapWindow(mapView, requireContext());

        checkLocationPermissions();
    }


    /*
        Permission functions
    */

    private void checkLocationPermissions() {
        boolean hasFineLocation = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean hasCoarseLocation = ContextCompat.checkSelfPermission(requireContext(),
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
                Log.e("MapFragment", "Location permission revoked: " + e.getMessage());
            }
        }
    }

    /*
        Lifecycle functions
     */

    @Override
    public void onStart() {
        super.onStart();
        if(mapView != null){
            mapView.onStart();
            MapKitFactory.getInstance().onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!permissionRequested) {
            checkLocationPermissions();
        }
    }

    @Override
    public void onStop() {
        if (mapView != null) {
            mapView.onStop();
            MapKitFactory.getInstance().onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mapWindow != null) {
            mapWindow.cleanup();
            mapWindow = null;
        }
        mapView = null;
        super.onDestroyView();
    }

}
