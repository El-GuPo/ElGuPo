package com.ru.ami.hse.elgupo.profile.photo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.serverrequests.clientPhoto.PhotoServerRequester;

import java.io.File;
import java.net.URL;

public class PhotoRepository {

    public LiveData<Resource<URL>> uploadPhoto(Long userId, File photoFile) {
        MutableLiveData<Resource<URL>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        PhotoServerRequester.uploadPhoto(userId, photoFile, new PhotoServerRequester.ApiCallback<URL>() {
            @Override
            public void onSuccess(URL url) {
                result.postValue(Resource.success(url));
            }

            @Override
            public void onError(String error) {
                result.postValue(Resource.error(error));
            }

        });

        return result;
    }

    public LiveData<Resource<URL>> getPhoto(Long userId) {
        MutableLiveData<Resource<URL>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        PhotoServerRequester.getPhoto(userId, new PhotoServerRequester.ApiCallback<URL>() {
            @Override
            public void onSuccess(URL url) {
                result.postValue(Resource.success(url));
            }

            @Override
            public void onError(String error) {
                result.postValue(Resource.error(error));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> deletePhoto(Long userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        PhotoServerRequester.deletePhoto(userId, new PhotoServerRequester.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                result.postValue(Resource.success(success));
            }

            @Override
            public void onError(String error) {
                result.postValue(Resource.error(error));
            }
        });

        return result;
    }
}
