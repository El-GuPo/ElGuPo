package com.ru.ami.hse.elgupo.map;

import static com.yandex.runtime.Runtime.getApplicationContext;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ru.ami.hse.elgupo.ElGupoApplication;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.map.utils.BitmapUtils;
import com.ru.ami.hse.elgupo.map.viewModel.MapViewModel;
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
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.GeoObjectSelectionMetadata;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.MapObjectVisitor;
import com.yandex.mapkit.map.PlacemarkCreatedCallback;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
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
import java.util.List;
import java.util.Objects;

public class MapWindow implements CameraListener {

    private static final float COMFORTABLE_ZOOM_LEVEL = 16.4F;
    private static final String TAG = MapWindow.class.getSimpleName();
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 0;
    private static final boolean USE_IN_BACKGROUND = false;
    private static final String POPUP_USER_DATA = "custom_popup";

    private final MapViewModel viewModel;
    private final WeakReference<Context> contextRef;
    private final Map map;
    private final MapActivity mapActivity;

    /*
        Location Manager properties
     */
    private final Point startLocation = new Point(59.9402, 30.315);
    private final SearchManager searchManager;
    private final MapObjectCollection mapObjectCollection;
    private final java.util.Map<Place, MapObject> mapObjectDictionary;

    /*
        Camera properties
     */
    private LocationManager locationManager;
    private LocationListener locationListener;

    /*
        Object listeners
     */
    private Point userLocation;
    private boolean locationTrackingEnabled = false;
    private Float zoomValue = 16.5F;
    private Session searchSession;
    private GeoObjectTapListener tapListener;
    private Session.SearchListener searchListener;
    private InputListener inputListener;
    private MapObjectTapListener mapObjectTapListener;

    public MapWindow(MapView mapView_, Context context_, MapViewModel viewModel_, MapActivity mapActivity) {
        contextRef = new WeakReference<>(context_);
        this.mapActivity = mapActivity;
        map = mapView_.getMapWindow().getMap();
        viewModel = viewModel_;
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);

        mapObjectCollection = map.getMapObjects().addCollection();
        mapObjectDictionary = new HashMap<>();

        initializeMap();
    }

    /*
        Initializing functions
     */

    private void initializeMap() {
        setupListeners();
        initialRequest();
    }

    private void setupListeners() {
        map.addCameraListener(this);

        this.tapListener = new GeoObjectTapListener() {
            @Override
            public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
                GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent.getGeoObject()
                        .getMetadataContainer()
                        .getItem(GeoObjectSelectionMetadata.class);

                if (selectionMetadata != null) {
                    map.selectGeoObject(selectionMetadata);
                    return true;
                }
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
                map.deselectGeoObject();
                searchSession = searchManager.submit(point, 20, new SearchOptions(), searchListener);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            }
        };
        this.mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                try {
                    if (mapObject instanceof PlacemarkMapObject) {

                        PlacemarkMapObject placemark = (PlacemarkMapObject) mapObject;

                        Object data = placemark.getUserData();
                        if (data instanceof Place) {
                            Place place = (Place) data;
                            showPlaceInfo(place);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e("Listener error", e.getMessage());
                }
                return true;
            }
        };
        map.addTapListener(tapListener);
        map.addInputListener(inputListener);

    }

    private void initialRequest() {
        viewModel.loadPlaces(55.75, 37.61, 5000000.0);
        moveToStartLocation();
    }

    private void moveToStartLocation() {
        map.move(
                new CameraPosition(startLocation, zoomValue, 0.0F, 0.0F),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    /*
        marker functions
     */

    public void updateMarkers(List<Place> places) {
        for (Place place : places) {
            if (mapObjectDictionary.containsKey(place)) {
                MapObject mapObject = mapObjectDictionary.get(place);
                mapObjectCollection.remove(mapObject);
            }
            setMarkerInPoint(new Point(place.getLatitude(), place.getLongitude()), place);
        }
    }

    public void setMarkerInPoint(Point location, Place place) {
        var marker = BitmapUtils.createBitmapFromVector(getSafeContext(), R.drawable.ic_pin_blue_svg);

        if (marker == null) {
            Log.e("MapWindow", "Null marker in MapWindow method");
        }

        MapObject mapObject = mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
            @Override
            public void onPlacemarkCreated(@NonNull PlacemarkMapObject placemarkMapObject) {
                placemarkMapObject.setGeometry(location);
                placemarkMapObject.setIcon(ImageProvider.fromBitmap(Objects.requireNonNull(marker)));
                float initialScale = zoomValue / 30.0f;
                placemarkMapObject.setIconStyle(new IconStyle().setScale(initialScale));
                placemarkMapObject.setZIndex(100);
                placemarkMapObject.setUserData(place);
                placemarkMapObject.addTapListener(mapObjectTapListener);

            }
        });

        mapObjectDictionary.put(place, mapObject);

    }

    /*
        UI functions for popups
     */

    private void showPlaceInfo(Place place) {
        try {
            mapActivity.showDialogWindow(place);
        } catch (Exception e) {
            Log.e("Map dialog error", e.getMessage());
        }
    }

    /*
        Camera functions
     */

    @Override
    public void onCameraPositionChanged(
            @NonNull Map map,
            @NonNull CameraPosition cameraPosition,
            @NonNull CameraUpdateReason cameraUpdateReason,
            boolean finished
    ) {
        if (finished && mapObjectCollection != null) {
            final float currentZoom = cameraPosition.getZoom();
            final boolean shouldUseDetailedIcons = currentZoom >= COMFORTABLE_ZOOM_LEVEL;

            mapObjectCollection.traverse(new MapObjectVisitor() {
                @Override
                public void onPlacemarkVisited(@NonNull PlacemarkMapObject placemark) {
                    if (placemark.getUserData() == null || placemark.getUserData() == POPUP_USER_DATA)
                        return;


                    int iconRes = shouldUseDetailedIcons
                            ? R.drawable.search_result
                            : R.drawable.ic_pin_blue_svg;

                    ImageProvider icon = ImageProvider.fromBitmap(
                            BitmapUtils.createBitmapFromVector(getSafeContext(), iconRes)
                    );

                    IconStyle style = new IconStyle();
                    style.setScale(currentZoom / 60f);

                    placemark.setIcon(icon);
                    placemark.setIconStyle(style);
                }

                @Override
                public void onPolylineVisited(@NonNull PolylineMapObject o) {
                }

                @Override
                public void onPolygonVisited(@NonNull PolygonMapObject o) {
                }

                @Override
                public void onCircleVisited(@NonNull CircleMapObject o) {
                }

                @Override
                public boolean onCollectionVisitStart(@NonNull MapObjectCollection c) {
                    return true;
                }

                @Override
                public void onCollectionVisitEnd(@NonNull MapObjectCollection c) {
                }

                @Override
                public boolean onClusterizedCollectionVisitStart(@NonNull ClusterizedPlacemarkCollection c) {
                    return false;
                }

                @Override
                public void onClusterizedCollectionVisitEnd(@NonNull ClusterizedPlacemarkCollection c) {
                }
            });

            zoomValue = currentZoom;
        }
    }

    public void moveCamera(Point point) {
        map.move(
                new CameraPosition(point, COMFORTABLE_ZOOM_LEVEL, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    /*
        User Location Setup
     */

    private void subscribeToLocationUpdate() {
        if (locationManager != null && locationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, locationListener);
        }
    }

    public void moveToUserLocation() {
        if (userLocation != null) {
            moveCamera(userLocation);
        }
    }

    private void setupLocationManager() {
        locationTrackingEnabled = true;
        this.locationManager = MapKitFactory.getInstance().createLocationManager();
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                userLocation = location.getPosition();
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

    /*
        Enabling location tracking, check permissions
     */

    public void enableLocationTracking() {
        if (checkPermission() && !locationTrackingEnabled) {
            setupLocationManager();
            locationTrackingEnabled = true;
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
            mapObjectCollection.clear();
            map.getMapObjects().clear();
        }
    }

    // getters

    private Context getSafeContext() {
        Context context = contextRef.get();
        return context != null ? context : ElGupoApplication.getAppContext();
    }

}
