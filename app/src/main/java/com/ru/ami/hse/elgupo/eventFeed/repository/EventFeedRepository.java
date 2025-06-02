package com.ru.ami.hse.elgupo.eventFeed.repository;

import com.ru.ami.hse.elgupo.dataclasses.Event;

import java.util.HashMap;
import java.util.List;

public interface EventFeedRepository {
    void loadEventsByCategory(EventsByCategoryCallback callback);

    interface EventsByCategoryCallback {
        void onSuccess(HashMap<Integer, List<Event>> data);

        void onError(Throwable t);
    }
}
