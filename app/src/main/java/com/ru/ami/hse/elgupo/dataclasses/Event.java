package com.ru.ami.hse.elgupo.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DataFormatException;

import lombok.Getter;

@Getter
public class Event implements Serializable {
    private final Integer id;
    private final String name;
    private final String logo;
    private final Integer dateStart;
    private final Integer dateEnd;
    private final Integer catId;
    private final ArrayList<String> adressList = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
