package com.ru.ami.hse.elgupo.serverrequests.tinderServices.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeUserRequest {

    Long likerId;

    Long userLikeableId;

    Long eventId;

    boolean isLiked;
}