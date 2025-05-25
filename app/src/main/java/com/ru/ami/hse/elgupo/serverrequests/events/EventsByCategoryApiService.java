package com.ru.ami.hse.elgupo.serverrequests.events;

import com.ru.ami.hse.elgupo.dataclasses.Category;
import com.ru.ami.hse.elgupo.dataclasses.Event;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventsByCategoryApiService {
    @GET("events-by-category")
    Call<HashMap<Category, List<Event>>> getEventsByCategory(
    );
}
