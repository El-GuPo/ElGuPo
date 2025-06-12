package com.ru.ami.hse.elgupo.tinder.repository;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserResponse;

public interface TinderLikeRepository {
    void likeUser(LikeUserRequest likeUserRequest, TinderCallback<LikeUserResponse> callback);
}
