package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetProfileInfoResponse {

    public String sex;

    public String name;

    public String surname;

    public Integer age;

    public String description;

    public String telegramTag;

    public String email;
}
