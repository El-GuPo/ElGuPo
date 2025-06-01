package com.ru.ami.hse.elgupo.map.repository;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import java.util.List;

public interface PlacesRepository {
    void loadPlaces(double lat, double lon, int limit, double radius, PlacesCallback callback);

    interface PlacesCallback {
        void onSuccess(List<Place> places);

        void onError(Throwable t);
    }
}
