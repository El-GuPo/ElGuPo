package com.ru.ami.hse.elgupo.serverrequests;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

import retrofit2.Call;

public class ServerRequester {
    public static List<Place> getPlacesNearby(Double latitude, Double longitude, int count, Double radius) {
        final List<Place>[] places = new List[]{new ArrayList<Place>()};
        NetworkManager
                .getInstance()
                .getApiService()
                .getPlacesNearby(latitude, longitude, count, radius)
                .enqueue(new retrofit2.Callback<List<Place>>() {
                    public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            places[0] = response.body();
                        }
                    }

                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
        return places[0];
    }
}
