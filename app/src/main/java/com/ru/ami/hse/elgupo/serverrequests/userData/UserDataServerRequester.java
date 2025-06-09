package com.ru.ami.hse.elgupo.serverrequests.userData;

import android.util.Log;

import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.GetProfileInfoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataServerRequester {

    public interface UserCallbackPost {
        void onSuccess(FillProfileResponse request);

        void onError(Throwable t);
    }

    public interface UserCallbackGet {
        void onSuccess(GetProfileInfoResponse request);

        void onError(Throwable t);
    }
    
    public static void uploadUserData(FillProfileRequest request, UserCallbackPost callback){
        NetworkManager.getInstance().getInstanceOfService(UserDataService.class).fillProfile(request).enqueue(new Callback<FillProfileResponse>() {
            @Override
            public void onResponse(Call<FillProfileResponse> call, Response<FillProfileResponse> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    Log.e("UserDataServerRequester error uploadUserData", response.message());
                }
            }

            @Override
            public void onFailure(Call<FillProfileResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public static void getUserData(Long userId, UserCallbackGet callback){
        NetworkManager.getInstance().getInstanceOfService(UserDataService.class).getProfile(userId).enqueue(new Callback<GetProfileInfoResponse>() {
            @Override
            public void onResponse(Call<GetProfileInfoResponse> call, Response<GetProfileInfoResponse> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                } else {
                    Log.e("UserDataServerRequester error getUserData", response.message());
                }
            }

            @Override
            public void onFailure(Call<GetProfileInfoResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
