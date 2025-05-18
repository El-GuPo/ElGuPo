package com.ru.ami.hse.elgupo.serverrequests;

import com.ru.ami.hse.elgupo.dataclasses.Category;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.dataclasses.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.HashMap;
import java.util.List;
public interface EventsByCategoryApiService {
    @GET("events-by-category")
    Call<HashMap<Category, List<Event>>> getEventsByCategory(
    );
}
