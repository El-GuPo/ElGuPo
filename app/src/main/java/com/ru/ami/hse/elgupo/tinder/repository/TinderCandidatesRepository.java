package com.ru.ami.hse.elgupo.tinder.repository;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

public interface TinderCandidatesRepository {
    void getCandidates(Long mainUserId, Long eventId, Integer minAge, Integer maxAge, String sex, TinderCallback<List<User>> callback);
}
