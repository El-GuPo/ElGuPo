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

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.GeoObjectTapEvent;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.GeoObjectSelectionMetadata;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import com.yandex.mapkit.search.Address;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.ToponymObjectMetadata;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements CameraListener {

    private MapView mapView;
    private Point startLocation = new Point(59.9402, 30.315);
    private Float zoomValue = 16.5F;
    private MapObjectCollection mapObjectCollection;
    private PlacemarkMapObject placemarkMapObject;
    private static final float ZOOM_BOUNDARY = 16.4F;
    private MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            Toast.makeText(getApplicationContext(), "Эрмитаж — музей изобразительных искусств", Toast.LENGTH_SHORT).show();
            return true;
        }
    };
    private GeoObjectTapListener tapListener = new GeoObjectTapListener() {
        @Override
        public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
            GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent.getGeoObject()
                    .getMetadataContainer().getItem(GeoObjectSelectionMetadata.class);
            mapView.getMapWindow().getMap().selectGeoObject(selectionMetadata);
            return false;
        }
    };
    private Session.SearchListener searchListener = new Session.SearchListener() {
        @Override
        public void onSearchResponse(@NonNull Response response) {
            String street = response.getCollection().getChildren().stream()
                    .findFirst()
                    .map(child -> child.getObj().getMetadataContainer()
                            .getItem(ToponymObjectMetadata.class)
                            .getAddress()
                            .getComponents().stream()
                            .filter(component -> component.getKinds().contains(Address.Component.Kind.STREET))
                            .findFirst()
                            .map(Address.Component::getName)
                            .orElse("Информация об улице не найдена"))
                    .orElse("Информация об улице не найдена");

            Toast.makeText(getApplicationContext(), street, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSearchError(@NonNull Error error) {
        }
    };
    private SearchManager searchManager;
    private Session searchSession;
    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            searchSession = searchManager.submit(point, 20, new SearchOptions(), searchListener);
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setApiKey(savedInstanceState);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapview);
        moveToStartLocation();
        setMarkerInStartLocation();
        mapView.getMapWindow().getMap().addCameraListener(this);
        mapView.getMapWindow().getMap().addTapListener(tapListener);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
        mapView.getMapWindow().getMap().addInputListener(inputListener);
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

    private void moveToStartLocation() {
        mapView.getMapWindow().getMap().move(
                new CameraPosition(startLocation, zoomValue, 0.0F, 0.0F),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    private void setMarkerInStartLocation() {
        var marker = createBitmapFromVector(R.drawable.ic_pin_red_svg);
        mapObjectCollection = mapView.getMapWindow().getMap().getMapObjects();
        placemarkMapObject = mapObjectCollection.addPlacemark(startLocation, ImageProvider.fromBitmap(marker));
        placemarkMapObject.setOpacity(0.5f);
        placemarkMapObject.addTapListener(mapObjectTapListener);
    }

    @Override
    public void onCameraPositionChanged(Map map,
                                        CameraPosition cameraPosition,
                                        CameraUpdateReason cameraUpdateReason,
                                        boolean finished){
        if(finished){
            if(cameraPosition.getZoom() >= ZOOM_BOUNDARY && zoomValue <= ZOOM_BOUNDARY){
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(createBitmapFromVector(R.drawable.ic_pin_blue_svg)));
            }
            else if(cameraPosition.getZoom() <= ZOOM_BOUNDARY && zoomValue >= ZOOM_BOUNDARY){
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(createBitmapFromVector(R.drawable.ic_pin_red_svg)));
            }
            zoomValue = cameraPosition.getZoom();
        }
    }

    private Bitmap createBitmapFromVector(int art){
        Drawable drawable = ContextCompat.getDrawable(this, art);
        if(drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        if(bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

}