package com.ru.ami.hse.elgupo.eventFeed.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Category;
import com.ru.ami.hse.elgupo.dataclasses.Event;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.eventFeed.adapter.LocationAdapter;
import com.ru.ami.hse.elgupo.eventFeed.utils.CategoryUtils;
import com.ru.ami.hse.elgupo.eventFeed.viewModel.EventLikeViewModel;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.map.utils.DateUtils;
import com.ru.ami.hse.elgupo.serverrequests.eventsLike.models.LikeEventRequest;
import com.ru.ami.hse.elgupo.tinder.fragment.TinderFragment;
import com.ru.ami.hse.elgupo.tinder.viewModel.TinderCandidatesViewModel;

public class EventFragment extends Fragment {

    private final String EVENT_PARAM = "event";
    private final String USER_ID_PARAM = "userId";
    private final String TAG = "EventFragment";
    private Event event;
    private Long userId;
    private EventLikeViewModel eventLikeViewModel;
    private TinderCandidatesViewModel tinderCandidatesViewModel;
    private boolean isAttending;

    public EventFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventLikeViewModel = new ViewModelProvider(this).get(EventLikeViewModel.class);
        tinderCandidatesViewModel = new ViewModelProvider(requireActivity()).get(TinderCandidatesViewModel.class);

        if (getArguments() != null) {
            event = getArguments().getParcelable(EVENT_PARAM);
            userId = getArguments().getLong(USER_ID_PARAM);
        } else {
            Log.e(TAG, "Error in initializing in onCreate");
        }

        eventLikeViewModel.checkIsEventLiked(userId, event.getId().longValue());
        tinderCandidatesViewModel.loadCandidates(userId, event.getId().longValue(), null, null, null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_feed_event_fragment_layout, container, false);

        ImageView eventImage = view.findViewById(R.id.eventImage);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventCategory = view.findViewById(R.id.event_category);
        TextView eventStartDate = view.findViewById(R.id.event_start_date);
        TextView eventEndDate = view.findViewById(R.id.event_end_date);
        RecyclerView locationsRecycler = view.findViewById(R.id.locations_recycler);
        MaterialButton btnTinder = view.findViewById(R.id.btn_tinder);
        MaterialButton btnAttend = view.findViewById(R.id.btn_attend);
        eventLikeViewModel.getIsEventLiked().observe(getViewLifecycleOwner(), isAttending -> {
            if (isAttending) {
                btnTinder.setVisibility(View.VISIBLE);
                btnAttend.setText("Иду");
                btnAttend.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.blue_500)
                );
            } else {
                btnTinder.setVisibility(View.GONE);
                btnAttend.setText("Хочу пойти!");
                btnAttend.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.green_700)
                );
            }
            this.isAttending = isAttending;
        });
        MaterialButton btnBack = view.findViewById(R.id.btn_back);

        setUpButtonObservers(btnAttend, btnTinder, btnBack);
        setUpTextViews(eventName, eventCategory, eventStartDate, eventEndDate);
        Glide.with(this)
                .load(event.getLogo())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .into(eventImage);

        LocationAdapter locationAdapter = new LocationAdapter(event.getAdressList());
        locationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        locationsRecycler.setAdapter(locationAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        eventLikeViewModel.checkIsEventLiked(userId, event.getId().longValue());
        tinderCandidatesViewModel.loadCandidates(userId, event.getId().longValue(), null, null, null);
    }

    private void setUpTextViews(TextView eventName, TextView eventCategory, TextView eventStartDate, TextView eventEndDate) {
        eventName.setText(event.getName());

        eventCategory.setText(Category.getCategoryById(event.getCatId()).getTitle());

        int colorResId = CategoryUtils.categoryColor(Category.getCategoryById(event.getCatId()));
        GradientDrawable background = (GradientDrawable) eventCategory.getBackground();
        background.setColor(ContextCompat.getColor(eventCategory.getContext(), colorResId));


        String dateStart = DateUtils.convertTimestampToTime(event.getDateStart());
        String dateEnd = DateUtils.convertTimestampToTime(event.getDateEnd());
        StringBuilder sb = new StringBuilder("Начало: ");
        sb.append(dateStart);
        eventStartDate.setText(sb.toString());
        sb = new StringBuilder("Окончание: ");
        sb.append(dateEnd);
        eventEndDate.setText(sb.toString());
    }

    private void setUpButtonObservers(MaterialButton btnAttend, MaterialButton btnTinder, MaterialButton btnBack) throws NullPointerException {
        btnBack.setOnClickListener(v -> handleBackPressed());
        btnTinder.setOnClickListener(v -> {
            if (tinderCandidatesViewModel.getUserList().getValue() == null || tinderCandidatesViewModel.getUserList().getValue().isEmpty()) {
                Toast.makeText(requireContext(), "Нет пользователей для оценки", Toast.LENGTH_SHORT).show();
            } else {
                openTinderFragment();
            }
        });

        btnAttend.setOnClickListener(v -> {
            isAttending = !isAttending;

            if (isAttending) {
                registerForEvent(event);
                btnAttend.setText("Иду");
                btnAttend.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.blue_500)
                );
                btnTinder.setVisibility(View.VISIBLE);
            } else {
                unregisterForEvent(event);
                btnAttend.setText("Хочу пойти!");
                btnAttend.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.green_700)
                );
                btnTinder.setVisibility(View.GONE);
            }
        });
    }

    private void handleBackPressed() {
        if (getActivity() instanceof EventFeedActivity) {
            ((EventFeedActivity) getActivity()).returnToEventFeed();
        } else if (getActivity() instanceof MapActivity) {
            ((MapActivity) getActivity()).returnToMap();
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void unregisterForEvent(Event event) {
        eventLikeViewModel.likeEvent(new LikeEventRequest(event.getId().longValue(), userId, event.getCatId().longValue(), isAttending));
    }

    private void registerForEvent(Event event) {
        eventLikeViewModel.likeEvent(new LikeEventRequest(event.getId().longValue(), userId, event.getCatId().longValue(), isAttending));

    }

    private void openTinderFragment() {
        TinderFragment tinderFragment = new TinderFragment();
        Bundle args = new Bundle();

        args.putLong(USER_ID_PARAM, userId);
        args.putParcelable(EVENT_PARAM, event);
        tinderFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, tinderFragment)
                .addToBackStack("tinder")
                .commit();
    }

}
