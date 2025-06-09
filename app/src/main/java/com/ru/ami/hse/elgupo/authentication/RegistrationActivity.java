package com.ru.ami.hse.elgupo.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.profile.ProfileFillingActivity;
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

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register);

        etPassword = findViewById(R.id.setPassword1);
        etPasswordAgain = findViewById(R.id.setPasswordAgain);
        imageButton = findViewById(R.id.imageButton3);
        backButton = findViewById(R.id.Back2);
        TextView tvPasswordHint = findViewById(R.id.tvPasswordHint);

        backButton.setOnClickListener(v -> back());
        imageButton.setOnClickListener(v -> register());
        tvPasswordHint.setOnClickListener(v -> showPasswordRequirementsDialog());
    }

    private void showPasswordRequirementsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Требования к паролю");

        String message = "Такой пароль считается надежным:\n\n" +
                "• Минимум 8 символов\n" +
                "• Максимум 20 символов\n" +
                "• Хотя бы одна заглавная буква (A-Z)\n" +
                "• Хотя бы одна строчная буква (a-z)\n" +
                "• Хотя бы одна цифра (0-9)\n" +
                "• Специальный символ (@#$%^&+=!?)\n";

        builder.setMessage(message);

        builder.setPositiveButton("Понятно", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    private void back() {
        Intent intent = new Intent(RegistrationActivity.this, CheckEmailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void register() {
        LoginApiService loginApiService = NetworkManager.getInstance().getInstanceOfService(LoginApiService.class);
        if (!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isCorrectPassword(etPassword.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Ненадежный пароль!", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = getIntent().getStringExtra("email");
        RegistrationRequest request = new RegistrationRequest(email, etPassword.getText().toString(), etPassword.getText().toString());

        Call<RegistrationResponse> call = loginApiService.registerUser(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().message.equals("OK")) {
                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("userId", response.body().id);
                        editor.apply();

                        long userId = prefs.getLong("userId", -1L);

                        Log.d("REGISTRATION", "User id is " + userId);


                        Intent intent = new Intent(RegistrationActivity.this, ProfileFillingActivity.class);
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
        String regExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}