package com.ru.ami.hse.elgupo.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.authentication.LoginApiService;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.LoginRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etPassword;
    ImageButton imageButton;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        etPassword = findViewById(R.id.setPassword);
        imageButton = findViewById(R.id.imageButton2);
        backButton = findViewById(R.id.Back1);

        backButton.setOnClickListener(v -> back());
        imageButton.setOnClickListener(v -> login());
    }

    private void back() {
        Intent intent = new Intent(LoginActivity.this, CheckEmailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void login() {
        LoginApiService loginApiService = NetworkManager.getInstance().getInstanceOfService(LoginApiService.class);
        String email = getIntent().getStringExtra("email");
        LoginRequest request = new LoginRequest(email, etPassword.getText().toString());

        Call<LoginResponse> call = loginApiService.login(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().message.equals("OK")) {
                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("userId", response.body().id);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
