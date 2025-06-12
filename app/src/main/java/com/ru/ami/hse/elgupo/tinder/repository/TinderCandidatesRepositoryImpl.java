package com.ru.ami.hse.elgupo.tinder.repository;

import android.util.Log;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.TinderServerRequester;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

public class TinderCandidatesRepositoryImpl implements TinderCandidatesRepository{
    private final String TAG = "TinderCandidatesRepositoryImpl";
    @Override
    public void getCandidates(Long mainUserId, Long eventId, Integer minAge, Integer maxAge, String sex, TinderCallback<List<User>> callback) {
        TinderServerRequester.getCandidates(mainUserId, eventId, minAge, maxAge, sex, new TinderServerRequester.TinderCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
                Log.e(TAG, "error: "+t.getMessage());
            }
        });
    }
}
