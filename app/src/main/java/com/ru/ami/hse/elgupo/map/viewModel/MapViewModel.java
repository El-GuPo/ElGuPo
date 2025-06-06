package com.ru.ami.hse.elgupo.map.viewModel;

import android.app.Application;
import android.util.Log;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.map.repository.PlacesRepository;
import com.ru.ami.hse.elgupo.map.repository.PlacesRepositoryImpl;
import com.ru.ami.hse.elgupo.serverrequests.events.ServerRequester;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MapViewModel extends AndroidViewModel {
    private final PlacesRepository repository;
    private final MutableLiveData<List<Place>> places = new MutableLiveData<>();

    public MapViewModel(
            @NonNull Application application
    ) {
        super(application);
        this.repository = new PlacesRepositoryImpl();
    }

    public void loadPlaces(double lat, double lon, double radius) {
        repository.loadPlaces(lat, lon, 20, radius, new PlacesRepository.PlacesCallback() {
            @Override
            public void onSuccess(List<Place> placesList) {
                places.postValue(placesList);
                Log.w("Events size", "Size: " + placesList.size());
            }

            @Override
            public void onError(Throwable t) {
                Log.e("MapViewModel", "Error loading places", t);
            }
        });
    }

    public LiveData<List<Place>> getPlaces() {
        return places;
    }
}
