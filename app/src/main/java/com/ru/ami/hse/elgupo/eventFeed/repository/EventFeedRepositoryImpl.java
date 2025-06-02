package com.ru.ami.hse.elgupo.eventFeed.repository;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.serverrequests.events.ServerRequester;

import java.util.HashMap;
import java.util.List;

public class EventFeedRepositoryImpl implements EventFeedRepository {
    @Override
    public void loadEventsByCategory(EventsByCategoryCallback callback) {
        ServerRequester.getEventsByCategory(new ServerRequester.EventsByCategoryCallback() {
            @Override
            public void onSuccess(HashMap<Integer, List<Event>> data) {
                callback.onSuccess(data);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });

    }
}
