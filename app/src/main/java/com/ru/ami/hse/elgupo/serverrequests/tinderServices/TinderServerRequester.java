package com.ru.ami.hse.elgupo.serverrequests.tinderServices;

import android.util.Log;

import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.LikeEventsService;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventResponse;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserResponse;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TinderServerRequester {
    public interface TinderCallback<T>{
        void onSuccess(T request);

        void onError(Throwable t);
    }

    public static void likeUser(LikeUserRequest likeUserRequest, TinderServerRequester.TinderCallback<LikeUserResponse> callback){
        NetworkManager.getInstance().getInstanceOfService(LikeUserApiService.class).likeUser(likeUserRequest).enqueue(new Callback<LikeUserResponse>() {
            @Override
            public void onResponse(Call<LikeUserResponse> call, Response<LikeUserResponse> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    Log.e("TinderServerRequester error likeUser", response.message());
                }
            }

            @Override
            public void onFailure(Call<LikeUserResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public static void getCandidates(Long mainUserId, Long eventId, Integer minAge, Integer maxAge, String sex,
                                     TinderServerRequester.TinderCallback<List<User>> callback) {
        NetworkManager.getInstance().getInstanceOfService(GetCandidatesApiService.class)
                .getCandidates(mainUserId, eventId, minAge, maxAge, sex).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Server error: " + response.code() + " - " + response.message();
                    Log.e("TinderServerRequester error getCandidates", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public static void getLikedEvents(Long userId, TinderServerRequester.TinderCallback<List<Event>> callback) {
        NetworkManager.getInstance().getInstanceOfService(GetLikedEventsApiService.class)
                .getLikedEvents(userId).enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                        if(response.isSuccessful()){
                            callback.onSuccess(response.body());
                        } else {
                            Log.e("TinderServerRequester error getLikedEvents", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public static void getMatches(Long mainUserId, Long eventId, TinderServerRequester.TinderCallback<List<User>> callback) {
        NetworkManager.getInstance().getInstanceOfService(GetMatchesApiService.class)
                .getMatches(mainUserId, eventId).enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if(response.isSuccessful()){
                            callback.onSuccess(response.body());
                        } else {
                            Log.e("TinderServerRequester error getCandidates", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }
}
