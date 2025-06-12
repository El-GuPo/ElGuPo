package com.ru.ami.hse.elgupo.tinder.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.profile.photo.Resource;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.LikeUserRequest;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;
import com.ru.ami.hse.elgupo.tinder.viewModel.TinderCandidatesViewModel;
import com.ru.ami.hse.elgupo.tinder.viewModel.TinderLikeViewModel;

public class TinderFragment extends Fragment {

    private final String USER_ID_PARAM = "userId";
    private final String EVENT_PARAM = "event";
    private final String TAG = "TinderFragmentAction";
    private Long userId;
    private User user;
    private Event event;
    private TinderLikeViewModel tinderLikeViewModel;
    private TinderCandidatesViewModel tinderCandidatesViewModel;
    private PhotoViewModel photoViewModel;
    private TextView userFirstName, userLastName, userAge, userDescription, userTg;
    private ImageView userImagePhoto;
    private MaterialButton toggleMale, toggleFemale, toggleNotSpecified;


    public TinderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        tinderCandidatesViewModel = new ViewModelProvider(requireActivity()).get(TinderCandidatesViewModel.class);
        tinderLikeViewModel = new ViewModelProvider(this).get(TinderLikeViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID_PARAM);
            event = getArguments().getParcelable(EVENT_PARAM);
        }

        tinderCandidatesViewModel.loadCandidates(userId, event.getId().longValue(), null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tinder_layout, container, false);

        MaterialButton buttonBack = view.findViewById(R.id.btn_back);
        MaterialButton btnApprove = view.findViewById(R.id.btnApprove);
        MaterialButton btnReject = view.findViewById(R.id.btnReject);
        setOnBtnListeners(btnApprove, btnReject, buttonBack);

        userFirstName = view.findViewById(R.id.firstName);
        userLastName = view.findViewById(R.id.lastName);
        userAge = view.findViewById(R.id.age);
        userDescription = view.findViewById(R.id.description);
        userTg = view.findViewById(R.id.telegram);

        userImagePhoto = view.findViewById(R.id.profile_image);
        userImagePhoto.setClipToOutline(true);

        toggleMale = view.findViewById(R.id.toggleMale);
        toggleFemale = view.findViewById(R.id.toggleFemale);
        toggleNotSpecified = view.findViewById(R.id.toggleNotSpecified);

        setupObservers();
        return view;
    }

    private void setupObservers() {
        tinderCandidatesViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                closeTinderFragment();
            } else {
                this.user = user;
                updateUserData();
            }
        });
    }

    private void updateUserData() {
        setUpPhoto(userImagePhoto);
        setUpTextView(userFirstName, userLastName, userAge, userDescription, userTg);
        setUpSex();
    }

    private void setUpTextView(TextView userFirstName, TextView userLastName, TextView userAge, TextView userDescription, TextView userTg) {
        userFirstName.setText(user.getName());
        userLastName.setText(user.getSurname());
        userAge.setText(user.getAge().toString());
        userDescription.setText(user.getDescription());
        userTg.setText(user.getTelegramTag());
    }

    private void setUpPhoto(ImageView userImage) {
        photoViewModel.getPhotoUrl(user.getId().longValue()).observe(getViewLifecycleOwner(), urlResource -> {
            if (urlResource.status == Resource.Status.SUCCESS) {
                Glide.with(this)
                        .load(urlResource.data.toString())
                        .circleCrop()
                        .into(userImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.user)
                        .circleCrop()
                        .into(userImage);
            }
        });

    }

    private void setUpSex() {
        setDefaultColors();
        switch (user.getSex()){
            case "MAN":
                toggleMale.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                toggleMale.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_500));
                break;
            case "WOMAN":
                toggleFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                toggleFemale.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_500));
                break;
            default:
                toggleNotSpecified.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                toggleNotSpecified.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_400));
                break;
        }
    }

    private void setDefaultColors(){
        toggleMale.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_500));
        toggleMale.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_100));
        toggleFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.pink_500));
        toggleFemale.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_100));
        toggleNotSpecified.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        toggleNotSpecified.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_400));
    }

    private void handleDecision(boolean isApproved) {
        if (user == null) {
            Log.w(TAG, "handleDesicionCalled user null");
            return;
        }
        Log.w(TAG, "handleDesicionCalled");
        sendDecisionToServer(isApproved);
        if (tinderCandidatesViewModel.hasMoreUsers()) {
            tinderCandidatesViewModel.nextUser();
        } else {
            closeTinderFragment();
        }
    }

    private void setOnBtnListeners(MaterialButton btnApprove, MaterialButton btnReject, MaterialButton btnBack) {
        btnBack.setOnClickListener(v -> returnToEventFragment());
        btnApprove.setOnClickListener(v -> handleDecision(true));
        btnReject.setOnClickListener(v -> handleDecision(false));
    }

    private void sendDecisionToServer(boolean isApproved) {
        tinderLikeViewModel.likeUser(new LikeUserRequest(userId, user.getId().longValue(), event.getId().longValue(), isApproved));
    }

    private void returnToEventFragment() {
        getParentFragmentManager().popBackStack();
    }

    private void closeTinderFragment() {
        getParentFragmentManager().popBackStack();
        Toast.makeText(requireContext(), "Нет пользователей для оценки", Toast.LENGTH_SHORT).show();
    }
}