package com.ru.ami.hse.elgupo.serverrequests.eventsLike.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEventResponse {
    private Long userId;
    private Long eventId;
    private Long catId;
    private String status;
}
