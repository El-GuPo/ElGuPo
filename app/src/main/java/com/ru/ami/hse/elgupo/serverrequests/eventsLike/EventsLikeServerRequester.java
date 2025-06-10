package com.ru.ami.hse.elgupo.serverrequests.eventsLike;

import android.util.Log;

import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsLikeServerRequester {

    public interface EventsLikeCallback<T>{
        void onSuccess(T request);

        void onError(Throwable t);
    }

    public static void likeEvent(LikeEventRequest likeEventRequest, EventsLikeCallback<LikeEventResponse> callback){
        NetworkManager.getInstance().getInstanceOfService(LikeEventsService.class).likeEvent(likeEventRequest).enqueue(new Callback<LikeEventResponse>() {
            @Override
            public void onResponse(Call<LikeEventResponse> call, Response<LikeEventResponse> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    Log.e("EventsLikeServerRequester error likeEvent", response.message());
                }
            }

            @Override
            public void onFailure(Call<LikeEventResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public static void isEventLiked(Long userId, Long eventId, EventsLikeCallback<Boolean> callback) {
        NetworkManager.getInstance().getInstanceOfService(LikeEventsService.class).isEventLiked(userId, eventId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    Log.e("EventsLikeServerRequester error isEventLiked", response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
