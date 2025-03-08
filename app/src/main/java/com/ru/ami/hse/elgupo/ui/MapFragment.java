package com.ru.ami.hse.elgupo.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.map.MapWindow;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

public class MapFragment extends Fragment {

    private MapWindow mapWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MapKitFactory.initialize(getContext());
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mapView = rootView.findViewById(R.id.mapview);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mapWindow = new MapWindow(mainActivity, mapView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MapKitFactory.initialize(getContext());
        MapView mapView = view.findViewById(R.id.mapview);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mapWindow = new MapWindow(mainActivity, mapView);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapWindow.getMapView().onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        mapWindow.getMapView().onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapWindow != null) {
            mapWindow.getMapView().onStop();
        }
    }
}