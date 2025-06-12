package com.ru.ami.hse.elgupo.eventFeed.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.repository.EventFeedRepository;
import com.ru.ami.hse.elgupo.eventFeed.repository.EventFeedRepositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

public class EventFeedViewModel extends AndroidViewModel {

    private final String TAG = "EventFeedViewModel";
    private final EventFeedRepository eventsRepository;
    @Getter
    private final MutableLiveData<Map<Integer, List<Event>>> eventsByCategory = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<Event>> allEvents = new MutableLiveData<>();

    public EventFeedViewModel(@NonNull Application application) {
        super(application);
        eventsRepository = new EventFeedRepositoryImpl();
    }

    public void loadEventsByCategory() {
        eventsRepository.loadEventsByCategory(new EventFeedRepository.EventsByCategoryCallback() {
            @Override
            public void onSuccess(HashMap<Integer, List<Event>> data) {
                Set<Event> uniqueEvents = data.values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());
                Map<Integer, List<Event>> uniqueData = uniqueEvents.stream()
                        .collect(Collectors.groupingBy(Event::getCatId));
                allEvents.postValue(new ArrayList<>(uniqueEvents));
                eventsByCategory.postValue(uniqueData);
                Log.w(TAG, " " + uniqueEvents.size());
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

}
