package com.ru.ami.hse.elgupo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ru.ami.hse.elgupo.databinding.ActivityMainBinding;
import com.ru.ami.hse.elgupo.ui.ListFragment;
import com.ru.ami.hse.elgupo.ui.MapFragment;
import com.ru.ami.hse.elgupo.ui.SettingsFragment;
import com.ru.ami.hse.elgupo.ui.UserFragment;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setApiKey(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ListFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.button_nav_menu_list) {
                replaceFragment(new ListFragment());
            } else if (itemId == R.id.button_nav_menu_settings) {
                replaceFragment(new SettingsFragment());
            } else if (itemId == R.id.button_nav_menu_map) {
                replaceFragment(new MapFragment());
            } else if(itemId == R.id.button_nav_menu_user){
                replaceFragment(new UserFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

        currentFragment = fragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("haveApiKey", true);
    }

    private void setApiKey(Bundle savedInstanceState) {
        boolean haveApiKey = savedInstanceState != null && savedInstanceState.getBoolean("haveApiKey") ? true : false;
        if (!haveApiKey) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        }
    }

}