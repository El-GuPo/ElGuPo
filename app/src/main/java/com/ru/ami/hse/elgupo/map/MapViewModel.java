package com.ru.ami.hse.elgupo.map;

import android.app.Application;
import android.util.Log;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.serverrequests.events.ServerRequester;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MapViewModel extends AndroidViewModel {
    private MutableLiveData<List<Place>> places = new MutableLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
    }

    /*
        Главное положить в places то, что хочется с необходимым интервалом, дальше, оно все само обработается
     */
    public void loadPlaces(double lat, double lon, double radius) {
        ServerRequester.getPlacesNearby(lat, lon, 10, radius, new ServerRequester.PlacesCallback() {
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

    public LiveData<List<Place>> getPlaces() { return places; }
}
