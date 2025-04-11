package com.ru.ami.hse.elgupo.serverrequests;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

//    private static final String BASE_URL = "http://localhost:8080/";
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static NetworkManager instance = null;
    private final PlacesNearbyApiService apiService;

    private NetworkManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // <- подключаем кастомный клиент
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
