package com.ru.ami.hse.elgupo.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.authentication.LoginApiService;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.RegistrationRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.RegistrationResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    EditText etPassword;
    EditText etPasswordAgain;

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register);

        etPassword = findViewById(R.id.setPassword1);
        etPasswordAgain = findViewById(R.id.setPasswordAgain);
        imageButton = findViewById(R.id.imageButton3);

        imageButton.setOnClickListener(v -> register());
    }

    private void register() {
        LoginApiService loginApiService = NetworkManager.getInstance().getInstanceOfService(LoginApiService.class);
        if (!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isCorrectPassword(etPassword.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Ненадежный пароль!", Toast.LENGTH_SHORT).show();
        }
        String email = getIntent().getStringExtra("email");
        RegistrationRequest request = new RegistrationRequest(email, etPassword.toString(), etPassword.toString());

        Call<RegistrationResponse> call = loginApiService.registerUser(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().message.equals("OK")) {
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isCorrectPassword(String password) {
        String regExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
