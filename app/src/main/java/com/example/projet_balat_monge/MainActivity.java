package com.example.projet_balat_monge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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
    static final int PICK_CONTACT_REQUEST = 2;
    private Double latitude;
    private Double longitude;
    private Intent intentMaps;
    private Intent intentConfirm;
    private String mPhoneNumber;

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
        verifierPermissionServiceTelephone();
        String url = "http://projet_balat_monge.com/confirmerdv/" + mPhoneNumber;
        intentConfirm = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public void sendSms(View view) {
        EditText numeroText = (EditText) findViewById(R.id.editTextNumero);
        String contenuMessage = getResources().getString(R.string.contenusms);
        String locationChoisie = "https://www.google.com/maps/place/" + latitude.toString() + "," + longitude.toString();
        String confirmation = " Confirmez ici " + intentConfirm.getData();
        String message = contenuMessage + locationChoisie + confirmation;
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Échec de l'envoi", duration).show();
        }
    }


    public void choisirContact(View view){
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Permet de lister le nom des contacts
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
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

    private void verifierPermissionServiceTelephone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                mPhoneNumber = tMgr.getLine1Number();
            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
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
        switch(requestCode) {
            case 1:
                if (resultCode == 1) {
                    if (data.hasExtra("point")) {
                        LatLng point = (LatLng) data.getExtras().get("point");
                        latitude = point.latitude;
                        longitude = point.longitude;
                        editerLatLong();
                    }
                }
            case 2:
                System.out.println("retour");
                if (resultCode == RESULT_OK){
                    System.out.println("Je vais faire le traitement");
                    // On récupère le lien qui pointe vers le contact que nous avons choisit
                    Uri contactChoisitUri = data.getData();
                    //On récupère les numérors des contacts
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                    // On cherche le numéro du contact que l'on a choisit
                    Cursor cursor = getContentResolver().query(contactChoisitUri, projection, null, null, null);
                    cursor.moveToFirst();

                    // On recupère le numéro
                    int recupNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String numero = cursor.getString(recupNumero);

                    // On insere le numéro dans l'EditText
                    EditText editNum = (EditText) findViewById(R.id.editTextNumero);
                    System.out.println("Edit text : " + editNum.getText());
                    if (editNum.getText().length() < 1 ) {
                        editNum.setText(numero);
                    }
                    else {
                        editNum.setText(editNum.getText() + ";" + numero);
                    }
                }
        }


    }

    private void editerLatLong() {
        textViewLatitude.setText(String.valueOf(latitude));
        textViewLongitude.setText(String.valueOf(longitude));
    }
}