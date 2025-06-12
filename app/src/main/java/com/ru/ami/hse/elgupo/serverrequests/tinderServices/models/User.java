package com.ru.ami.hse.elgupo.serverrequests.tinderServices.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class User implements Parcelable {

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

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        surname = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readInt();
        }
        email = in.readString();
        hashedPassword = in.readString();
        salt = in.readString();
        sex = in.readString();
        description = in.readString();
        telegramTag = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(surname);
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(age);
        }
        dest.writeString(email);
        dest.writeString(hashedPassword);
        dest.writeString(salt);
        dest.writeString(sex);
        dest.writeString(description);
        dest.writeString(telegramTag);
    }
}

