package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponse {
    public String email;

    public int id;

    public boolean isProfileFilledOut;

    public String message;
}
