package com.ru.ami.hse.elgupo;

import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.serverrequests.ServerRequester;
import org.junit.Test;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InterruptedException {

        ServerRequester.getPlacesNearby(55.75, 37.61, 10, 5.0, new ServerRequester.PlacesCallback() {
            public void onSuccess(List<Place> places) {
                System.out.println("onSuccess:");
                for (Place place : places) {
                    System.out.println(place.getEvents().size());
                }
            }

            public void onError(Throwable t) {
                System.out.println("onError:");
                t.printStackTrace();
            }
        });
    }
}