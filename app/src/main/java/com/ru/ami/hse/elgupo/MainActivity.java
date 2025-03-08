package com.ru.ami.hse.elgupo;

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

import com.ru.ami.hse.elgupo.map.MapWindow;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity {

    private MapWindow mapWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setApiKey(savedInstanceState);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        mapWindow = new MapWindow(this, findViewById(R.id.mapview));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("haveApiKey", true);
    }

    private void setApiKey(Bundle savedInstanceState) {
        boolean haveApiKey = savedInstanceState != null && savedInstanceState.getBoolean("haveApiKey") ? true : false;
        if(!haveApiKey){
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapWindow.getMapView().onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapWindow.getMapView().onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

}