package com.example.projet_balat_monge;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    static final int CHOISIR_LOCALISATION = 1;
    private Double latitude;
    private Double longitude;
    private Intent intentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.intentMain = new Intent(this, MainActivity.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Place la carte sur la dernière position sélectionnée (position actuelle par défaut) lorsque la carte est chargée
     * Au clic sur la carte, la localisation est mise à jour
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(position).title("Vous êtes ici"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                intentMain.putExtra("point", point);
                setResult(CHOISIR_LOCALISATION, intentMain);
                finish();
            }
        });
    }
}
