package com.ru.ami.hse.elgupo.tinder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;
import com.ru.ami.hse.elgupo.tinder.repository.TinderCallback;
import com.ru.ami.hse.elgupo.tinder.repository.TinderMatchesRepository;
import com.ru.ami.hse.elgupo.tinder.repository.TinderMatchesRepositoryImpl;

import java.util.List;

import lombok.Getter;

public class TinderMatchesViewModel extends AndroidViewModel {
    private final String TAG = "TinderMatchesViewModel";

    private final TinderMatchesRepository tinderMatchesRepository;
    @Getter
    private final MutableLiveData<List<User>> userList = new MutableLiveData<>();

    public TinderMatchesViewModel(@NonNull Application application) {
        super(application);
        this.tinderMatchesRepository = new TinderMatchesRepositoryImpl();
    }

    public void loadMatches(Long mainUserId, Long eventId) {
        tinderMatchesRepository.getMatches(mainUserId, eventId, new TinderCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> response) {
                if (response != null) {
                    userList.postValue(response);
                    Log.e(TAG, "size:" + response.size());
                } else {
                    Log.e(TAG, "response is null");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
