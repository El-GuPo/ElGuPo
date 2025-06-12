package com.ru.ami.hse.elgupo.tinder.repository;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.TinderServerRequester;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

public class TinderMatchesRepositoryImpl implements TinderMatchesRepository{
    @Override
    public void getMatches(Long mainUserId, Long eventId, TinderCallback<List<User>> callback) {
        TinderServerRequester.getMatches(mainUserId, eventId, new TinderServerRequester.TinderCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }
}
