package com.ru.ami.hse.elgupo.serverrequests.tinderServices;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetLikedEventsApiService {
    @GET("get-liked-events")
    Call<List<Event>> getLikedEvents(
            @Query("userId") Long userId
    );
}
