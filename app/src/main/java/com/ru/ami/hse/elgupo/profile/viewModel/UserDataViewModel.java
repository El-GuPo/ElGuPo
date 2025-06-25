package com.ru.ami.hse.elgupo.profile.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.profile.repository.UserDataRepository;
import com.ru.ami.hse.elgupo.profile.repository.UserDataRepositoryImpl;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileResponse;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.GetProfileInfoResponse;

import lombok.Getter;

public class UserDataViewModel extends AndroidViewModel {
    private final String TAG = "USER_DATA_VIEW_MODEL";
    private final UserDataRepository userDataRepository;
    @Getter
    private final MutableLiveData<GetProfileInfoResponse> userData = new MutableLiveData<>();

    public UserDataViewModel(@NonNull Application application) {
        super(application);
        this.userDataRepository = new UserDataRepositoryImpl();
    }

    public void uploadUserData(FillProfileRequest fillProfileRequest) {
        userDataRepository.fillUserData(fillProfileRequest, new UserDataRepository.UserCallbackPost() {
            @Override
            public void onSuccess(FillProfileResponse request) {
                if (request.id == -1) {
                    Log.e(TAG, "error uploading user Data");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void loadUserData(Long userId) {
        userDataRepository.loadUserData(userId, new UserDataRepository.UserCallbackGet() {
            @Override
            public void onSuccess(GetProfileInfoResponse request) {
                userData.postValue(request);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

}

