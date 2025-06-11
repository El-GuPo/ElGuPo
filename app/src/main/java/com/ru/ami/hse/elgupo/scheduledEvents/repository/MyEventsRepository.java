package com.ru.ami.hse.elgupo.scheduledEvents.repository;


import com.ru.ami.hse.elgupo.dataclasses.Event;

import java.util.List;

public interface MyEventsRepository {
    void loadLikedEvents(Long userId, MyEventCallback myEventCallback);

    interface MyEventCallback {
        void onSuccess(List<Event> request);

        void onError(Throwable t);
    }
}
