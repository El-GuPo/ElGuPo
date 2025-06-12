package com.ru.ami.hse.elgupo.tinder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;
import com.ru.ami.hse.elgupo.tinder.repository.TinderCallback;
import com.ru.ami.hse.elgupo.tinder.repository.TinderCandidatesRepository;
import com.ru.ami.hse.elgupo.tinder.repository.TinderCandidatesRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class TinderCandidatesViewModel extends AndroidViewModel {

    private final String TAG = "TinderCandidatesViewModel";
    private final TinderCandidatesRepository tinderCandidatesRepository;
    @Getter
    private final MutableLiveData<List<User>> userList = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private List<User> loadedUsers = new ArrayList<>();
    private int currentUserIndex = 0;

    public TinderCandidatesViewModel(@NonNull Application application) {
        super(application);
        this.tinderCandidatesRepository = new TinderCandidatesRepositoryImpl();
    }

    public void loadCandidates(Long mainUserId, Long eventId, Integer minAge, Integer maxAge, String sex) {
        tinderCandidatesRepository.getCandidates(mainUserId, eventId, minAge, maxAge, sex, new TinderCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> response) {
                if (response != null && !response.isEmpty()) {
                    loadedUsers = response;
                    userList.postValue(response);
                    currentUserIndex = 0;
                    currentUser.postValue(loadedUsers.get(currentUserIndex));
                    Log.w(TAG, "Size:" + response.size());
                } else {
                    currentUser.postValue(null);
                    Log.w(TAG, "response is null");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void nextUser() {
        currentUserIndex++;
        if (currentUserIndex < loadedUsers.size()) {
            currentUser.postValue(loadedUsers.get(currentUserIndex));
        } else {
            currentUser.postValue(null);
        }
    }

    public boolean hasMoreUsers() {
        return currentUserIndex < loadedUsers.size();
    }

}
