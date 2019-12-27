package com.example.projet_balat_monge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifierPermissionSms();
        verifierPermissionInternet();
        verifierPermissionReadContact();
        verifierPermissionAccessFineLocation();
    }

    public void sendSms(View view) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        System.out.println("Bjr");
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                System.out.println("Bjr");
                if (location != null) {
                    System.out.println("Bjr");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
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
                        Toast.makeText(context, "Message envoyé", duration).show();
                    }
                    catch (Exception e){
                        ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                        Toast.makeText(context, "Échec de l'envoi", duration).show();
                    }
                }
            }
        });

    }

    public void choisirContact(View view){
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        //pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
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
}
