package com.ru.ami.hse.elgupo.eventFeed.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ru.ami.hse.elgupo.R;

public class EventFragment extends Fragment {

    public EventFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_feed_event_fragment_layout, container, false);
        TextView eventNameTextView = view.findViewById(R.id.event_name);
        if(getArguments() != null){
            String name = getArguments().getString("event_name");
            eventNameTextView.setText(name);
        } else{
            eventNameTextView.setText("default");
        }
        return view;
    }
}
