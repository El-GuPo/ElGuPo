package com.ru.ami.hse.elgupo.eventFeed.repository;

import com.ru.ami.hse.elgupo.serverrequests.eventsLike.EventsLikeServerRequester;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;

public class EventLikeRepositoryImpl implements EventLikeRepository {

    @Override
    public void likeEvent(LikeEventRequest likeEventRequest, EventsLikeCallback<LikeEventResponse> callback) {
        EventsLikeServerRequester.likeEvent(likeEventRequest, new EventsLikeServerRequester.EventsLikeCallback<LikeEventResponse>() {
            @Override
            public void onSuccess(LikeEventResponse request) {
                callback.onSuccess(request);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void isEventLiked(Long userId, Long eventId, EventsLikeCallback<Boolean> callback) {
        EventsLikeServerRequester.isEventLiked(userId, eventId, new EventsLikeServerRequester.EventsLikeCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean request) {
                callback.onSuccess(request);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }
}
