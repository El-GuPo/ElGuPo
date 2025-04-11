package com.ru.ami.hse.elgupo.serverrequests;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.ru.ami.hse.elgupo.dataclasses.Place;
import java.util.List;

public class ServerRequester {

    public interface PlacesCallback {
        void onSuccess(List<Place> places);
        void onError(Throwable t);
    }

    public static void getPlacesNearby(Double latitude, Double longitude, int count, Double radius, PlacesCallback callback) {
        NetworkManager
                .getInstance()
                .getApiService()
                .getPlacesNearby(latitude, longitude, count, radius)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(new Exception("Server error or empty response"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }
}