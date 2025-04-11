package com.ru.ami.hse.elgupo.serverrequests;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String BASE_URL = "http://localhost:8080/";
    private static NetworkManager instance = null;
    private final PlacesNearbyApiService apiService;

    private NetworkManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PlacesNearbyApiService.class);
    }


    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public PlacesNearbyApiService getApiService() {
        return apiService;
    }
}