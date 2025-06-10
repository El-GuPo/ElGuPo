package com.ru.ami.hse.elgupo.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.eventFeed.EventFeedActivity;
import com.ru.ami.hse.elgupo.map.MapActivity;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.profile.photo.Resource;
import com.ru.ami.hse.elgupo.profile.photo.downloadUtils;
import com.ru.ami.hse.elgupo.profile.viewModel.UserDataViewModel;
import com.ru.ami.hse.elgupo.scheduledEvents.ScheduledEventsActivity;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    private Long userId;
    private BottomNavigationView bottomNavigationView;
    private PhotoViewModel photoViewModel;
    private UserDataViewModel userDataViewModel;
    private File userPhoto;
    private String name;
    private String surname;
    private String gender;
    private String chosenGender;
    private Integer age;
    private String description;
    private String tgTag;
    private boolean isDataSaved;
    private boolean isPhotoSaved;
    private String userEmail;

    private ImageView userImageView;
    private EditText etFirstName, etLastName, etAge, etDescription, etTelegram;
    private TextView tvUserId, tvEmail;
    private MaterialButtonToggleGroup genderToggleGroup;
    private MaterialButton toggleMale, toggleFemale, toggleNotSpecified;
    private MaterialButton btnSave, btnLoadPhoto, btnDeletePhoto;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        setupNavigation();

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoViewModel.getPhotoUrl(userId);
        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
        userDataViewModel.loadUserData(userId);

        initViews();
        initLaunchers();
        setUpObservers();
        setUpListeners();
    }

    private void initViews() {
        userImageView = findViewById(R.id.profile_image);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        genderToggleGroup = findViewById(R.id.genderToggleGroup);
        toggleMale = findViewById(R.id.toggleMale);
        toggleFemale = findViewById(R.id.toggleFemale);
        toggleNotSpecified = findViewById(R.id.toggleNotSpecified);
        etAge = findViewById(R.id.etAge);
        etDescription = findViewById(R.id.etDescription);
        etTelegram = findViewById(R.id.etTelegram);
        btnSave = findViewById(R.id.btnSave);
        btnLoadPhoto = findViewById(R.id.btnLoadPhoto);
        btnDeletePhoto = findViewById(R.id.btnDeletePhoto);
        tvUserId = findViewById(R.id.tvUserId);
        tvEmail = findViewById(R.id.tvEmail);
        isDataSaved = true;

        userImageView.setClipToOutline(true);
    }

    private void setUpObservers() {
        userDataViewModel.getUserData().observe(this, userData -> {
            if (userData != null) {
                name = userData.name;
                surname = userData.surname;
                gender = userData.sex;
                chosenGender = gender;
                age = userData.age;
                description = userData.description;
                tgTag = userData.telegramTag;
                userEmail = userData.email;
                updateUserDataWindow();
            }
        });
        photoViewModel.getPhotoUrl(userId).observeForever(new Observer<Resource<URL>>() {
            @Override
            public void onChanged(Resource<URL> urlResource) {
                if (urlResource.status == Resource.Status.SUCCESS) {
                    Glide.with(ProfileActivity.this)
                            .load(urlResource.data.toString())
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(userImageView);
                }
            }
        });
    }

    private void updateUserDataWindow() {
        etFirstName.setText(name);
        etLastName.setText(surname);
        if (gender != null) {
            switch (gender) {
                case "MAN":
                    genderToggleGroup.check(toggleMale.getId());
                    break;
                case "WOMAN":
                    genderToggleGroup.check(toggleFemale.getId());
                    break;
                default:
                    genderToggleGroup.check(toggleNotSpecified.getId());
            }
        }
        etAge.setText(String.valueOf(age));
        etDescription.setText(description);
        etTelegram.setText(tgTag);
        tvUserId.setText(userId.toString());
        tvEmail.setText(userEmail);
    }

    private void initLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        loadImageFromUri(uri);
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        galleryLauncher.launch("image/*");
                    } else {
                        Toast.makeText(this, "Для загрузки фото необходимо разрешение", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setUpListeners() {
        btnDeletePhoto.setOnClickListener(v -> deletePhoto());
        btnLoadPhoto.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnSave.setOnClickListener(v -> saveData());
        genderToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.toggleMale) {
                        chosenGender = "MAN";
                    } else if (checkedId == R.id.toggleFemale) {
                        chosenGender = "WOMAN";
                    } else {
                        chosenGender = "OTHER";
                    }
                }
            }
        });
    }

    private void checkPermissionAndOpenGallery() {
        String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = android.Manifest.permission.READ_MEDIA_IMAGES;
        }

        if (ContextCompat.checkSelfPermission(this, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*");
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void deletePhoto() {
        userPhoto = null;
        isPhotoSaved = false;
        Glide.with(this)
                .load(R.drawable.user)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(userImageView);
    }

    private void saveData() {
        if (!name.equals(etFirstName.getText().toString())) {
            isDataSaved = false;
            name = etFirstName.getText().toString();
        }
        if (!surname.equals(etLastName.getText().toString())) {
            isDataSaved = false;
            surname = etLastName.getText().toString();
            Log.w("Profile Activity", surname);
        }
        try {
            String s = etAge.getText().toString();
            Integer newAge = Integer.parseInt(s);
            if (!age.equals(newAge)) {
                age = newAge;
                isDataSaved = false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорретный формат возраста, невозможно сохранить изменения", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!description.equals(etDescription.getText().toString())) {
            isDataSaved = false;
            description = etDescription.getText().toString();
        }
        if (!tgTag.equals(etTelegram.getText().toString())) {
            isDataSaved = false;
            tgTag = etTelegram.getText().toString();
        }
        if (!gender.equals(chosenGender)) {
            isDataSaved = false;
            gender = chosenGender;
        }

        if (!isDataSaved) {
            try {
                userDataViewModel.uploadUserData(new FillProfileRequest(userId, gender, name, surname, age, description, tgTag));
            } catch (Exception e) {
                Log.e("Profile activity update userData", e.getMessage());
            }
        }
        if (!isPhotoSaved) {
            try {
                if (userPhoto != null) {
                    photoViewModel.uploadUserPhoto(userId, userPhoto);
                } else {
                    photoViewModel.deleteUserPhoto(userId);
                }
            } catch (Exception e) {
                Log.e("Profile activity update photo", e.getMessage());
            }
        }
        isDataSaved = true;
        isPhotoSaved = true;
    }

    private void loadImageFromUri(Uri uri) {
        try {
            userPhoto = downloadUtils.downloadFileFromUri(this, uri, userId);
        } catch (IOException e) {
            Log.e("Error creating tempFile", e.getMessage());
        }

        Glide.with(this)
                .load(uri)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(userImageView);
        isPhotoSaved = false;
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_user);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.button_nav_menu_user) {
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_list) {
                navigateToActivity(EventFeedActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_map) {
                navigateToActivity(MapActivity.class);
                return true;
            } else if (item.getItemId() == R.id.button_nav_menu_calendar) {
                navigateToActivity(ScheduledEventsActivity.class);
                return true;
            }
            return false;
        });
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.post(() -> {
            bottomNavigationView.setSelectedItemId(R.id.button_nav_menu_user);
        });
    }
}