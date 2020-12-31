package com.trungngo.asm2.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.trungngo.asm2.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GooglePlaceAddressComponentsParser {
    static final List<String> ACCEPTED_TYPES = new ArrayList<String>() {{
        add(Constants.PlaceAddressComponentTypes.adminAreaLv1);
        add(Constants.PlaceAddressComponentTypes.adminAreaLv2);
        add(Constants.PlaceAddressComponentTypes.premise);
        add(Constants.PlaceAddressComponentTypes.streetNumber);
        add(Constants.PlaceAddressComponentTypes.route);
        add(Constants.PlaceAddressComponentTypes.country);
    }};


    public static HashMap<String, String> parseAddressComponents(Place place){
        AddressComponents addressComponents = place.getAddressComponents();
        HashMap<String, String> parsedAddressComponents = new HashMap<>();
        for (AddressComponent addressComponent :
                addressComponents.asList()) {
            for (String type: addressComponent.getTypes()){
                if (ACCEPTED_TYPES.contains(type)){
                    parsedAddressComponents.put(type, addressComponent.getName());
                }
            }
        }

        return parsedAddressComponents;
    }

}
