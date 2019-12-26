package com.example.projet_balat_monge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifierPermission();
    }

    public void sendSms(View view) {
        EditText numeroText = (EditText) findViewById(R.id.editTextNumero);
        String message = "toto";
        String numero = numeroText.getText().toString();

        String[] parts = numero.split(";");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        if (!message.isEmpty()) {
            try {
                SmsManager smgr = SmsManager.getDefault();
                for (String numeroTel : parts) {
                    smgr.sendTextMessage(numeroTel, null, message, null, null);
                }
                Toast.makeText(context, "Message envoyé", duration).show();
            }
            catch (Exception e){
                int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
                Toast.makeText(context, "Échec de l'envoi", duration).show();
            }
        }
        else {
            Toast.makeText(context, "Erreur : numéro incorrect ou message vide", duration).show();
        }
    }

    public void confirmationRDV(View view) {
        Intent intent = new Intent(this, ConfirmationRDV.class);
        String numero = "5556";
        intent.putExtra("numPersDemandeRDV", numero);
        startActivity(intent);

    }

    private void verifierPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // Ne rien faire
            }

            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
    }
}
