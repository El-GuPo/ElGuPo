package com.ru.ami.hse.elgupo.serverrequests.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FillProfileRequest {

    public Long userId;

    public String sex;

    public String name;

    public String surname;

    public Integer age;

    public String description;

    public String telegramTag;
}

