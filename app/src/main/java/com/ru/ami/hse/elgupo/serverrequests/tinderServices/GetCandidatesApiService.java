package com.ru.ami.hse.elgupo.serverrequests.tinderServices;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCandidatesApiService {
    @GET("candidates-list")
    Call<List<User>> getCandidates(
            @Query("mainUserId") Long mainUserId,
            @Query("eventId") Long eventId,
            @Query("minAge") Integer minAge,
            @Query("maxAge") Integer maxAge,
            @Query("sex") String sex
    );
}
