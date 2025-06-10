package com.ru.ami.hse.elgupo.profile.repository;

import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.GetProfileInfoResponse;

public interface UserDataRepository {
    void loadUserData(Long userId, UserDataRepository.UserCallbackGet callback);

    void fillUserData(FillProfileRequest request, UserDataRepository.UserCallbackPost callback);

    interface UserCallbackPost {
        void onSuccess(FillProfileResponse request);

        void onError(Throwable t);
    }

    interface UserCallbackGet {
        void onSuccess(GetProfileInfoResponse request);

        void onError(Throwable t);
    }
}
