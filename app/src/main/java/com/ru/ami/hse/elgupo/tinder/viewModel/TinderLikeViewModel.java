package com.ru.ami.hse.elgupo.tinder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserResponse;
import com.ru.ami.hse.elgupo.tinder.repository.TinderCallback;
import com.ru.ami.hse.elgupo.tinder.repository.TinderLikeRepository;
import com.ru.ami.hse.elgupo.tinder.repository.TinderLikeRepositoryImpl;

import lombok.Getter;

public class TinderLikeViewModel extends AndroidViewModel {

    private final String TAG = "TinderLikeViewModel";
    private final TinderLikeRepository tinderLikeRepository;
    @Getter
    private final MutableLiveData<Boolean> isMatch = new MutableLiveData<>();

    public TinderLikeViewModel(@NonNull Application application) {
        super(application);
        this.tinderLikeRepository = new TinderLikeRepositoryImpl();
    }

    public void likeUser(LikeUserRequest likeUserRequest) {
        tinderLikeRepository.likeUser(likeUserRequest, new TinderCallback<LikeUserResponse>() {
            @Override
            public void onSuccess(LikeUserResponse response) {
                if (response != null) {
                    isMatch.postValue(response.isMatch());
                } else {
                    Log.e(TAG, "response is null");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
