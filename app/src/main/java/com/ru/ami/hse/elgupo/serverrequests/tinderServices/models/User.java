package com.ru.ami.hse.elgupo.serverrequests.tinderServices.models;

import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class User {

    private Integer id;

    private String name;

    private String surname;

    private Integer age;

    private String email;

    private String hashedPassword;

    private String salt;

    private String sex;

    private String description;

    private String telegramTag;
}

