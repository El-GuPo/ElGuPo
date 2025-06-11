package com.ru.ami.hse.elgupo.scheduledEvents.repository;

import android.util.Log;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.TinderServerRequester;

import java.util.List;

public class MyEventsRepositoryImpl implements MyEventsRepository {

    private final String TAG = "MyEventsRepositoryImpl";

    @Override
    public void loadLikedEvents(Long userId, MyEventCallback myEventCallback) {
        TinderServerRequester.getLikedEvents(userId, new TinderServerRequester.TinderCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> response) {
                myEventCallback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                myEventCallback.onError(t);
                Log.w(TAG, "error: " + t.getMessage());
            }
        });

    }
}
