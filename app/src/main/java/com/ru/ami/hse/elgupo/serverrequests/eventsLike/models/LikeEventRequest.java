package com.ru.ami.hse.elgupo.serverrequests.eventsLike.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeEventRequest {

    Long eventId;

    Long userId;

    Long catId;

    Boolean liked;
}
