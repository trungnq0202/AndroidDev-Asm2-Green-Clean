package com.trungngo.asm2.ui.find_sites;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.model.GoogleMaps.CustomClusterItemRender;
import com.trungngo.asm2.model.GoogleMaps.MyClusterItem;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.utilities.DateStringParser;
import com.trungngo.asm2.utilities.DirectionsJSONParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener<MyClusterItem> {

    //View elements
    private FloatingActionButton getMyLocationBtn;

    //ViewModel
    private MapsViewModel mViewModel;

    //Google maps variables
    private static final int MY_LOCATION_REQUEST = 99;
    private SupportMapFragment supportMapFragment; //maps view
    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private Marker currentUserLocationMarker;
    private LatLng prevUserLocation;
    private MyClusterItem currentTargetLocationClusterItem;
    private LatLng prevTargetLocation;
    private ArrayList<Polyline> currentRoute = new ArrayList<>();

    //Maps marker clustering
    private ClusterManager<MyClusterItem> clusterManager;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    User currentUserObject = null;
    String currentUserDocId = null;

    //Role of User to site
    private enum UserRoleToSite {
        ADMIN, PARTICIPANT, NONE
    }

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    /**
     * Init Google MapsFragment
     */
    private void initMapsFragment() {
        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_maps);
        supportMapFragment.getMapAsync(this);
    }

    /**
     * Connect view elements of layout to this class variable
     * @param rootView
     */
    private void linkViewElements(View rootView) {
        getMyLocationBtn = rootView.findViewById(R.id.fragmentMapsFindMyLocationBtn);
    }

    /**
     * Set Action Handlers
     */
    private void setActionHandlers() {
        setGetMyLocationBtnHandler(); //Find My location Button listener
        setPlaceSelectedActionHandler(); //GooglePlaceAutocomplete listener
    }

    /**
     * //Find My location Button listener
     */
    private void setGetMyLocationBtnHandler() {
        getMyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetPositionClick();
            }
        });
    }

    /**
     * Smoothly change camera position with zoom level
     * @param latLng
     * @param zoomLevel
     */
    private void smoothlyMoveCameraToPosition(LatLng latLng, float zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    /**
     * Get BitmapDescriptor from drawable vector asset, for custom cluster marker
     * @param context
     * @param vectorResId
     * @param color
     * @return
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId, int color) {
        if (context == null) {
            return null;
        }
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        DrawableCompat.setTint(vectorDrawable, color);
        DrawableCompat.setTintMode(vectorDrawable, PorterDuff.Mode.SRC_IN);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Set up cluster items on Google maps
     */
    @SuppressLint("PotentialBehaviorOverride")
    private void setUpCluster() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyClusterItem>(getActivity(), mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        clusterManager.setRenderer(new CustomClusterItemRender(getActivity(), mMap, clusterManager));
        clusterManager.setOnClusterItemClickListener(this);

        fetchSitesThenMakeClusters();

        clusterManager.cluster();
    }

    /**
     * Add a cluster item to Google map
     * @param site
     */
    private void addClusterItem(Site site) {
        MyClusterItem clusterItem = new MyClusterItem(
                site.getPlaceLatitude(),
                site.getPlaceLongitude(),
                bitmapDescriptorFromVector(
                        getContext(),
                        R.drawable.ic_site_marker,
                        Color.GREEN
                ),
                site);
        clusterManager.addItem(clusterItem);
    }

    /**
     * Fetch all sites data to create clusters on Google map
     */
    private void fetchSitesThenMakeClusters() {
        db.collection(Constants.FSSite.siteCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
//                                final String placeId = site.getPlaceId();
                                addClusterItem(site);
                            }
                        } else {

                        }
                    }
                });
    }

    /**
     * Init GooglePlacesAutocomplete search bar
     */
    private void initGooglePlacesAutocomplete() {
        //Init the SDK
        String apiKey = getString(R.string.google_api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        this.placesClient = Places.createClient(getActivity().getApplicationContext());

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.maps_place_autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS,
                        Place.Field.ADDRESS_COMPONENTS,
                        Place.Field.PLUS_CODE
                ));
    }

    /**
     * Request user for location permission
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST);
    }

    /**
     * Set up a PlaceSelectionListener to handle the response
     */
    private void setPlaceSelectedActionHandler() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                smoothlyMoveCameraToPosition(place.getLatLng(), Constants.GoogleMaps.CameraZoomLevel.betweenCityAndStreets);
            }

            @Override
            public void onError(@NotNull Status status) {
                Toast.makeText(getActivity().getApplicationContext(),
                        Constants.ToastMessage.placeAutocompleteError + status, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Update current user location marker
     * @param newLatLng
     */
    private void updateCurrentUserLocationMarker(LatLng newLatLng) {
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        currentUserLocationMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(newLatLng)
                        .icon(bitmapDescriptorFromVector(
                                getActivity(),
                                R.drawable.ic_current_location_marker, Color.BLUE)
                        )
                        .title("You are here!")
        );
    }

    /**
     * Draw new route from current user location to target location
     */
    private void drawRoute() {
        prevUserLocation = new LatLng(currentUserLocationMarker.getPosition().latitude,
                currentUserLocationMarker.getPosition().longitude);
        prevTargetLocation = new LatLng(currentTargetLocationClusterItem.getPosition().latitude,
                currentTargetLocationClusterItem.getPosition().longitude);

        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API
        String url = getRouteUrl(currentUserLocationMarker.getPosition(), currentTargetLocationClusterItem.getPosition(), "driving");

        FetchDataTask fetchDataTask = new FetchDataTask();

        // Start fetching json data from Google Directions API
        fetchDataTask.execute(url);
    }

    /**
     * Update new route from current user location to target location
     */
    private void updateCurrentRoute() {
        if (currentTargetLocationClusterItem != null) {
            if (prevTargetLocation == null) {
                drawRoute();
            } else if ((currentTargetLocationClusterItem.getPosition().latitude != prevTargetLocation.latitude)
                    && (currentTargetLocationClusterItem.getPosition().longitude != prevTargetLocation.longitude)){
                drawRoute();
            }
            else {
                if ((currentUserLocationMarker.getPosition().latitude != prevUserLocation.latitude)
                        && (currentUserLocationMarker.getPosition().longitude != prevUserLocation.longitude)) {
                    drawRoute();
                }
            }
        }
    }

    /**
     * when Google map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) { //Init GooglePlaceAutocomplete if not existed
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        this.placesClient = Places.createClient(getActivity().getApplicationContext());
        mMap = googleMap;
        requestPermission(); //Request user for location permission
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mMap.getUiSettings().setZoomControlsEnabled(true);
        startLocationUpdate(); //Start location update listener
        setUpCluster(); //Set up cluster on Google Map
        onGetPositionClick();  // Position the map.
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Find my position action handler
     */
    @SuppressLint("MissingPermission")
    public void onGetPositionClick() {
        locationClient.getLastLocation().
                addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Toast.makeText(getActivity(),
                                    Constants.ToastMessage.currentLocationNotUpdatedYet,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentUserLocationMarker == null) {
                            updateCurrentUserLocationMarker(latLng);
                        }
                        smoothlyMoveCameraToPosition(latLng, Constants.GoogleMaps.CameraZoomLevel.streets);
//                        Toast.makeText(getActivity(),
//                                "(" + location.getLatitude() + ","+
//                                        location.getLongitude() +")",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * //Start location update listener
     */
    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000); //5s
        locationRequest.setFastestInterval(5 * 1000); //5s
        locationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        updateCurrentUserLocationMarker(latLng);
                        updateCurrentRoute();

                    }
                }
                , null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Init view
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        linkViewElements(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        initMapsFragment();
        initGooglePlacesAutocomplete();
        setActionHandlers();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapsViewModel.class);

        //Use the ViewModel
        mViewModel = ViewModelProviders.of(getActivity()).get(MapsViewModel.class);
        mViewModel.getCurrentUserObject().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUserObject = user;
            }
        });

        mViewModel.getCurrentUserDocId().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUserDocId = s;
            }
        });
    }


    /****************** Action handlers for each cluster item's custom info window **********************/

    /**
     * Fill all site details in custom info window (actually dialog)
     * @param item
     * @param role
     */
    @SuppressLint("SetTextI18n")
    private void setSiteDetailsToCustomInfoWindow(MyClusterItem item, UserRoleToSite role) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popupWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        dialogBuilder.setView(popupWindow);
        Dialog dialog = dialogBuilder.create();
        dialog.show();

        //Set event details to text view
        TextView customInfoEventName = popupWindow.findViewById(R.id.customInfoEventNameTextView);
        customInfoEventName.setText("Event: " + item.getSite().getSiteName());
        TextView customInfoWindowLocation = popupWindow.findViewById(R.id.customInfoWindowLocationTextView);
        customInfoWindowLocation.setText(item.getSite().getPlaceName());
        TextView customInfoWindowAddress = popupWindow.findViewById(R.id.customInfoWindowAddressTextView);
        customInfoWindowAddress.setText(item.getSite().getPlaceAddress());
        TextView customInfoWindowStartDate = popupWindow.findViewById(R.id.customInfoStartDateTextView);
        TextView customInfoWindowEndDate = popupWindow.findViewById(R.id.customInfoEndDateTextView);

        try {
            customInfoWindowStartDate.setText(DateStringParser.parseFromDateObjectDDMMYYYY(item.getSite().getStartDate()));
            customInfoWindowEndDate.setText(DateStringParser.parseFromDateObjectDDMMYYYY(item.getSite().getEndDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Show message based on the current user's role to site
        switch (role) {
            //Base on current user's role to this site
            case ADMIN: //Admin
                TextView customInfoAdminMsg = popupWindow.findViewById(R.id.customInfoAdminMsgTextView);
                customInfoAdminMsg.setVisibility(View.VISIBLE); //Show admin statement message
                break;
            case PARTICIPANT: //Participant
                TextView customInfoAlreadyJoinedMsg = popupWindow.findViewById(R.id.customInfoAlreadyJoinedMsgTextView);
                customInfoAlreadyJoinedMsg.setVisibility(View.VISIBLE); //Show participant statement message
                break;
            default: //None
                Button customInfoJoinBtn = popupWindow.findViewById(R.id.customInfoWindowJoinBtn);
                customInfoJoinBtn.setVisibility(View.VISIBLE); //Show join button
                setJoinEventBtnListener(item, popupWindow, dialog);
        }
        setShowDirectionBtnListener(item, popupWindow, dialog);
    }

    /**
     * Aciton handler for clicking show direction to site button
     * @param item
     * @param popupWindow
     * @param dialog
     */
    private void setShowDirectionBtnListener(MyClusterItem item, View popupWindow, Dialog dialog) {
        Button showDirectionBtn = popupWindow.findViewById(R.id.customInfoWindowShowDirectionBtn);
        showDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTargetLocationClusterItem = item;
                updateCurrentRoute();
                dialog.dismiss();
            }
        });
    }


    /**
     * Add new site to current user's list of own sites
     * @param item
     */
    private void updateCurrentUserParticipatingSites(MyClusterItem item) {
        currentUserObject.getParticipatingSitesId().add(item.getSite().getDocId());
        db.collection(Constants.FSUser.userCollection)
                .document(currentUserDocId)
                .update(Constants.FSUser.participatingSitesIdField, currentUserObject.getParticipatingSitesId())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }

    /**
     * Add this user doc id to site's participants' id list after clicking on join button
     * @param item
     */
    private void updateCurrentSiteParticipants(MyClusterItem item) {
        item.getSite().getParticipantsId().add(currentUserDocId);
        db.collection(Constants.FSSite.siteCollection)
                .document(item.getSite().getDocId())
                .update(Constants.FSSite.participantsIdField, item.getSite().getParticipantsId())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }

    /**
     * Action handler for clicking on join event button
     * @param item
     * @param popupWindow
     * @param dialog
     */
    private void setJoinEventBtnListener(MyClusterItem item, View popupWindow, Dialog dialog) {
        Button joinEventBtn = popupWindow.findViewById(R.id.customInfoWindowJoinBtn);
        joinEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentUserParticipatingSites(item);
                updateCurrentSiteParticipants(item);
                dialog.dismiss();
            }
        });

    }

    /**
     * Check if this current user have already participated in this event
     * @param item
     * @return
     */
    private boolean validateSiteParticipantsThenFillDetails(MyClusterItem item) {
        return item.getSite().getParticipantsId().contains(this.currentUserDocId);
    }

    /**
     * Check if this current user is the admin of this event
     * @param item
     * @return
     */
    private boolean validateSiteAdminThenFillDetails(MyClusterItem item) {
        return item.getSite().getAdminId().equals(this.currentUserDocId);
    }

    /**
     * Show A popup dialog containing site info when clicking on the cluster item marker
     * @param item
     * @return
     */
    @Override
    public boolean onClusterItemClick(MyClusterItem item) {
        if (validateSiteAdminThenFillDetails(item)) {
            setSiteDetailsToCustomInfoWindow(item, UserRoleToSite.ADMIN);
        } else if (validateSiteParticipantsThenFillDetails((item))) {
            setSiteDetailsToCustomInfoWindow(item, UserRoleToSite.PARTICIPANT);
        } else {
            setSiteDetailsToCustomInfoWindow(item, UserRoleToSite.NONE);
        }
        return true;
    }

    /**
     * A Class to call Google Directions API with callback
     */
    private class FetchDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = fetchDataFromURL(url[0]);
            } catch (Exception ignored) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            //Clear current route
            for (Polyline polyline : currentRoute) {
                polyline.remove();
            }
            currentRoute.clear();

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> route = result.get(i);

                for (int j = 0; j < route.size(); j++) {
                    HashMap<String, String> point = route.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            currentRoute.add(mMap.addPolyline(lineOptions));
        }
    }

    /**
     * Method to get URL for fetching data from Google Directions API (finding direction from origin to destination)
     * @param origin
     * @param destination
     * @param directionMode
     * @return
     */
    private String getRouteUrl(LatLng origin, LatLng destination, String directionMode) {
        String originParam = Constants.GoogleMaps.DirectionApi.originParam +
                "=" + origin.latitude + "," + origin.longitude;
        String destinationParam = Constants.GoogleMaps.DirectionApi.destinationParam +
                "=" + destination.latitude + "," + destination.longitude;
        String modeParam = Constants.GoogleMaps.DirectionApi.modeParam + "=" + directionMode;
        String params = originParam + "&" + destinationParam + "&" + modeParam;
        String output = Constants.GoogleMaps.DirectionApi.outputParam;
        return Constants.GoogleMaps.DirectionApi.baseUrl + output + "?" + params
                + "&key=" + getString(R.string.google_api_key);
    }

    /**
     * A method to fetch json data from url
     */
    private String fetchDataFromURL(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}

