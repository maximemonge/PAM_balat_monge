package com.example.projet_balat_monge;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
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
        Uri url = getIntent().getData();
        numero = String.valueOf(url).replace("http://projet_balat_monge.com/confirmerdv/", "");
    }

    /**
     * Permet de confirmer le rendez-vous proposé par notre interlocuteur
     * Bloque les boutons une fois qu'un des deux a été pressé
     * Envoie un sms en fonction du bouton choisi
     * Un toaster nous confirme si le message a été envoyé ou non
    */
    public void confirmationOui(View view){

        TextView choix = (TextView) findViewById(R.id.choixAcceptationRDV);
        choix.setText(getResources().getString(R.string.rdv_accepte));

        Button oui = (Button) findViewById(R.id.acceptationOui);
        Button non = (Button) findViewById(R.id.acceptationNon);

        oui.setEnabled(false);
        non.setEnabled(false);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(numero, null, getResources().getString(R.string.sms_rdv_accepte), null, null);

            Toast.makeText(context, getResources().getString(R.string.label_message_envoye), duration).show();
        }
        catch (Exception e){
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            Toast.makeText(context, getResources().getString(R.string.label_erreur_envoi_sms), duration).show();
        }
    }


    /**
     * Permet de refuser le rendez-vous proposé par notre interlocuteur
     * Bloque les boutons une fois qu'un des deux a été pressé
     * Envoie un sms en fonction du bouton choisi
     * Un toaster nous confirme si le message a été envoyé ou non
    */
    public void confirmationNon (View view){

        TextView choix = (TextView) findViewById(R.id.choixAcceptationRDV);
        choix.setText(getResources().getString(R.string.rdv_refuse));

        Button oui = (Button) findViewById(R.id.acceptationOui);
        Button non = (Button) findViewById(R.id.acceptationNon);

        oui.setEnabled(false);
        non.setEnabled(false);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        try {
            SmsManager smgr = SmsManager.getDefault();

            smgr.sendTextMessage(numero, null, getResources().getString(R.string.sms_rdv_refuse), null, null);

            Toast.makeText(context, getResources().getString(R.string.label_message_envoye), duration).show();
        }
        catch (Exception e){
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            Toast.makeText(context, getResources().getString(R.string.label_erreur_envoi_sms), duration).show();
        }
    }
}
