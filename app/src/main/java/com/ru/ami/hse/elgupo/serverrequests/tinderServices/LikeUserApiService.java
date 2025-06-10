package com.ru.ami.hse.elgupo.serverrequests.tinderServices;

import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserResponse;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface LikeUserApiService {
    @POST("like_user")
    Call<LikeUserResponse> likeUser(@Body LikeUserRequest likeUserRequest);
}
