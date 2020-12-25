package com.trungngo.asm2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trungngo.asm2.ui.maps.MapsFragment;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MapsFragment.newInstance())
                    .commitNow();
        }

    }
}