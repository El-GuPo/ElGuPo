package com.ru.ami.hse.elgupo.map.repository;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.serverrequests.events.ServerRequester;

import java.util.List;

public class PlacesRepositoryImpl implements PlacesRepository{
    @Override
    public void loadPlaces(double lat, double lon, int limit, double radius, PlacesCallback callback) {
        ServerRequester.getPlacesNearby(lat, lon, limit, radius, new ServerRequester.PlacesCallback() {
            @Override
            public void onSuccess(List<Place> places) {
                callback.onSuccess(places);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }
}
