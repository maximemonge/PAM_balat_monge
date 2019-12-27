package com.example.projet_balat_monge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    static final int PICK_CONTACT_REQUEST = 1;
    private Double latitude;
    private Double longitude;
    private Intent intentMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.intentMaps = new Intent(this, MapsActivity.class);
        setContentView(R.layout.activity_main);
        initialiserLocalisation();
        latitude = 0.0;
        longitude = 0.0;
        textViewLatitude = (TextView) findViewById(R.id.textViewLat);
        textViewLongitude = (TextView) findViewById(R.id.textViewLong);
        verifierPermissionSms();
        verifierPermissionInternet();
        verifierPermissionReadContact();
        verifierPermissionAccessFineLocation();
    }

    public void sendSms(View view) {
        EditText numeroText = (EditText) findViewById(R.id.editTextNumero);
        String contenuMessage = getResources().getString(R.string.contenusms);
        String locationChoisie = "https://www.google.com/maps/place/" + latitude.toString() + "," + longitude.toString();
        String message = contenuMessage + locationChoisie;
        String numero = numeroText.getText().toString();

        String[] parts = numero.split(";");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();
            for (String numeroTel : parts) {
                smgr.sendTextMessage(numeroTel, null, message, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void choisirContact(View view){
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        System.out.println("Je suis ici");
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        System.out.println("Je suis ici");
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
        System.out.println("Je suis ici");
    }

    public void confirmationRDV(View view) {
        Intent intent = new Intent(this, ConfirmationRDV.class);
        String numero = "5556";
        intent.putExtra("numPersDemandeRDV", numero);
        startActivity(intent);

    }

    private void verifierPermissionSms() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // Ne rien faire
            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
    }

    public void  verifierPermissionReadContact(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Ne rien faire
            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        }
    }

    private void verifierPermissionInternet() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                // Ne rien faire
            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
    }

    private void verifierPermissionAccessFineLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private void initialiserLocalisation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    editerLatLong();
                }
            }
        });
    }

    public void ouvrirMap(View view) {
        intentMaps.putExtra("latitude", latitude);
        intentMaps.putExtra("longitude", longitude);
        startActivityForResult(intentMaps, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1 && requestCode == 1) {
            if (data.hasExtra("point")) {
                LatLng point = (LatLng) data.getExtras().get("point");
                latitude = point.latitude;
                longitude = point.longitude;
                editerLatLong();
            }
        }
    }

    private void editerLatLong() {
        textViewLatitude.setText(String.valueOf(latitude));
        textViewLongitude.setText(String.valueOf(longitude));
    }
}