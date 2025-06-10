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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.profile.photo.downloadUtils;
import com.ru.ami.hse.elgupo.profile.viewModel.UserDataViewModel;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.FillProfileRequest;

import java.io.File;
import java.io.IOException;

public class ProfileFillingActivity extends AppCompatActivity {
    private Long userId;
    private PhotoViewModel photoViewModel;
    private UserDataViewModel userDataViewModel;
    private File userPhoto;
    private String chosenGender;
    private String userEmail;

    private ImageView userImageView;
    private EditText etFirstName, etLastName, etAge, etDescription, etTelegram;
    private TextView tvUserId, tvEmail;
    private MaterialButtonToggleGroup genderToggleGroup;
    private MaterialButton btnSave, btnLoadPhoto;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1L);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_filling_activity_layout);

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
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
        etAge = findViewById(R.id.etAge);
        etDescription = findViewById(R.id.etDescription);
        etTelegram = findViewById(R.id.etTelegram);
        btnSave = findViewById(R.id.btnSave);
        btnLoadPhoto = findViewById(R.id.btnLoadPhoto);
        tvUserId = findViewById(R.id.tvUserId);
        tvUserId.setText(userId.toString());
        tvEmail = findViewById(R.id.tvEmail);

        userImageView.setClipToOutline(true);
    }

    private void setUpObservers() {
        userDataViewModel.getUserData().observe(this, userData -> {
            if (userData != null && userData.email != null) {
                userEmail = userData.email;
                tvEmail.setText(userEmail);
            }
        });
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

    private void saveData() {
        String name = etFirstName.getText().toString();
        String surname = etLastName.getText().toString();
        Integer age;
        try {
            String s = etAge.getText().toString();
            age = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорретный формат возраста, невозможно сохранить изменения", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = etDescription.getText().toString();
        String tgTag = etTelegram.getText().toString();
        String gender = chosenGender;

        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Необходимо заполнить имя", Toast.LENGTH_SHORT).show();
            return;
        } else if (surname == null || surname.isEmpty()) {
            Toast.makeText(this, "Необходимо заполнить фамилию", Toast.LENGTH_SHORT).show();
            return;
        } else if (age == null) {
            Toast.makeText(this, "Необходимо заполнить возраст", Toast.LENGTH_SHORT).show();
            return;
        } else if (tgTag == null || tgTag.isEmpty() || tgTag.charAt(0) != '@' || tgTag.length() < 2) {
            Toast.makeText(this, "Неправильный формат telegram username", Toast.LENGTH_SHORT).show();
            return;
        } else if (chosenGender == null) {
            gender = "OTHER";
        }
        if (description == null) {
            description = "";
        }

        try {
            userDataViewModel.uploadUserData(new FillProfileRequest(userId, gender, name, surname, age, description, tgTag));
        } catch (Exception e) {
            Log.e("Profile activity update userData", e.getMessage());
        }

        try {
            photoViewModel.uploadUserPhoto(userId, userPhoto);
        } catch (Exception e) {
            Log.e("Profile activity update userPhoto", e.getMessage());
        }
        navigateToActivity(MainActivity.class);
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
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
