package com.ru.ami.hse.elgupo.eventFeed.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.eventFeed.repository.EventLikeRepository;
import com.ru.ami.hse.elgupo.eventFeed.repository.EventLikeRepositoryImpl;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;

import lombok.Getter;

public class EventLikeViewModel extends AndroidViewModel {

    private final String TAG = "EventLikeViewModel";
    private final EventLikeRepository eventLikeRepository;
    @Getter
    private final MutableLiveData<Boolean> isEventLiked = new MutableLiveData<>();

    public EventLikeViewModel(@NonNull Application application) {
        super(application);
        this.eventLikeRepository = new EventLikeRepositoryImpl();
    }

    public void likeEvent(LikeEventRequest likeEventRequest){
        eventLikeRepository.likeEvent(likeEventRequest, new EventLikeRepository.EventsLikeCallback<LikeEventResponse>() {
            @Override
            public void onSuccess(LikeEventResponse request) {
                if(!request.getStatus().equals("OK")){
                    Log.e(TAG, "Error like event, but onSuccess");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error like event" + t.getMessage());
            }
        });
    }

    public void checkIsEventLiked(Long userId, Long eventId){
        eventLikeRepository.isEventLiked(userId, eventId, new EventLikeRepository.EventsLikeCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean request) {
                isEventLiked.postValue(request);
                Log.w(TAG, "onSuccess");
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error check event liked" + t.getMessage());
            }
        });
    }
}
