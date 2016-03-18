package com.gtavtrainer.ui.trainer;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.gtavtrainer.R;
import com.gtavtrainer.net.client.Client;
import com.gtavtrainer.net.client.ClientAsyncTask;
import com.gtavtrainer.net.data.Data;
import com.gtavtrainer.net.data.EventData;
import com.gtavtrainer.ui.MainActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Manoj Khanna
 */

public class MapFragment extends SupportMapFragment {

    private static final String TILE_FILE_NAME = "map/%d/%d/%d.jpg";
    private static final int TILE_SIZE = 512;
    private static final int MAX_ZOOM = 4;

    private static final float MIN_X_GAME_MAP = -3747.0f;
    private static final float MAX_X_GAME_MAP = 4500.0f;
    private static final float MIN_Y_GAME_MAP = 8022.0f;
    private static final float MAX_Y_GAME_MAP = -3946.0f;
    private static final float X_DISTANCE_GAME_MAP = MAX_X_GAME_MAP - MIN_X_GAME_MAP;
    private static final float Y_DISTANCE_GAME_MAP = -(MAX_Y_GAME_MAP - MIN_Y_GAME_MAP);

    private static final float MIN_X_GOOGLE_MAP = 1283.0f;
    private static final float MAX_X_GOOGLE_MAP = 6723.0f;
    private static final float MIN_Y_GOOGLE_MAP = 240.0f;
    private static final float MAX_Y_GOOGLE_MAP = 8135.0f;
    private static final float X_DISTANCE_GOOGLE_MAP = MAX_X_GOOGLE_MAP - MIN_X_GOOGLE_MAP;
    private static final float Y_DISTANCE_GOOGLE_MAP = MAX_Y_GOOGLE_MAP - MIN_Y_GOOGLE_MAP;

    private Marker mPlayerMarker, mWaypointMarker;
    private boolean mMarkerDragging;
    private MarkerAsyncTask mMarkerAsyncTask;
    private TeleportAsyncTask mTeleportAsyncTask;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                googleMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(new TileProvider() {

                            @Override
                            public Tile getTile(int x, int y, int zoom) {
                                if (zoom <= MAX_ZOOM) {
                                    try {
                                        InputStream inputStream = getContext().getAssets().open(String.format(TILE_FILE_NAME, zoom, x, y));
                                        byte[] bytes = new byte[inputStream.available()];
                                        //noinspection ResultOfMethodCallIgnored
                                        inputStream.read(bytes);
                                        inputStream.close();

                                        return new Tile(TILE_SIZE, TILE_SIZE, bytes);
                                    } catch (IOException e) {
                                        Log.e(MapFragment.class.getName(), e.getMessage(), e);
                                    }
                                }

                                return null;
                            }

                        })
                        .fadeIn(true));
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        if (cameraPosition.zoom > MAX_ZOOM) {
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
                        }
                    }

                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {
                        mTeleportAsyncTask = (TeleportAsyncTask) new TeleportAsyncTask().execute(latLng);
                    }

                });
                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        mMarkerDragging = true;
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        mMarkerDragging = false;

                        mTeleportAsyncTask = (TeleportAsyncTask) new TeleportAsyncTask().execute(marker.getPosition());
                    }

                });

                final View view = View.inflate(getContext(), R.layout.marker_info_map, null);
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        String title = marker.getTitle();
                        if (title != null) {
                            TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view_map);
                            titleTextView.setText(title);
                        }

                        String snippet = marker.getSnippet();
                        if (snippet != null) {
                            TextView snippetTextView = (TextView) view.findViewById(R.id.snippet_text_view_map);
                            snippetTextView.setText(snippet);
                        }

                        return view;
                    }

                });

                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setMapToolbarEnabled(false);
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setAllGesturesEnabled(false);
                uiSettings.setZoomGesturesEnabled(true);
                uiSettings.setScrollGesturesEnabled(true);

                mPlayerMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(85.0, -180.0))
                        .title("Player")
                        .draggable(true)
                        .visible(false));

                mWaypointMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(85.0, -180.0))
                        .title("Waypoint")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .visible(false));

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    mMarkerAsyncTask = (MarkerAsyncTask) new MarkerAsyncTask().execute();
                } else {
                    mMarkerAsyncTask = (MarkerAsyncTask) new MarkerAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMarkerAsyncTask != null) {
            mMarkerAsyncTask.cancel(true);
        }

        if (mTeleportAsyncTask != null) {
            mTeleportAsyncTask.cancel(true);
        }
    }

    private LatLng gameMapToGoogleMap(Vector2 vector2) {
        int zoomFactor = 1 << MAX_ZOOM;

        double latitude = -(vector2.getY() - MIN_Y_GAME_MAP) / (Y_DISTANCE_GAME_MAP / Y_DISTANCE_GOOGLE_MAP) + MIN_Y_GOOGLE_MAP;
        latitude = Math.pow(Math.E, -(latitude / TILE_SIZE / zoomFactor - 0.5) * 4.0 * Math.PI);
        latitude = (latitude - 1.0) / (latitude + 1.0);
        latitude = Math.asin(latitude) * 180.0 / Math.PI;

        double longitude = (vector2.getX() - MIN_X_GAME_MAP) / (X_DISTANCE_GAME_MAP / X_DISTANCE_GOOGLE_MAP) + MIN_X_GOOGLE_MAP;
        longitude = (longitude / TILE_SIZE / zoomFactor - 0.5) * 360.0;

        return new LatLng(latitude, longitude);
    }

    private Vector2 googleMapToGameMap(LatLng latLng) {
        int zoomFactor = 1 << MAX_ZOOM;

        double x = TILE_SIZE * (0.5 + latLng.longitude / 360.0) * zoomFactor;
        x = MIN_X_GAME_MAP + (x - MIN_X_GOOGLE_MAP) * X_DISTANCE_GAME_MAP / X_DISTANCE_GOOGLE_MAP;

        double sinY = Math.sin(latLng.latitude * Math.PI / 180.0);
        sinY = Math.min(Math.max(sinY, -0.9999), 0.9999);

        double y = TILE_SIZE * (0.5 - Math.log((1.0 + sinY) / (1.0 - sinY)) / (4.0 * Math.PI)) * zoomFactor;
        y = MIN_Y_GAME_MAP - (y - MIN_Y_GOOGLE_MAP) * Y_DISTANCE_GAME_MAP / Y_DISTANCE_GOOGLE_MAP;

        return new Vector2((float) x, (float) y);
    }

    private class Vector2 {

        private float mX, mY;

        public Vector2(float x, float y) {
            mX = x;
            mY = y;
        }

        public float getX() {
            return mX;
        }

        public float getY() {
            return mY;
        }

    }

    private class MarkerAsyncTask extends ClientAsyncTask<Void, EventData, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                Client client = ((MainActivity) getActivity()).getClient();
                if (client != null) {
                    client.write(new EventData("map", "get_markers"));

                    EventData eventData = client.read();
                    if (eventData != null) {
                        publishProgress(eventData);
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(MapFragment.class.getName(), e.getMessage(), e);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(EventData... values) {
            super.onProgressUpdate(values);

            EventData eventData = values[0];

            if (!mMarkerDragging) {
                Data playerData = eventData.getData("player"),
                        vector2Data = playerData.getData("vector2");
                float x = vector2Data.getFloat("x"),
                        y = vector2Data.getFloat("y");
                String street = playerData.getString("street"),
                        zone = playerData.getString("zone");

                mPlayerMarker.setPosition(gameMapToGoogleMap(new Vector2(x, y)));
                mPlayerMarker.setSnippet(street + "\n" + zone);
                mPlayerMarker.setVisible(true);

                if (mPlayerMarker.isInfoWindowShown()) {
                    mPlayerMarker.hideInfoWindow();
                    mPlayerMarker.showInfoWindow();
                }
            }

            if (eventData.contains("waypoint")) {
                Data waypointData = eventData.getData("waypoint"),
                        vector2Data = waypointData.getData("vector2");
                float x = vector2Data.getFloat("x"),
                        y = vector2Data.getFloat("y");
                String street = waypointData.getString("street"),
                        zone = waypointData.getString("zone");

                mWaypointMarker.setPosition(gameMapToGoogleMap(new Vector2(x, y)));
                mWaypointMarker.setSnippet(street + "\n" + zone);
                mWaypointMarker.setVisible(true);

                if (mWaypointMarker.isInfoWindowShown()) {
                    mWaypointMarker.hideInfoWindow();
                    mWaypointMarker.showInfoWindow();
                }
            } else {
                mWaypointMarker.setVisible(false);
            }
        }

    }

    private class TeleportAsyncTask extends ClientAsyncTask<LatLng, Void, Void> {

        @Override
        protected Void doInBackground(LatLng... params) {
            Client client = ((MainActivity) getActivity()).getClient();
            if (client != null) {
                EventData eventData = new EventData("map", "teleport");

                Vector2 vector2 = googleMapToGameMap(params[0]);
                eventData.putData("vector2", new Data()
                        .putFloat("x", vector2.getX())
                        .putFloat("y", vector2.getY()));

                client.write(eventData);
            }

            return null;
        }

    }

}
