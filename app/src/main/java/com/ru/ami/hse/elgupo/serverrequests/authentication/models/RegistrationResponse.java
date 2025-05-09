package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegistrationResponse implements Serializable {
    public String email;

    public int id;

    public String message;
}
