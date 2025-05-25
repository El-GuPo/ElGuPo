package com.ru.ami.hse.elgupo.serverrequests.clientPhoto;

import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoServerRequester {

    public interface ApiCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }

    public static void uploadPhoto(Long userId, File photoFile, ApiCallback<URL> callback) {
        MultipartBody.Part part = createMultipartPart(photoFile);
        executeCall(NetworkManager.getInstance().getInstanceOfService(ClientPhotoService.class).uploadPhoto(userId, part), callback);
    }

    public static void getPhoto(Long userId, ApiCallback<URL> callback) {
        executeCall(NetworkManager.getInstance().getInstanceOfService(ClientPhotoService.class).getPhoto(userId), callback);
    }

    public static void deletePhoto(Long userId, ApiCallback<Boolean> callback) {
        executeCall(NetworkManager.getInstance().getInstanceOfService(ClientPhotoService.class).deletePhoto(userId), callback);
    }

    private static <T> void executeCall(Call<T> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    handleError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private static <T> void handleError(Response<T> response, ApiCallback<T> callback) {
        try {
            String errorBody = response.errorBody().string();
            callback.onError("Server error " + response.code() + ": " + errorBody);
        } catch (IOException e) {
            callback.onError("Unexpected error: " + response.code());
        }
    }

    private static MultipartBody.Part createMultipartPart(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        RequestBody requestFile = RequestBody.create(
                file,
                MediaType.parse(mimeType)
        );
        return MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
    }

}
