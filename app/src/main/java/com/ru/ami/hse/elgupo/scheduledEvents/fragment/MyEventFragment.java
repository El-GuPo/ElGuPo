package com.ru.ami.hse.elgupo.scheduledEvents.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.viewModel.EventLikeViewModel;

public class MyEventFragment extends Fragment {

    private final String EVENT_PARAM = "event";
    private final String USER_ID_PARAM = "userId";
    private String TAG = "MyEventFragment";
    private Event event;
    private Long userId;
    private EventLikeViewModel eventLikeViewModel;
    private boolean isAttending;

    public MyEventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventLikeViewModel = new ViewModelProvider(this).get(EventLikeViewModel.class);

        if (getArguments() != null) {
            event = getArguments().getParcelable(EVENT_PARAM);
            userId = getArguments().getLong(USER_ID_PARAM);
        } else {
            Log.e(TAG, "Error in initializing in onCreate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_event, container, false);
    }
}