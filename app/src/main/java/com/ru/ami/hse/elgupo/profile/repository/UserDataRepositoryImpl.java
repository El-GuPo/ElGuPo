package com.ru.ami.hse.elgupo.profile.repository;

import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.GetProfileInfoResponse;
import com.ru.ami.hse.elgupo.serverrequests.userData.UserDataServerRequester;

public class UserDataRepositoryImpl implements UserDataRepository {

    @Override
    public void loadUserData(Long userId, UserCallbackGet callback) {
        UserDataServerRequester.getUserData(userId, new UserDataServerRequester.UserCallbackGet() {
            @Override
            public void onSuccess(GetProfileInfoResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public void fillUserData(FillProfileRequest request, UserCallbackPost callback) {
        UserDataServerRequester.uploadUserData(request, new UserDataServerRequester.UserCallbackPost() {
            @Override
            public void onSuccess(FillProfileResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }
}
