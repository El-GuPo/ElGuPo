package com.ru.ami.hse.elgupo.map;

import static com.yandex.runtime.Runtime.getApplicationContext;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.ru.ami.hse.elgupo.ElGupoApplication;
import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.GeoObjectTapEvent;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.GeoObjectSelectionMetadata;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkCreatedCallback;
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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

public class MapWindow implements CameraListener {

    private final WeakReference<Context> contextRef;
    private final MapView mapView;
    private final Map map;
    private final SearchManager searchManager;

    // constants and states
    private final Point startLocation = new Point(59.9402, 30.315);
    private static final float COMFORTABLE_ZOOM_LEVEL = 16.4F;
    private Float zoomValue = 16.5F;


    private MapObjectCollection mapObjectCollection;
    private PlacemarkMapObject placemarkMapObject;
    private Session searchSession;
    private HashMap<PlacemarkMapObject, String> placemarkMapObjects = new HashMap<>();

    private GeoObjectTapListener tapListener;
    private Session.SearchListener searchListener;
    private InputListener inputListener;
    private MapObjectTapListener mapObjectTapListener;

    private LocationManager locationManager;
    private LocationListener locationListener;


    private Point myLocation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 0;
    private static final boolean USE_IN_BACKGROUND = false;
    private boolean isFirstLocationUpdate = true;

    public MapWindow(MapView mapView_, Context context_) {
        contextRef = new WeakReference<>(context_);
        mapView = mapView_;
        map = mapView.getMapWindow().getMap();
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);

        initializeMap();
    }

    private void initializeMap(){
        setupListeners();
        enableLocationTracking();
        initialSetup();
    }

    private void setupListeners(){
        map.addCameraListener(this);

        this.tapListener = new GeoObjectTapListener() {
            @Override
            public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
                GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent.getGeoObject()
                        .getMetadataContainer().getItem(GeoObjectSelectionMetadata.class);
                map.selectGeoObject(selectionMetadata);
                return false;
            }
        };
        this.searchListener = new Session.SearchListener() {
            @Override
            public void onSearchResponse(@NonNull Response response) {
                String street = response.getCollection().getChildren().stream()
                        .findFirst()
                        .map(child -> Objects.requireNonNull(child.getObj()).getMetadataContainer()
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
        this.inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                searchSession = searchManager.submit(point, 20, new SearchOptions(), searchListener);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

            }
        };
        this.mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast.makeText(getSafeContext(), "Эрмитаж — музей изобразительных искусств", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        map.addTapListener(tapListener);
        map.addInputListener(inputListener);

    }

    private void setupLocationManager(){
        this.locationManager = MapKitFactory.getInstance().createLocationManager();
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                myLocation = location.getPosition();

                if (isFirstLocationUpdate) {
                    moveCamera(myLocation);
                    isFirstLocationUpdate = false;
                }

                Log.w(TAG, "Location updated: " + myLocation.getLatitude() + "," + myLocation.getLongitude());
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    System.out.println("Location is not available");
                }
            }
        };
        subscribeToLocationUpdate();
    }

    private void initialSetup(){
        moveToStartLocation();
        setMarkerInStartLocation();
    }

    private void moveToStartLocation() {
        map.move(
                new CameraPosition(startLocation, zoomValue, 0.0F, 0.0F),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    private void moveToUserLocation(){
        if (myLocation != null){
            moveCamera(myLocation);
        }
    }

    public void setMarkerInStartLocation() {
        var marker = createBitmapFromVector(R.drawable.ic_pin_red_svg);
        if(marker == null){
            Log.e("MapWindow", "Null marker in MapWindow method");
        }
        mapObjectCollection = map.getMapObjects();
        placemarkMapObject = mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
            @Override
            public void onPlacemarkCreated(@NonNull PlacemarkMapObject placemarkMapObject) {
                placemarkMapObject.setGeometry(startLocation);
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(Objects.requireNonNull(marker)));

            }
        });
        placemarkMapObject.setOpacity(0.5f);
        placemarkMapObject.addTapListener(mapObjectTapListener);
        placemarkMapObjects.put(placemarkMapObject, "Start");
    }

    public void setMarkerInPoint(Point location, String text, String info) {
        var marker = createBitmapFromVector(R.drawable.ic_pin_red_svg);
        mapObjectCollection = map.getMapObjects();
        placemarkMapObject = mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
            @Override
            public void onPlacemarkCreated(@NonNull PlacemarkMapObject placemarkMapObject) {
                placemarkMapObject.setGeometry(location);
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(Objects.requireNonNull(marker)));

            }
        });
        placemarkMapObject.setOpacity(0.5f);
        placemarkMapObject.setText(text);
        placemarkMapObject.addTapListener(mapObjectTapListener);
        placemarkMapObjects.put(placemarkMapObject, info);

    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map,
                                        @NonNull CameraPosition cameraPosition,
                                        @NonNull CameraUpdateReason cameraUpdateReason,
                                        boolean finished) {
        if (finished && placemarkMapObject != null) {
            if (cameraPosition.getZoom() >= COMFORTABLE_ZOOM_LEVEL && zoomValue <= COMFORTABLE_ZOOM_LEVEL) {
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(Objects.requireNonNull(createBitmapFromVector(R.drawable.ic_pin_blue_svg))));
            } else if (cameraPosition.getZoom() <= COMFORTABLE_ZOOM_LEVEL && zoomValue >= COMFORTABLE_ZOOM_LEVEL) {
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(Objects.requireNonNull(createBitmapFromVector(R.drawable.ic_pin_red_svg))));
            }
            zoomValue = cameraPosition.getZoom();
        }
    }

    private Bitmap createBitmapFromVector(int art) {
        Context context = getSafeContext();
        if(context == null){
            return null;
        }

        try {
            Drawable drawable = ContextCompat.getDrawable(ElGupoApplication.getAppContext(), art);
            if (drawable == null) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e){
            Log.e("MapWindow", "Error creating bitmap", e);
            return null;
        }
    }

    private void moveCamera(Point point) {
        map.move(
                new CameraPosition(point, zoomValue, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    private void subscribeToLocationUpdate() {
        if (locationManager != null && locationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, locationListener);
        }
    }

    /*
        Enabling location tracking, check permissions
     */
    public void enableLocationTracking() {
        if (checkPermission()) {
            setupLocationManager();
            moveToUserLocation();
        }
    }

    private boolean checkPermission() {
        Context context = getSafeContext();
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // destroying and cleaning functions

    public void cleanup() {
        if (locationManager != null && locationListener != null) {
            locationManager.unsubscribe(locationListener);
        }
        if (map != null) {
            map.removeCameraListener(this);
            map.getMapObjects().clear();
        }
    }

    // getters
    private Context getSafeContext() {
        Context context = contextRef.get();
        return context != null ? context : ElGupoApplication.getAppContext();
    }

}
