package ca.csf.mobile1.yogioh.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ca.csf.mobile1.yogioh.R;

public class BeamActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private String idGivenCard;
    private NdefMessage msg;
    private NfcAdapter nfcAdapter;
    private TextView idView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);

        idGivenCard = getIntent().getStringExtra("EXTRA_ID");

        idView = findViewById(R.id.textView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Toast.makeText(this, "NFC not working properly", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Enable NFC via settings", Toast.LENGTH_LONG).show();
        }

        nfcAdapter.setNdefPushMessageCallback(this,this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        msg = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( "text/plain", idGivenCard.getBytes())});

        return msg;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processIntentData(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntentData(Intent intent){
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        msg = (NdefMessage) rawMsgs[0];
        idView.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
