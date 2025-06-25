package com.ru.ami.hse.elgupo.scheduledEvents.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.scheduledEvents.adapter.PersonAdapter;
import com.ru.ami.hse.elgupo.tinder.viewModel.TinderMatchesViewModel;

import java.util.ArrayList;

public class MatchesFragment extends Fragment {

    private final String EVENT_PARAM = "event";
    private final String USER_ID_PARAM = "userId";
    private final String TAG = "MatchesFragment";
    private TinderMatchesViewModel tinderMatchesViewModel;
    private PhotoViewModel photoViewModel;
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;


    public MatchesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinderMatchesViewModel = new ViewModelProvider(requireActivity()).get(TinderMatchesViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches_layout, container, false);
        MaterialButton btnBack = view.findViewById(R.id.btn_back);
        setUpListeners(btnBack);

        recyclerView = view.findViewById(R.id.matches_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        personAdapter = new PersonAdapter(new ArrayList<>(), photoViewModel, getViewLifecycleOwner());
        recyclerView.setAdapter(personAdapter);

        tinderMatchesViewModel.getUserList().observe(
                requireActivity(),
                users -> personAdapter.updateUserList(users));
        return view;
    }

    private void setUpListeners(MaterialButton btnBack) {
        btnBack.setOnClickListener(v -> closeFragment());
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }
}