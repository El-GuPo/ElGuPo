package com.ru.ami.hse.elgupo.serverrequests.authentication;

import com.ru.ami.hse.elgupo.serverrequests.authentication.models.CheckEmailRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.CheckEmailResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.LoginRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.LoginResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.RegistrationRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApiService {
    @GET("register")
    Call<RegistrationResponse> registerUser(@Body RegistrationRequest registrationRequest);

    @POST("check_email")
    Call<CheckEmailResponse> checkEmail(@Body CheckEmailRequest checkEmailRequest);

    @POST("auth")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
