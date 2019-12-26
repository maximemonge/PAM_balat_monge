package com.example.projet_balat_monge;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ConfirmationRDV extends AppCompatActivity {

    String numero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirmation_rdv);

        /** On recupère le numéro de la personne qui nous a envoyer le rendez-vous dans l'intent*/
        numero =  getIntent().getStringExtra("numPersDemandeRDV");

    }



    public void confirmationOui(View view){

        TextView choix = (TextView) findViewById(R.id.choixAcceptationRDV);
        choix.setText("Vous avez accepté le rendez-vous!");

        Button oui = (Button) findViewById(R.id.acceptationOui);
        Button non = (Button) findViewById(R.id.acceptationNon);

        oui.setEnabled(false);
        non.setEnabled(false);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();

                smgr.sendTextMessage(numero, null, "Je serais présent au rendez-vous!", null, null);

            Toast.makeText(context, "Message envoyé", duration).show();
        }
        catch (Exception e){
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            Toast.makeText(context, "Échec de l'envoi", duration).show();
        }


    }

    public void confirmationNon (View view){

        TextView choix = (TextView) findViewById(R.id.choixAcceptationRDV);
        choix.setText("Vous avez refusé le rendez-vous!");

        Button oui = (Button) findViewById(R.id.acceptationOui);
        Button non = (Button) findViewById(R.id.acceptationNon);

        oui.setEnabled(false);
        non.setEnabled(false);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();

            smgr.sendTextMessage(numero, null, "Je ne serais pas présent au rendez-vous!", null, null);

            Toast.makeText(context, "Message envoyé", duration).show();
        }
        catch (Exception e){
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            Toast.makeText(context, "Échec de l'envoi", duration).show();
        }
    }
}
