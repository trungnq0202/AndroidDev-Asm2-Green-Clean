Student Id : 3742774
Student Name : Ngo Quang trung

Superuser account: 
	Email : admin123@gmail.com
	Password : admin123

ATTENTION: Please set the current location of the emulator to a place in Vietnam as all of the 
	   pre-created event sites are in Vietnam. However, the real scope is still the whole world.

ATTENTION: The apk file name is GreenAndClean.apk


FEATURES:
- Login/Register new user / Technology: Google Firebase FireStore
	+ Superuser cannot be created from the app.
	+ Please use the superuser account above to test the superuser's feature.
	
- User can logout by clicking on the "Logout item" which lies in the "3-dots menu" at the top left of the toolbar.

- Superuser can edit/get participants list of all created event sites.
			
- Show available event sites / Technology: GCP Maps SDK for Android
	+ All created event sites are shown around the world (with custom marker as green leaf)
	+ On decreasing camera zoom level, if more than 2 markers are too close together, they with combined into a cluster marker
		with a number indicating the total number of event sites within that cluster marker.
	+ On increasing camera zoom level, if the markers within a cluster are far from each other enough, the cluster will
		decompose into specific event site markers.
	+ User can see the event site's details by clicking on the marker

- Users can search for specific site using the top search bar / Technology: GCP Places API

- Users can join a site:
	+ Click on the event site's marker to view info, then click on the "Join" button

- Users can create new site and be admin of that site.
	+ Admin of a site can edit the details of that site.
	+ Admin of a site can see the participants list of that site.

- When there are changes made to a site, all the users who are participants of that site will have an Android notification appeared.
	+ Users can click on the notification to see the new details of the site.

- Users can find route from current user location to a specific event site.
	+ Click on the event site's marker to view info, then click on the "Show direction" button
	+ The route is drawn in read line.

- Users can get camera points to current location (in Streets zoom level) by clicking on the bottom left round "navigation icon" button.

All technologies used:
	- Google Places API (for finding/getting details of places available on Google Maps)
	- Google Maps SDK for Android (for displaying/manipulating Google Maps)
	- Google Directions API (for find route from an origin location to a destination)