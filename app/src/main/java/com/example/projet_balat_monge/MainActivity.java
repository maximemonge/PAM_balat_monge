package com.example.projet_balat_monge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifierPermission();
    }

    public void sendSms(View view) {
        EditText numeroText = (EditText) findViewById(R.id.editTextNumero);
        EditText messageText = (EditText) findViewById(R.id.editTextMessage);
        String message = messageText.getText().toString();
        String numero = numeroText.getText().toString();

        String[] parts = numero.split(";");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        if (!message.isEmpty() && contientQuatreChiffresMinimum(numero)) {
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

    private boolean contientQuatreChiffresMinimum(String num) {
        int compteur = 0;
        for (int index = 0; index < num.length(); index++) {
            if (Character.isDigit(num.charAt(index))) {
                compteur ++;
            }
            else {
                return false;
            }
        }
        if (compteur >= 4) {
            return true;
        }
        return false;
    }
}
