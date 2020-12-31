package com.trungngo.asm2.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.create_site.CreateSiteViewModel;
import com.trungngo.asm2.ui.edit_site.EditSiteViewModel;
import com.trungngo.asm2.ui.find_sites.MapsViewModel;
import com.trungngo.asm2.ui.my_created_sites.HomeViewModel;
import com.trungngo.asm2.ui.my_participating_sites.ParticipatingSitesViewModel;
import com.trungngo.asm2.ui.site_detail.SiteDetailViewModel;
import com.trungngo.asm2.ui.superuser.SuperUserViewModel;
import com.trungngo.asm2.ui.participants_list.ParticipantsListViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TextView navHeaderEmailTextView;
    private TextView navHeaderUsernameTextView;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    //Current user info
    User currentUserObject = null;
    String currentUserDocId = null;

    //All child fragments' viewModals
    CreateSiteViewModel createSiteViewModel;
    MapsViewModel mapsViewModel;
    EditSiteViewModel editSiteViewModel;
    HomeViewModel homeViewModel;
    ParticipatingSitesViewModel participatingSitesViewModel;
    SuperUserViewModel superUserViewModel;
    ParticipantsListViewModel participantsListViewModel;
    SiteDetailViewModel siteDetailViewModel;

    //Notification
//    NotificationManager notificationManager;

    private AppBarConfiguration mAppBarConfiguration;

    /**
     * Set up navigation drawer activity
     */
    private void navigationDrawerSetup() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_participating_sites,
                R.id.nav_superuser, R.id.nav_maps,
                R.id.create_site)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * Connect view elements of layout to this class variable
     */
    private void linkViewElements() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout navHeaderView = (LinearLayout) navigationView.getHeaderView(0);
        navHeaderUsernameTextView = (TextView) navHeaderView.getChildAt(1);
        navHeaderEmailTextView = (TextView) navHeaderView.getChildAt(2);
    }

    /**
     * Set nav header username and email
     */
    private void setNavHeaderEmailAndUsername() {
        navHeaderEmailTextView.setText(currentUser.getEmail());
        navHeaderUsernameTextView.setText(currentUserObject.getUsername());
    }

    /**
     * Get instances of Firebase FireStore Auth, db, current user
     */
    private void initFirebaseCurrentUserInfo() {
        //Get instances of Firebase FireStore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        getCurrentUserObject(); //Get current user object info
    }

    /**
     * Init all child fragments' view models
     */
    private void initAllChildFragmentsViewModel() {
        createSiteViewModel = ViewModelProviders.of(this).get(CreateSiteViewModel.class);
        mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        superUserViewModel = ViewModelProviders.of(this).get(SuperUserViewModel.class);
        editSiteViewModel = ViewModelProviders.of(this).get(EditSiteViewModel.class);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        participatingSitesViewModel = ViewModelProviders.of(this).get(ParticipatingSitesViewModel.class);
        participantsListViewModel = ViewModelProviders.of(this).get(ParticipantsListViewModel.class);
        siteDetailViewModel = ViewModelProviders.of(this).get(SiteDetailViewModel.class);
    }


    /**
     * Send current user data through child fragments' view models
     */
    private void setAllChildFragmentsViewModelData() {
        createSiteViewModel.setData(currentUserObject, currentUserDocId);
        mapsViewModel.setData(currentUserObject, currentUserDocId);
        superUserViewModel.setData(currentUserObject, currentUserDocId);
        editSiteViewModel.setData(currentUserObject, currentUserDocId);
        homeViewModel.setData(currentUserObject, currentUserDocId);
        participatingSitesViewModel.setData(currentUserObject, currentUserDocId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel(); //Create notification channel
        navigationDrawerSetup(); //setup navigation drawer
        initFirebaseCurrentUserInfo(); //Get all fireStore instances
        linkViewElements(); //Get view elements
        initAllChildFragmentsViewModel(); //Init all child fragments viewModels
        handleNotificationIntentIfExists(); //Handle notification onClick
    }

    /**
     * Handle notification onClick
     */
    private void handleNotificationIntentIfExists() {
        //Check if there is an Intent fired by clicking on the notification
        Intent i = getIntent();
        String siteId = (String) i.getExtras().getString("editedSiteId");
        i.removeExtra("editedSiteId");
        //If yes, handle by getting data of that site and move to SiteDetailFragment
        if (siteId != null) {
            db.collection(Constants.FSSite.siteCollection)
                    .document(siteId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Site site = task.getResult().toObject(Site.class);
                            siteDetailViewModel.setData(site);
                            moveToSiteDetail();
                        }
                    });
        }
    }

    /**
     * Move to SiteDetailFragment
     */
    private void moveToSiteDetail() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.site_detail);
    }

    /**
     * Set event listener when this current user's participating site changes
     */
    private void setParticipatingDocSnapShotListener() {
        for (String participatingSiteId : currentUserObject.getParticipatingSitesId()) {
            db.collection(Constants.FSSite.siteCollection)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                return;
                            }

                            Site site;

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        break;
                                    case MODIFIED: //Only handle "Update" changes
                                        site = dc.getDocument().toObject(Site.class);
                                        String docId = site.getDocId();
                                        if (currentUserObject.getParticipatingSitesId().contains(docId)){
                                            pushNotification(docId); //Push notification
                                        }
                                        break;
                                    case REMOVED:
                                        break;
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Create Intent to go to SiteDetail when the notification is clicked
     * @param siteId
     * @return PendingIntent
     */
    private PendingIntent onClickNotificationListener(String siteId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("REFRESH" + System.currentTimeMillis()); //Generate Unique action to prevent PendingIntent caching Extras Bundle
        intent.putExtra("editedSiteId", siteId); //Send SiteId
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * Create notification builder
     * @param siteId
     * @return NotificationCompat.Builder
     */
    private NotificationCompat.Builder createNotificationBuilder(String siteId) {
        return new NotificationCompat.Builder(this, Constants.Notification.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)   //Icon
                .setContentTitle(Constants.Notification.title) //Title
                .setContentText(Constants.Notification.onSiteChangeTextContent) //Text content
                .setStyle(new NotificationCompat.BigTextStyle() //BigText
                        .bigText(Constants.Notification.onSiteChangeTextContent))
                .setContentIntent(onClickNotificationListener(siteId)) //Set contentIntent
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Priority
                .setAutoCancel(true); //Auto removed when clicked
    }

    /**
     * Push notification
     * @param siteId
     */
    private void pushNotification(String siteId) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(100, createNotificationBuilder(siteId).build());
    }

    /**
     * Create notification channel settings
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.Notification.CHANNEL_NAME;
            String description = Constants.Notification.CHANNEL_DES;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.Notification.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Get current user object from FireStore
     */
    private void getCurrentUserObject() {
        db.collection(Constants.FSUser.userCollection)
                .whereEqualTo(Constants.FSUser.emailField, currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            currentUserObject = doc.toObject(User.class);
                            currentUserDocId = doc.getId();
                            setNavHeaderEmailAndUsername(); //Set nav header username and email
                            setAllChildFragmentsViewModelData();
                            if (!currentUserObject.getSuperuser()) {
                                hideAdminMenuItem();
                            }
                            setParticipatingDocSnapShotListener();
                        }
                    }
                });
    }

    /**
     * Hide admin menu item if this user is not superuser
     */
    private void hideAdminMenuItem() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem adminMenuItem = menu.getItem(2);
        adminMenuItem.setVisible(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Logout menu item listener (sits in 3-dots collapsing menu)
     */
    private void onLogoutOptionClick() {
        mAuth.signOut();
        Intent i = new Intent(MainActivity.this, StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                onLogoutOptionClick();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}