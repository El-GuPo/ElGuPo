package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegistrationRequest {

    public String email;

    public String password;

    public String confirmPassword;

}
