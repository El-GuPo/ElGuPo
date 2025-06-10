package com.ru.ami.hse.elgupo.serverrequests.tinderServices;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMatchesApiService {
    @GET("get-matches")
    Call<List<User>> getMatches(
            @Query("mainUserId") Long mainUserId,
            @Query("eventId") Long eventId
    );
}
