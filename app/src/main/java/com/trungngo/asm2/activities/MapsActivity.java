package com.trungngo.asm2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trungngo.asm2.R;
import com.trungngo.asm2.ui.find_sites.MapsFragment;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MapsFragment.newInstance())
                    .commitNow();
        }

    }
}