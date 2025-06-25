package com.ru.ami.hse.elgupo.map.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.map.repository.PlacesRepository;
import com.ru.ami.hse.elgupo.map.repository.PlacesRepositoryImpl;

import java.util.List;

public class MapViewModel extends AndroidViewModel {
    private final PlacesRepository repository;
    private final MutableLiveData<List<Place>> places = new MutableLiveData<>();
    private final String TAG = "MapViewModel";

    public MapViewModel(
            @NonNull Application application
    ) {
        super(application);
        this.repository = new PlacesRepositoryImpl();
    }

    public void loadPlaces(double lat, double lon, double radius) {
        repository.loadPlaces(lat, lon, 500, radius, new PlacesRepository.PlacesCallback() {
            @Override
            public void onSuccess(List<Place> placesList) {
                places.postValue(placesList);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error loading places", t);
            }
        });
    }

    public LiveData<List<Place>> getPlaces() {
        return places;
    }
}
