package com.ru.ami.hse.elgupo.tinder.repository;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

public interface TinderMatchesRepository {
    void getMatches(Long mainUserId, Long eventId, TinderCallback<List<User>> callback);
}
