package com.ru.ami.hse.elgupo.serverrequests.clientPhoto;

import java.net.URL;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ClientPhotoService {

    @Multipart
    @PUT("photos/{userID}")
    Call<URL> uploadPhoto(
            @Path("userID") Long userID,
            @Part MultipartBody.Part photo
    );

    @GET("photos/{userID}")
    Call<URL> getPhoto(
            @Path("userID") Long userID
    );

    @DELETE("photos/{userID}")
    Call<Boolean> deletePhoto(
            @Path("userID") Long userID
    );
}

