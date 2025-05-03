package com.ru.ami.hse.elgupo.map;

import static com.yandex.runtime.Runtime.getApplicationContext;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ru.ami.hse.elgupo.ElGupoApplication;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Place;
import com.ru.ami.hse.elgupo.serverrequests.ServerRequester;
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
import com.yandex.runtime.ui_view.ViewProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
    private final WeakReference<Context> contextRef;
    private final MapView mapView;
    private final Map map;
    private final SearchManager searchManager;
    // constants and states
    private final Point startLocation = new Point(59.9402, 30.315);
    private final MapObjectCollection mapObjectCollection;

    /*
        Location Manager properties
     */
    private Float zoomValue = 16.5F;
    private Session searchSession;
    private GeoObjectTapListener tapListener;
    private Session.SearchListener searchListener;
    private InputListener inputListener;
    private MapObjectTapListener mapObjectTapListener;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Point myLocation;

    /*
        popup properties
     */
    private boolean isFirstLocationUpdate = true;
    private View popupView;
    private ViewProvider popupProvider;
    private PlacemarkMapObject currentPopup;
    private MapObject currentMapObject;


    public MapWindow(MapView mapView_, Context context_) {
        contextRef = new WeakReference<>(context_);
        mapView = mapView_;
        map = mapView.getMapWindow().getMap();
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);

        mapObjectCollection = map.getMapObjects().addCollection();
        initPopup();

        initializeMap();
    }

    private void initializeMap() {
        setupListeners();
        initialSetup();
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
                hidePlaceInfo();
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
                if (mapObject instanceof PlacemarkMapObject) {
                    PlacemarkMapObject placemark = (PlacemarkMapObject) mapObject;

                    if (POPUP_USER_DATA.equals(placemark.getUserData()) || mapObject.equals(currentMapObject)) {
                        return true;
                    }

                    Object data = placemark.getUserData();
                    if (data instanceof Place) {
                        Place place = (Place) data;
                        currentMapObject = mapObject;
                        showPlaceInfo(place, new Point(place.getLatitude(), place.getLongitude()));
                        return true;
                    }
                }
                return true;
            }
        };
        map.addTapListener(tapListener);
        map.addInputListener(inputListener);

        // DELETE, SAMPLES!!!
        Point testLocation1 = new Point(60.9402, 30.315);
        Place testPlace1 = new Place(60.9402, 30.315, "Gosha");
        Point testLocation2 = new Point(59.9402, 30.815);
        Place testPlace2 = new Place(59.9402, 30.815, "George");

        setMarkerInPoint(testLocation1, testPlace1);
        setMarkerInPoint(testLocation2, testPlace2);
        // DELETE, SAMPLES
    }

    private void initialSetup() {
        moveToStartLocation();
        setMarkersInEvents();
    }

    private void moveToStartLocation() {
        map.move(
                new CameraPosition(startLocation, zoomValue, 0.0F, 0.0F),
                new Animation(Animation.Type.SMOOTH, 3F),
                null);
    }

    /*
        Set markers, UI functions for popups
     */

    private void setMarkersInEvents() {
        List<Place> events = new ArrayList<>();
        ServerRequester.getPlacesNearby(55.75, 37.61, 10, 5000000.0, new ServerRequester.PlacesCallback() {
            @Override
            public void onSuccess(List<Place> places) {
                events.addAll(places);
                Log.w("Events size", "Size: " + events.size());

                for (Place event : events) {
                    setMarkerInPoint(new Point(event.getLatitude(), event.getLongitude()), event);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("Network Error", "Request failed: " + t.getMessage());
            }
        });
    }

    public void setMarkerInPoint(Point location, Place place) {
        var marker = BitmapUtils.createBitmapFromVector(getSafeContext(), R.drawable.ic_pin_blue_svg);
        if (marker == null) {
            Log.e("MapWindow", "Null marker in MapWindow method");
        }
        mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
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

    }

    private void initPopup() {
        popupView = LayoutInflater.from(getSafeContext()).inflate(R.layout.popup_layout, null);

        popupProvider = new ViewProvider(popupView, true);

        currentPopup = mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
            @Override
            public void onPlacemarkCreated(@NonNull PlacemarkMapObject placemarkMapObject) {
                placemarkMapObject.setView(popupProvider);
                placemarkMapObject.setVisible(false);
                placemarkMapObject.setZIndex(1000);
            }
        });
    }

    private void showPlaceInfo(Place place, Point location) {
        if (currentPopup != null) {
            currentPopup.getParent().remove(currentPopup);
            currentPopup = null;
        }

        View popupView = LayoutInflater.from(getSafeContext()).inflate(R.layout.popup_layout, null);

        TextView title = popupView.findViewById(R.id.popup_title);
        TextView address = popupView.findViewById(R.id.popup_address);
        title.setText(place.getName());
        address.setText(place.getAddress());

        ViewProvider provider = new ViewProvider(popupView, false);

        currentPopup = mapObjectCollection.addPlacemark(new PlacemarkCreatedCallback() {
            @Override
            public void onPlacemarkCreated(@NonNull PlacemarkMapObject placemarkMapObject) {
                placemarkMapObject.setGeometry(new Point(location.getLatitude(), location.getLongitude()));
                placemarkMapObject.setView(provider);
                placemarkMapObject.setUserData(POPUP_USER_DATA);
            }
        });

        currentPopup.setZIndex(1000);
    }

    private void hidePlaceInfo() {
        if (currentPopup != null) {
            currentPopup.getParent().remove(currentPopup);
            currentPopup = null;
            currentMapObject = null;
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
                    style.setScale(currentZoom / 30f);

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

    private void moveCamera(Point point) {
        map.move(
                new CameraPosition(point, zoomValue, 0.0f, 0.0f),
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

    private void moveToUserLocation() {
        if (myLocation != null) {
            moveCamera(myLocation);
        }
    }

    private void setupLocationManager() {
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
