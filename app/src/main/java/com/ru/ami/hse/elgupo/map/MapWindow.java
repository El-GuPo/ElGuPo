package com.ru.ami.hse.elgupo.map;

import static com.yandex.runtime.Runtime.getApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ru.ami.hse.elgupo.MainActivity;
import com.ru.ami.hse.elgupo.R;
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

public class MapWindow implements CameraListener {
    private final Context context;
    private final MapView mapView;
    private final SearchManager searchManager;


    private Point startLocation = new Point(59.9402, 30.315);
    private static final float ZOOM_BOUNDARY = 16.4F;

    private Float zoomValue = 16.5F;
    private MapObjectCollection mapObjectCollection;
    private PlacemarkMapObject placemarkMapObject;
    private Session searchSession;

    private MainActivity mainActivity;

    public MapWindow(Context context_, MapView mapView_){
        context = context_;
        mapView = mapView_;
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);

        mapView.getMapWindow().getMap().addCameraListener(this);
        mapView.getMapWindow().getMap().addTapListener(tapListener);
        mapView.getMapWindow().getMap().addInputListener(inputListener);
    }

    private final MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            Toast.makeText(context.getApplicationContext(), "Эрмитаж — музей изобразительных искусств", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private final GeoObjectTapListener tapListener = new GeoObjectTapListener() {
        @Override
        public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
            GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent.getGeoObject()
                    .getMetadataContainer().getItem(GeoObjectSelectionMetadata.class);
            mapView.getMapWindow().getMap().selectGeoObject(selectionMetadata);
            return false;
        }
    };

    private final Session.SearchListener searchListener = new Session.SearchListener() {
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

    private final InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            searchSession = searchManager.submit(point, 20, new SearchOptions(), searchListener);
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

        }
    };

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
        Drawable drawable = ContextCompat.getDrawable(context, art);
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


}
