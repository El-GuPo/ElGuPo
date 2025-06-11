package com.ru.ami.hse.elgupo.scheduledEvents.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.scheduledEvents.repository.MyEventsRepository;
import com.ru.ami.hse.elgupo.scheduledEvents.repository.MyEventsRepositoryImpl;

import java.util.List;

import lombok.Getter;

public class MyEventsViewModel extends AndroidViewModel {

    private final String TAG = "MyEventsViewModel";
    private final MyEventsRepository myEventsRepository;
    @Getter
    private final MutableLiveData<List<Event>> myEventsList = new MutableLiveData<>();

    public MyEventsViewModel(@NonNull Application application) {
        super(application);
        this.myEventsRepository = new MyEventsRepositoryImpl();
    }

    public void loadMyEvents(Long userId) {
        myEventsRepository.loadLikedEvents(userId, new MyEventsRepository.MyEventCallback() {
            @Override
            public void onSuccess(List<Event> request) {
                myEventsList.postValue(request);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "error loading my events");
            }
        });
    }
}
