package com.ru.ami.hse.elgupo.serverrequests.userData;

import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.GetProfileInfoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserDataService {
    @POST("fill_profile")
    Call<FillProfileResponse> fillProfile(@Body FillProfileRequest fillProfileRequest);

    @GET("get_profile/{userId}")
    Call<GetProfileInfoResponse> getProfile(@Path("userId") Long userId);
}
