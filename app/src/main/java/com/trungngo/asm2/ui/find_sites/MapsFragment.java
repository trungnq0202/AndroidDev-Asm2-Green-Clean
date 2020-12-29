package com.trungngo.asm2.ui.find_sites;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.trungngo.asm2.model.MyClusterItem;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.create_site.CreateSiteViewModel;
import com.trungngo.asm2.utilities.GooglePlaceAddressComponentsParser;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

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

    //Maps marker clustering
    private ClusterManager<MyClusterItem> clusterManager;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    User currentUserObject = null;
    String currentUserDocId = null;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    private void initMapsFragment() {
        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_maps);
        supportMapFragment.getMapAsync(this);
    }

    private void linkViewElements(View rootView) {
        getMyLocationBtn = rootView.findViewById(R.id.fragmentMapsFindMyLocationBtn);
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

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000); //30s
        locationRequest.setFastestInterval(15 * 1000); //15s
        locationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(location.getLatitude(),
                                location.getLongitude());
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                          mMap.clear();
//                        mMap.addMarker(new MarkerOptions().position(latLng)
//                                        .icon(BitmapDescriptorFactory.defaultMarker()));
//                        Toast.makeText(getActivity(),
//                                "(" + location.getLatitude() + ","+
//                                        location.getLongitude() +")",
//                                Toast.LENGTH_SHORT).show();
                    }
                }
                , null);
    }

    private void setActionHandlers() {
        setGetMyLocationBtnHandler();
        setPlaceSelectedActionHandler();
    }

    private void setGetMyLocationBtnHandler() {
        getMyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetPositionClick();
            }
        });
    }

    private void smoothlyMoveCameraToPosition(LatLng latLng, float zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

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
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_current_location_marker)));
                        smoothlyMoveCameraToPosition(latLng, Constants.CameraZoomLevel.streets);
//                        Toast.makeText(getActivity(),
//                                "(" + location.getLatitude() + ","+
//                                        location.getLongitude() +")",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        DrawableCompat.setTint(vectorDrawable, Color.BLUE);
        DrawableCompat.setTintMode(vectorDrawable, PorterDuff.Mode.SRC_IN);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        this.placesClient = Places.createClient(getActivity().getApplicationContext());
        mMap = googleMap;
        requestPermission();
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mMap.getUiSettings().setZoomControlsEnabled(true);
        startLocationUpdate();
        setUpCluster();
        onGetPositionClick();  // Position the map.
    }

    private void setUpCluster() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyClusterItem>(getActivity(), mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        fetchSitesThenMakeClusters();
    }

    private void addClusterItem(LatLng latLng, String siteName) {
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        MyClusterItem offsetItem = new MyClusterItem(lat, lng, "Title " + siteName, "Snippet " + siteName);
        clusterManager.addItem(offsetItem);
    }

    private void fetchSitesThenMakeClusters() {
        db.collection(Constants.FSSite.siteCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                final String placeId = site.getLocationId();
                                final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                    Place place = response.getPlace();
                                    addClusterItem(place.getLatLng(), site.getSiteName());
//                                    Log.i(TAG, "Place found: " + place.getName());

                                }).addOnFailureListener((exception) -> {
                                    if (exception instanceof ApiException) {
                                        final ApiException apiException = (ApiException) exception;
//                                        Log.e(TAG, "Place not found: " + exception.getMessage());
                                        final int statusCode = apiException.getStatusCode();
                                        // TODO: Handle error with given status code.
                                    }
                                });
                            }
                        } else {

                        }
                    }
                });
    }

    private void initGooglePlacesAutocomplete() {
        //Init the SDK
        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyDIMOeueEaTD8QrMDMPAkMQn7uN3WJpvOs");
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST);
    }

    // Set up a PlaceSelectionListener to handle the response.
    private void setPlaceSelectedActionHandler() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                smoothlyMoveCameraToPosition(place.getLatLng(), Constants.CameraZoomLevel.betweenCityAndStreets);
            }

            @Override
            public void onError(@NotNull Status status) {
                Toast.makeText(getActivity().getApplicationContext(),
                        Constants.ToastMessage.placeAutocompleteError + status, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
        System.out.println("Destroying MapsFragment View");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapsViewModel.class);

        // TODO: Use the ViewModel
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
}