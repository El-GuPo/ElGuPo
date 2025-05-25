package com.ru.ami.hse.elgupo.profile.photo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.net.URL;

public class PhotoViewModel extends AndroidViewModel {
    private final PhotoRepository repository;
    private final MutableLiveData<Resource<URL>> photoUrl = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> deleteResult = new MutableLiveData<>();

    public PhotoViewModel(@NonNull Application application) {
        super(application);
        this.repository = new PhotoRepository();
    }

    public void uploadUserPhoto(Long userId, File photoFile) {
        repository.uploadPhoto(userId, photoFile)
                .observeForever(photoUrl::postValue);
    }

    public void deleteUserPhoto(Long userId) {
        repository.deletePhoto(userId)
                .observeForever(deleteResult::postValue);
    }

    public LiveData<Resource<URL>> getPhotoUrl(Long userID) {
        return repository.getPhoto(userID);
    }

    public LiveData<Resource<Boolean>> getDeleteResult() {
        return deleteResult;
    }
}
