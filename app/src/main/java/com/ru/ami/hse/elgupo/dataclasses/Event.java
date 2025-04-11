package com.ru.ami.hse.elgupo.dataclasses;

import lombok.Getter;
import org.json.JSONObject;

import java.util.Map;
import java.util.zip.DataFormatException;

@Getter
public class Event {
    private final Integer id;
    private final String name;
    private final String logo;
    private final Integer dateStart;
    private final Integer dateEnd;

    public Event(Map event) throws DataFormatException {
        try {
            id = Integer.parseInt((String) event.get("id"));
            name = (String) event.get("name");
            logo = (String) event.get("logo");
            dateStart = Integer.parseInt((String) event.get("date_start"));
            dateEnd = Integer.parseInt((String) event.get("date_end"));
        } catch (Exception e) {
            throw new DataFormatException("wrong data");
        }
    }
}
