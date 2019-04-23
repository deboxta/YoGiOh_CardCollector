package ca.csf.mobile1.yogioh.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
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
    private TextView idShower;
    private Button buttonBeam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);

        idShower = findViewById(R.id.textView);
        buttonBeam = findViewById(R.id.button);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Toast.makeText(this, "NFC not working properly", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        nfcAdapter.setNdefPushMessageCallback(this,this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        idGivenCard = "15";
        msg = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( "application/com.example.android.beam", idGivenCard.getBytes())});

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
        super.onNewIntent(intent);
    }

    void processIntentData(Intent intent){
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        msg = (NdefMessage) rawMsgs[0];
        idShower.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
