package com.trungngo.asm2.ui.edit_site;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.create_site.CreateSiteViewModel;
import com.trungngo.asm2.utilities.DateStringParser;
import com.trungngo.asm2.utilities.GooglePlaceAddressComponentsParser;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditSiteFragment extends Fragment {

    //UI elements
    private TextView countryTextView;
    private TextView streetNumberTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView districtTextView;

    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText siteNameEditText;

    private Button startDateBtn;
    private Button endDateBtn;
    private Button editSiteBtn;

    private EditSiteViewModel mViewModel;
    private Site currentSite;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    User currentUserObject = null;
    String currentUserDocId = null;

    private int year, month, day;
    private Date startDate = null;
    private Date endDate = null;
    private Place chosenPlace = null;
    private Boolean usedPlace = null;

    public static EditSiteFragment newInstance() {
        return new EditSiteFragment();
    }

    /**
     * Connect view elements of layout to this class variable
     * @param rootView
     */
    private void linkViewElements(View rootView) {
        countryTextView = rootView.findViewById(R.id.editSiteCountryTextView);
        streetNumberTextView = rootView.findViewById(R.id.editSiteStreetNumberTextView);
        streetTextView = rootView.findViewById(R.id.editSiteStreetTextView);
        cityTextView = rootView.findViewById(R.id.editSiteCityTextView);
        startDateEditText = rootView.findViewById(R.id.editSiteStartDateEditText);
        endDateEditText = rootView.findViewById(R.id.editSiteEndDateEditText);
        siteNameEditText = rootView.findViewById(R.id.editSiteSiteNameEditText);
        districtTextView = rootView.findViewById(R.id.editSiteDistrictTextView);

        startDateBtn = rootView.findViewById(R.id.editSiteStartDateBtn);
        endDateBtn = rootView.findViewById(R.id.editSiteEndDateBtn);
        editSiteBtn = rootView.findViewById(R.id.editSiteEditBtn);

    }

    /**
     * Set address components contents on textView
     * @param parsedAddressComponents
     */
    private void setAddressComponentTextViewContent(HashMap<String, String> parsedAddressComponents) {
        countryTextView.setText(parsedAddressComponents.get(Constants.PlaceAddressComponentTypes.country));
        streetNumberTextView.setText(parsedAddressComponents.get(Constants.PlaceAddressComponentTypes.streetNumber));
        streetTextView.setText(parsedAddressComponents.get(Constants.PlaceAddressComponentTypes.route));
        cityTextView.setText(parsedAddressComponents.get(Constants.PlaceAddressComponentTypes.adminAreaLv1));
        districtTextView.setText(parsedAddressComponents.get(Constants.PlaceAddressComponentTypes.adminAreaLv2));
    }

    /**
     * Initialize GooglePlacesAutocomplete
     */
    private void initGooglePlacesAutocomplete() {
        //Init the SDK
        String apiKey = getString(R.string.google_maps_key);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        this.placesClient = Places.createClient(getActivity().getApplicationContext());

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.create_site_place_autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS,
                        Place.Field.ADDRESS_COMPONENTS
                ));
    }


    /**
     * Set up a PlaceSelectionListener to handle the response.
     */
    private void setPlaceSelectedActionHandler() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                //When user choose a new place by searching using GooglePlaceAutocomplete search bar
                chosenPlace = place;
                checkUsedLocation(); //Check if this location has been used to make an event
                setAddressComponentTextViewContent(GooglePlaceAddressComponentsParser
                        .parseAddressComponents(place)); //Set this place details' address components in textView
            }

            @Override
            public void onError(@NotNull Status status) {
                Toast.makeText(getActivity().getApplicationContext(),
                        Constants.ToastMessage.placeAutocompleteError + status, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * date picker dialog for start/end date choosing
     * @param dateBtn
     * @param dateEditText
     */
    private void setDatePickerBtnAction(Button dateBtn, EditText dateEditText) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(dateBtn.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

    }

    /**
     * Validation after input start/end date in the edit text
     * @param dateEditText
     */
    private void setDateEditTextAutoFormat(EditText dateEditText) {
        dateEditText.addTextChangedListener(new TextWatcher() {
            private String curDateStr = "";
            private final Calendar calendar = Calendar.getInstance();
            private final int tempYear = calendar.get(Calendar.YEAR);
            private final int maxYear = calendar.get(Calendar.YEAR) + 2;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Take action at most 1 number is changed at a time.
                if (!s.toString().equals(curDateStr) && count == 1) {
                    //Current date string in the edit text, after latest change, without the "/" character
                    String curDateStrAfterChangedWithoutSlash = s.toString().replaceAll("[^\\d.]|\\.", "");
                    //Current date string in the edit text, before the latest change, without the "/" character
                    String curDateStrBeforeChangedWithoutSlash = curDateStr.replaceAll("[^\\d.]|\\.", "");

                    int dateStrAfterChangedLen = curDateStrAfterChangedWithoutSlash.length();
                    int cursorPos = dateStrAfterChangedLen; //Cursor position

                    for (int i = 2; i <= dateStrAfterChangedLen && i < 6; i += 2) {
                        cursorPos++;
                    }

                    //If delete the slash character "/", move cursor back 1 position
                    if (curDateStrAfterChangedWithoutSlash.equals(curDateStrBeforeChangedWithoutSlash))
                        cursorPos--;

                    //If the current date string, after latest change, without slash, is not fully filled
                    if (curDateStrAfterChangedWithoutSlash.length() < 8) {
                        String dateFormat = "DDMMYYYY";
                        //
                        curDateStrAfterChangedWithoutSlash = curDateStrAfterChangedWithoutSlash
                                + dateFormat.substring(curDateStrAfterChangedWithoutSlash.length());
                    } else {
                        //Validate and fix the input date if necessary
                        int day = Integer.parseInt(curDateStrAfterChangedWithoutSlash.substring(0, 2));
                        int month = Integer.parseInt(curDateStrAfterChangedWithoutSlash.substring(2, 4));
                        int year = Integer.parseInt(curDateStrAfterChangedWithoutSlash.substring(4, 8));

                        month = month < 1 ? 1 : Math.min(month, 12); //Max month is 12
                        calendar.set(Calendar.MONTH, month - 1);

                        year = (year < 1900) ? tempYear : Math.min(year, maxYear); //Max year for event is this year
                        calendar.set(Calendar.YEAR, year);

                        //Get the right day according to the input year and month
                        day = Math.min(day, calendar.getActualMaximum(Calendar.DATE));
                        curDateStrAfterChangedWithoutSlash = String.format("%02d%02d%02d", day, month, year);
                    }

                    //finalize the form of displayed date string
                    curDateStrAfterChangedWithoutSlash = String.format("%s/%s/%s", curDateStrAfterChangedWithoutSlash.substring(0, 2),
                            curDateStrAfterChangedWithoutSlash.substring(2, 4),
                            curDateStrAfterChangedWithoutSlash.substring(4, 8));

                    //Set date string as text in the EditText view and set the cursor position, update current date string
                    cursorPos = Math.max(cursorPos, 0);
                    curDateStr = curDateStrAfterChangedWithoutSlash;
                    dateEditText.setText(curDateStr);
                    dateEditText.setSelection(Math.min(cursorPos, curDateStr.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Validate if site details are enough (country, street, city, district fields must not be empty)
     * @return
     */
    private boolean checkSiteLocationDetailsNotEnough() {
        return
                countryTextView.getText().toString().isEmpty()
                        || streetTextView.getText().toString().isEmpty()
                        || cityTextView.getText().toString().isEmpty()
                        || districtTextView.getText().toString().isEmpty();
    }

    /**
     * Validate empty date
     * @return
     */
    private boolean checkEmptyEventDate() {
        return
                startDateEditText.getText().toString().isEmpty()
                        || endDateEditText.getText().toString().isEmpty();
    }

    /**
     * Validate if startDate is after endDate
     * @param startDate
     * @param endDate
     * @return
     */
    private boolean checkEventDateWrongOrder(Date startDate, Date endDate) {
        return startDate.after(endDate);
    }

    /**
     * Check if the site name is not entered by the user yet
     * @return
     */
    private boolean checkEmptySiteName() {
        return siteNameEditText.getText().toString().isEmpty();
    }

    /**
     * Check if this place has been used to make an event
     */
    private void checkUsedLocation() {
        db.collection(Constants.FSUsedLocations.usedLocationsCollection)
                .whereEqualTo(Constants.FSUsedLocations.locationIdField, chosenPlace.getId())
                .limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usedPlace = !task.getResult().isEmpty();
                        } else {

                        }
                    }
                });
    }

    /**
     * Validate all site details
     * @return
     */
    private boolean validateSiteDetails() {
        //Validate if this location has been used to make an event
        if (usedPlace) {
            Toast.makeText(getActivity().getApplicationContext(),
                    Constants.ToastMessage.existedEventThatUsedThisLocation, Toast.LENGTH_LONG).show();
            return false;
        }

        //Validate if site details are enough
        if (checkSiteLocationDetailsNotEnough()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    Constants.ToastMessage.notEnoughDetailsOfSite, Toast.LENGTH_LONG).show();
            return false;
        }

        //Check if the event date is empty
        if (checkEmptyEventDate()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    Constants.ToastMessage.emptyEventDateError, Toast.LENGTH_LONG).show();
            return false;
        }

        //Parse date strings to date objects
        try {
            startDate = DateStringParser.parseFromDateStringMMDDYYYY(startDateEditText.getText().toString());
            endDate = DateStringParser.parseFromDateStringMMDDYYYY(endDateEditText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (checkEmptySiteName()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    Constants.ToastMessage.emptySiteNameError, Toast.LENGTH_LONG).show();
        }

        //Validate event date order (startDate must be before endDate)
        if (checkEventDateWrongOrder(startDate, endDate)) {
            Toast.makeText(getActivity().getApplicationContext(),
                    Constants.ToastMessage.wrongEventDateOrder, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    /**
     * Set editSiteBtn Action Listener
     */
    private void setEditSiteBtnHandler() {
        editSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateSiteDetails()) {
                    return;
                }
                //TODO
                submitSiteEditDetails();
            }
        });
    }

    /**
     * Submit Site details to FireStore database
     */
    private void submitSiteEditDetails(){
        Map<String, Object> siteData = new HashMap<>();
        siteData.put(Constants.FSSite.siteNameField, siteNameEditText.getText().toString());
        siteData.put(Constants.FSSite.startDateField, startDate);
        siteData.put(Constants.FSSite.endDateField, endDate);

        siteData.put(Constants.FSSite.placeIdField, chosenPlace.getId());
        siteData.put(Constants.FSSite.placeName, chosenPlace.getName());
        siteData.put(Constants.FSSite.placeLatitude, chosenPlace.getLatLng().latitude);
        siteData.put(Constants.FSSite.placeLongitude, chosenPlace.getLatLng().longitude);
        siteData.put(Constants.FSSite.placeAddress, chosenPlace.getAddress());

        db.collection(Constants.FSSite.siteCollection).document(currentSite.getDocId())
                .update(siteData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            moveToSuperUserFragment();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    Constants.ToastMessage.editSiteSuccess,
                                    Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                });
    }

    /**
     * Create necessary action handlers
     */
    private void setActionHandlers() {
        //Set OnPlaceSelectedActionHandler to fill all location details editText
        setPlaceSelectedActionHandler();

        //Set startDateBtn Action Handler
        setDatePickerBtnAction(startDateBtn, startDateEditText);
        setDateEditTextAutoFormat(startDateEditText);

        //Set endDateBtn Action Handler
        setDatePickerBtnAction(endDateBtn, endDateEditText);
        setDateEditTextAutoFormat(endDateEditText);

        //Set createSiteBtn Action Handler
        setEditSiteBtnHandler();
    }


    /**
     * Fetch current place's details and set address components on textViews
     */
    private void setCurrentPlaceDetails(){
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(currentSite.getPlaceId(), placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            chosenPlace = place;
            usedPlace = false;
            setAddressComponentTextViewContent(GooglePlaceAddressComponentsParser
                    .parseAddressComponents(place));

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                final int statusCode = apiException.getStatusCode();

            }
        });
    }

    /**
     * Move to superUserFragment
     */
    private void moveToSuperUserFragment(){
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_superuser);
    }

    /**
     * Set current siteDetails to textViews
     * @throws ParseException
     */
    private void setCurrentSiteDetails() throws ParseException {
        siteNameEditText.setText(currentSite.getSiteName());
        startDateEditText.setText(DateStringParser.parseFromDateObjectDDMMYYYY(currentSite.getStartDate()));
        endDateEditText.setText(DateStringParser.parseFromDateObjectDDMMYYYY(currentSite.getEndDate()));
    }

    /**
     * Fill all temporary info in all textViews of EditForm
     * @throws ParseException
     */
    private void fillEditForm() throws ParseException {
        if (currentSite != null){
            setCurrentPlaceDetails();
            setCurrentSiteDetails();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_site, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        linkViewElements(view); //Link view elements to class properties
        initGooglePlacesAutocomplete();
        try {
            fillEditForm();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setActionHandlers();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Use the ViewModel
        mViewModel = ViewModelProviders.of(getActivity()).get(EditSiteViewModel.class);
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

        mViewModel.getCurrentSite().observe(getActivity(), new Observer<Site>() {
            @Override
            public void onChanged(Site site) {
                currentSite = site;
                try {
                    fillEditForm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}