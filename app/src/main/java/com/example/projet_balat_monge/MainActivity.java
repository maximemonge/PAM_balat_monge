package com.example.projet_balat_monge;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    static final int CHOISIR_LOCALISATION = 1;
    static final int CHOISIR_CONTACT = 2;
    private Double latitude;
    private Double longitude;
    private Intent intentMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.intentMaps = new Intent(this, MapsActivity.class);
        setContentView(R.layout.activity_main);
        latitude = 0.0;
        longitude = 0.0;
        textViewLatitude = (TextView) findViewById(R.id.textViewLat);
        textViewLongitude = (TextView) findViewById(R.id.textViewLong);
        verifierPermissions();
    }

    @Override
    public void onStart(){
        super.onStart();
        initialiserLocalisation();
    }

    /**
     * Permet d'envoyer le SMS contenant le rendez_vous géolocalisé
     *
     * On récupère d'abord le numéro de téléphone de la personne qui envoie le rendez_vous
     * On créé le lien permettant au destinataire de confirmer ou refuser le rendez_vous
     * On prépare le contenu du message
     * On envoie le message pour chaque destinataire
     * On affiche un toast pour dire si le message a été envoyé ou non
     */
    public void sendSms(View view) {
        EditText monNumero = (EditText) findViewById(R.id.editTextMonNumero);
        String mPhoneNumber = monNumero.getText().toString();

        String url = "http://projet_balat_monge.com/confirmerdv/" + mPhoneNumber;
        Intent intentConfirm = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        TextView mydate = (TextView) findViewById(R.id.textViewDate);
        String date = mydate.getText().toString();

        TextView hour = (TextView) findViewById(R.id.textViewHeure);
        String heure = hour.getText().toString();

        EditText numeroText = (EditText) findViewById(R.id.editTextNumero);
        String contenuMessage = getResources().getString(R.string.contenusms);
        String locationChoisie = "https://www.google.com/maps/place/" + latitude.toString() + "," + longitude.toString();
        String messageUn = contenuMessage + locationChoisie;
        String messageDeux =  getResources().getString(R.string.label_message_confirmer) + " " + date + " à " + heure + " :" + intentConfirm.getData();
        String numero = numeroText.getText().toString();
        String[] parts = numero.split(";");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();
            for (String numeroTel : parts) {
                smgr.sendTextMessage(numeroTel, null, messageUn, null, null);
                smgr.sendTextMessage(numeroTel, null, messageDeux, null, null);
            }
            Toast.makeText(context, getResources().getString(R.string.label_message_envoye), duration).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.label_erreur_envoi_sms), duration).show();
        }
    }

    /**
     * Permet de choisir un contact dans la liste des contacts présent dans l'application de contact de l'utilisateur
    */
    public void choisirContact(View view){
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, CHOISIR_CONTACT);
    }

    /**
     * Permet de choisir la date du rendez-vous
     *
     * On incremente le mois car il commence à 0 (0 = Janvier or nous voulons 1=Janvier)
     */
    public void choisirDate(View view){
        System.out.println("Je peux choisir le date");

        final TextView date =(TextView) findViewById(R.id.textViewDate);
        date.setInputType(InputType.TYPE_NULL);

        final Calendar calendar = Calendar.getInstance();
        int jour = calendar.get(Calendar.DAY_OF_MONTH);
        int mois = calendar.get(Calendar.MONTH);
        int année = calendar.get(Calendar.YEAR);
        // time picker dialog
        DatePickerDialog picker = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker tp, int sAnnee, int sMois, int sJour) {
                        sMois = sMois +1 ;
                        date.setText(sJour + "/" + sMois + "/" + sAnnee);
                    }
                }, année, mois, jour);
        picker.show();

    }

    /**
     * Permet de choisir l'heure du rendez-vous
     */
    public void choisirHeure(View view){
        System.out.println("Je peux choisir le date");

        final TextView heure =(TextView) findViewById(R.id.textViewHeure);
        heure.setInputType(InputType.TYPE_NULL);

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        heure.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes, true);
        picker.show();

    }

    /*
    Permet de vérifier la permission d'envoi de SMS
    */
    private void verifierPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    /*
    Permet d'initialiser la localisation de l'utilisateur sur sa position actuelle
    */
    private void initialiserLocalisation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

    /**
     * Permet d'ouvrir la carte GoogleMaps positionnées sur les dernières coordonnées sélectionnées (actuelle par défaut)
    */
    public void ouvrirMap(View view) {
        intentMaps.putExtra("latitude", latitude);
        intentMaps.putExtra("longitude", longitude);
        startActivityForResult(intentMaps, CHOISIR_LOCALISATION);
    }

    /**
     * Récupère des informations à la fermeture d'une activité
     * Le cas où le resultCode est 1 permet de récupérer les coordonnées (longitude, latitude) choisies sur la carte
     *
     * Le cas où le resultCode est 2 permet de récupérer un contact qu'on a choisi dans l'application de contact
     * On récupère le lien qui pointe vers le contact choisi
     * On récupère les numéros de tous les contacts
     * On cherche ensuite le numéro du contact qu'on a choisi
     * On récupère le numéro et on l'insère dans la liste des destinataires
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CHOISIR_LOCALISATION) {
            if (data.hasExtra("point")) {
                LatLng point = (LatLng) data.getExtras().get("point");
                latitude = point.latitude;
                longitude = point.longitude;
                editerLatLong();
            }
        }
        if (resultCode == RESULT_OK){
            Uri contactChoisiUri = data.getData();

            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor cursor = getContentResolver().query(contactChoisiUri, projection, null, null, null);
            cursor.moveToFirst();

            int recupNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String numero = cursor.getString(recupNumero);

            EditText editNum = (EditText) findViewById(R.id.editTextNumero);
            if (editNum.getText().length() < 1) {
                editNum.setText(numero);
            }
            else {
                String ajoutNouveauNumero = editNum.getText() + ";" + numero;
                editNum.setText(ajoutNouveauNumero);
            }
        }
    }

    /*
    Permet de mettre à jour la latitude et la longitude
    */
    private void editerLatLong() {
        textViewLatitude.setText(String.valueOf(latitude));
        textViewLongitude.setText(String.valueOf(longitude));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            initialiserLocalisation();
    }
}