package com.ru.ami.hse.elgupo.authentication;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.validator.routines.EmailValidator;

import androidx.appcompat.app.AppCompatActivity;

import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.serverrequests.NetworkManager;
import com.ru.ami.hse.elgupo.serverrequests.authentication.LoginApiService;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.CheckEmailRequest;
import com.ru.ami.hse.elgupo.serverrequests.authentication.models.CheckEmailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckEmailActivity extends AppCompatActivity {
    private EditText etEmail;
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_main);

        etEmail = findViewById(R.id.setEmail);
        ImageButton fstBtn = findViewById(R.id.imageButton);

        fstBtn.setOnClickListener(v -> attemptFirst());
    }

    private void attemptFirst() {
        String email = etEmail.getText().toString().trim();
        if (!emailValidator.isValid(email)) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginApiService loginApiService = NetworkManager.getInstance().getInstanceOfService(LoginApiService.class);
        CheckEmailRequest request = new CheckEmailRequest(email);

        Call<CheckEmailResponse> call = loginApiService.checkEmail(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().isUserExists) {
                        Toast.makeText(CheckEmailActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckEmailActivity.this, "BEBEB", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                Toast.makeText(CheckEmailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
