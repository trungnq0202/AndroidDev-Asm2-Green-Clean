package com.trungngo.asm2.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.create_site.CreateSiteViewModel;
import com.trungngo.asm2.ui.find_sites.MapsViewModel;

import androidx.annotation.NonNull;
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

    private AppBarConfiguration mAppBarConfiguration;

    //Set up navigation drawer activity
    private void navigationDrawerSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_maps, R.id.create_site) //////// Pay attention
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    //Connect view elements of layout to this class variable
    private void linkViewElements(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout navHeaderView = (LinearLayout) navigationView.getHeaderView(0);
        navHeaderUsernameTextView = (TextView) navHeaderView.getChildAt(1);
        navHeaderEmailTextView = (TextView) navHeaderView.getChildAt(2);
    }

    //Set nav header username and email
    private void setNavHeaderEmailAndUsername(){
        navHeaderEmailTextView.setText(currentUser.getEmail());
        navHeaderUsernameTextView.setText(currentUserObject.getUsername());
    }

    //Get instances of Firebase FireStore Auth, db, current user
    private void initFirebaseCurrentUserInfo(){
        //Get instances of Firebase FireStore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        getCurrentUserObject(); //Get current user object info
    }

    //Init all child fragments' view models
    private void initAllChildFragmentsViewModel(){
        createSiteViewModel = ViewModelProviders.of(this).get(CreateSiteViewModel.class);
        mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
    }

    //Send current user data through child fragments' view models
    private void setAllChildFragmentsViewModelData(){
        createSiteViewModel.setData(currentUserObject, currentUserDocId);
        mapsViewModel.setData(currentUserObject, currentUserDocId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationDrawerSetup();
        linkViewElements();
        initFirebaseCurrentUserInfo();
        initAllChildFragmentsViewModel();
    }

    //Get current user object from FireStore
    private void getCurrentUserObject() {
        db.collection(Constants.FSUser.userCollection)
                .whereEqualTo(Constants.FSUser.emailField, currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                currentUserObject = document.toObject(User.class);
                                currentUserDocId = document.getId();
                                setNavHeaderEmailAndUsername(); //Set nav header username and email
                                setAllChildFragmentsViewModelData();
                            }
                        } else {
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}