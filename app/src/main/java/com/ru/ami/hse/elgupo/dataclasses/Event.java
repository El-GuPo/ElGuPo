package com.ru.ami.hse.elgupo.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DataFormatException;

import lombok.Getter;

@Getter
public class Event implements Serializable, Parcelable {
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    private final Integer id;
    private final String name;
    private final String logo;
    private final Integer dateStart;
    private final Integer dateEnd;
    private final Integer catId;
    private ArrayList<String> adressList = new ArrayList<>();

    public Event(Map event) throws DataFormatException {
        try {
            id = Integer.parseInt((String) event.get("id"));
            name = (String) event.get("name");
            logo = (String) event.get("logo");
            dateStart = Integer.parseInt((String) event.get("date_start"));
            dateEnd = Integer.parseInt((String) event.get("date_end"));
            catId = Integer.parseInt((String) event.get("cat_id"));
        } catch (Exception e) {
            throw new DataFormatException("wrong data");
        }
    }

    protected Event(Parcel in) {
        int tempId = in.readInt();
        id = tempId == -1 ? null : tempId;
        name = in.readString();
        logo = in.readString();

        int tempDateStart = in.readInt();
        dateStart = tempDateStart == -1 ? null : tempDateStart;

        int tempDateEnd = in.readInt();
        dateEnd = tempDateEnd == -1 ? null : tempDateEnd;

        int tempCatId = in.readInt();
        catId = tempCatId == -1 ? null : tempCatId;

        adressList = in.createStringArrayList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id != null ? id : -1);
        dest.writeString(name);
        dest.writeString(logo);
        dest.writeInt(dateStart != null ? dateStart : -1);
        dest.writeInt(dateEnd != null ? dateEnd : -1);
        dest.writeInt(catId != null ? catId : -1);
        dest.writeStringList(adressList);
    }
}