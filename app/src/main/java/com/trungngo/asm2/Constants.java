package com.trungngo.asm2;

import java.util.List;

public class Constants {
    //Fields of FireStore 'users' collection
    public static class FSUser {
        public static final String userCollection = "users";
        public static final String usernameField = "username";
        public static final String phoneField = "phone";
        public static final String birthDateField = "birthDate";
        public static final String genderField = "gender";
        public static final String emailField = "email";
        public static final String ownSitesIdField = "ownSitesId";
        public static final String participatingSitesIdField = "participatingSitesId";
        public static final String superuserField = "superuser";
    }

    public static class FSSite {
        public static final String siteCollection = "sites";
        public static final String siteNameField = "siteName";
        public static final String adminIdField = "adminId";
        public static final String participantsIdField = "participantsId";
        public static final String startDateField = "startDate";
        public static final String endDateField = "endDate";

        public static final String placeIdField = "placeId";
        public static final String placeName = "placeName";
        public static final String placeLatitude = "placeLatitude";
        public static final String placeLongitude = "placeLongitude";
        public static final String placeAddress = "placeAddress";
    }

    public static class FSUsedLocations {
        public static final String usedLocationsCollection = "usedLocations";
        public static final String locationIdField = "locationId";
    }

    //All Toast messages being used
    public static class ToastMessage {
        public static final String emptyInputError = "Please fill in your account authentication.";
        public static final String signInSuccess = "Sign in successfully!";
        public static final String signInFailure = "Invalid email/password!";
        public static final String registerSuccess = "Successfully registered";
        public static final String registerFailure = "Authentication failed, email must be unique and has correct form!";
        public static final String retrieveUsersInfoFailure = "Error querying for all users' information!";
        public static final String emptyMessageInputError = "Please type your message to send!";

        //Create site validation message
        public static final String notEnoughDetailsOfSite = "Please choose a more specific site!";
        public static final String wrongEventDateOrder = "Event start date must be before end date!";
        public static final String createSiteSuccess = "Create new site successfully!";
        public static final String emptyEventDateError = "Please choose your start/end event date!";
        public static final String placeAutocompleteError = "Google PlaceAutocomplete error with code: ";
        public static final String emptySiteNameError = "Please give your event site a name!";
        public static final String successfullyCreateSite = "Create new site successfully!";
        public static final String existedEventThatUsedThisLocation = "This location has been used to make an event, please choose another location!";

        //Maps Error Handling
        public static final String currentLocationNotUpdatedYet = "Please wait for a few seconds for current location to be updated!";
        public static final String routeRenderingInProgress = "Please wait, the route is being rendered!";

        //Edit site Message
        public static final String editSiteSuccess = "Edit site successfully!";
    }

    public static class PlaceAddressComponentTypes {
        public static final String premise = "premise";
        public static final String streetNumber = "street_number";
        public static final String route = "route";
        public static final String adminAreaLv1 = "administrative_area_level_1";
        public static final String adminAreaLv2 = "administrative_area_level_2";
        public static final String country = "country";
    }

    public static class MenuItemsIndex {
        public static final int myCreatedSitesItemIndex = 0;
        public static final int joinSitesItemIndex = 1;
        public static final int createSiteItemIndex = 2;
    }

    public static class GoogleMaps {
        public static class CameraZoomLevel {
            public static final int city = 10;
            public static final int streets = 15;
            public static final float betweenCityAndStreets = (float) 12.5;
        }

        public static class DirectionApi {
            public static final String baseUrl = "https://maps.googleapis.com/maps/api/directions/";
            public static final String originParam = "origin";
            public static final String destinationParam = "destination";
            public static final String modeParam = "mode";
            public static final String outputParam = "json";


        }

    }
}