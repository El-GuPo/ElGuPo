package com.ru.ami.hse.elgupo.tinder.repository;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.TinderServerRequester;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserResponse;

public class TinderLikeRepositoryImpl implements TinderLikeRepository{
    @Override
    public void likeUser(LikeUserRequest likeUserRequest, TinderCallback<LikeUserResponse> callback) {
        TinderServerRequester.likeUser(likeUserRequest, new TinderServerRequester.TinderCallback<LikeUserResponse>() {
            @Override
            public void onSuccess(LikeUserResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }
}
