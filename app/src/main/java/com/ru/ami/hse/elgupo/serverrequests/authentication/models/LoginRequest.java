package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import lombok.Builder;

@Builder
public class LoginRequest {

    public String email;

    public String password;

}
