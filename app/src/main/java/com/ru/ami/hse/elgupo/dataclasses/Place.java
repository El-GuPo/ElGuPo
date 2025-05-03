package com.ru.ami.hse.elgupo.dataclasses;

import lombok.Getter;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;

@Getter
public class Place {
    private final Integer id;
    private final String name;
    private final Double latitude;
    private final Double longitude;
    private final String address;
    private final String logo;
    ArrayList<Event> events = new ArrayList<Event>();

    // DELETE made for client test
    public Place(Double latitude_, Double longitude_, String name_){
        id = 0;
        name = name_;
        latitude = latitude_;
        longitude = longitude_;
        address = "ok";
        logo = "ok";
    }
    // DELETE!!

    public Place(Map place) throws JSONException, DataFormatException {
        try {
            id = Integer.parseInt((String) place.get("id"));
            name = (String) place.get("name");
            latitude = Double.parseDouble((String) place.get("latitude"));
            longitude = Double.parseDouble((String) place.get("longitude"));
            address = (String) place.get("address");
            logo = (String) place.get("logo");
        } catch (Exception e) {
            throw new DataFormatException("wrong data");
        }
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}
