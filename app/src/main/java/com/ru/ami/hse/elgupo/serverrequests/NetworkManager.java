package com.ru.ami.hse.elgupo.serverrequests;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String BASE_URL = "http://localhost:8080/";
    private static NetworkManager instance = null;

    private final Retrofit retrofit;

    private NetworkManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public <T> T getInstanceOfService(Class<T> type) {
        return retrofit.create(type);
    }
}