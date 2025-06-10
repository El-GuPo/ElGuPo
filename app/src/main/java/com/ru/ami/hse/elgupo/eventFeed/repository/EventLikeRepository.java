package com.ru.ami.hse.elgupo.eventFeed.repository;

import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;

public interface EventLikeRepository {
    void likeEvent(LikeEventRequest likeEventRequest, EventsLikeCallback<LikeEventResponse> callback);

    void isEventLiked(Long userId, Long eventId, EventsLikeCallback<Boolean> callback);

    interface EventsLikeCallback<T> {
        void onSuccess(T request);

        void onError(Throwable t);
    }
}
