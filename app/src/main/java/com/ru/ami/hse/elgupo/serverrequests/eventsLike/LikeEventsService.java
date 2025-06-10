package com.ru.ami.hse.elgupo.serverrequests.eventsLike;

import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeEventsService {
    @POST("like_event")
    Call<LikeEventResponse> likeEvent(@Body LikeEventRequest likeEventRequest);

    @GET("like_events/user/{userId}/event/{eventId}")
    Call<Boolean> isEventLiked(@Path("userId") Long userId, @Path("eventId") Long eventId);
}
