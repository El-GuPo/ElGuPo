package com.ru.ami.hse.elgupo.serverrequests.events;

import com.ru.ami.hse.elgupo.dataclasses.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface PlacesNearbyApiService {

    @GET("places-nearby")
    Call<List<Place>> getPlacesNearby(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("count") int count,
            @Query("radius") Double radius
    );
}