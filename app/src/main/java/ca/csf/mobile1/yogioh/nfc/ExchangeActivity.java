package ca.csf.mobile1.yogioh.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ca.csf.mobile1.yogioh.R;


public class ExchangeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private String idGivenCard;
    private NdefMessage operationMessage;
    private NfcAdapter nfcAdapter;
    private TextView idView;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        
        idGivenCard = getIntent().getStringExtra("EXTRA_ID");

        idView = findViewById(R.id.textView);
        View rootView = findViewById(R.id.rootView);
        ImageView cardView = findViewById(R.id.cardView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Toast.makeText(this, R.string.ERROR_TEXT_NFC, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Snackbar.make(rootView, R.string.ERROR_TEXT_NFC_NOT_ACTIVATED, Snackbar.LENGTH_INDEFINITE).setAction(R.string.SNACK_NFC_ACTIVATION_TEXT, this::activateNFC).show();
        }

        nfcAdapter.setNdefPushMessageCallback(this,this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private void activateNFC(View view) {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        operationMessage = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( "text/plain", idGivenCard.getBytes())});

        return operationMessage;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processIntentData(getIntent());
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntentData(Intent intent){
        Parcelable[] rawOperationMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        operationMessage = (NdefMessage) rawOperationMessages[0];
        idView.setText(new String(operationMessage.getRecords()[0].getPayload()));

    }
}
