package ca.csf.mobile1.yogioh.activity;

import android.app.PendingIntent;
import android.content.Context;
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
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.DeleteDeckCardInPlayerDeck;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.InsertOneCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.UpdateDeckCardAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.ConvertUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class ExchangeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback
{

    private String idGivenCard;
    private NdefMessage operationMessage;
    private NfcAdapter nfcAdapter;
    private TextView idView;
    private PendingIntent pendingIntent;
    private ImageView cardView;
    private YugiohDeckDAO yugiohDeckDAO;
    private YugiohDeckCard cardInDeck;

    public static void start(Context context, String cardId) {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, cardId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        idGivenCard = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_ID);

        cardInDeck = new YugiohDeckCard(ConstantsUtil.PLAYER_ID, Integer.valueOf(idGivenCard), 1);

        YugiohDatabase yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        idView = findViewById(R.id.textView);
        cardView = findViewById(R.id.cardView);
        View rootView = findViewById(R.id.rootView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Toast.makeText(this, R.string.error_text_nfc, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled())
        {
            Snackbar.make(rootView, R.string.error_text_nfc_not_activated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.snack_nfc_activation_text, this::activateNFC).show();
        }

        nfcAdapter.setNdefPushMessageCallback(this,this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            processIntentData(getIntent());
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        operationMessage = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( "text/plain", idGivenCard.getBytes())});
        deleteCardFromDeck(idGivenCard);
        return operationMessage;
    }

    private void activateNFC(View view)
    {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    void processIntentData(Intent intent)
    {
        Parcelable[] rawOperationMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        operationMessage = (NdefMessage) rawOperationMessages[0];

        idView.setText(new String(operationMessage.getRecords()[0].getPayload()));

        cardView.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(idGivenCard)));
        cardView.setOnClickListener(this::onClickedCardView);
    }

    private void deleteCardFromDeck(String idGivenCard)
    {
        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, this::onFetchingDeckCard, this::onCardFetchedDeckCard, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.valueOf(idGivenCard), ConstantsUtil.PLAYER_ID);
    }

    private void onClickedCardView(View view)
    {
        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.valueOf(idGivenCard), ConstantsUtil.PLAYER_ID);

        InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, this::onInsertingCard, this::onInsertedCard, this::onDatabaseError);
        insertOneCardInDeckAsyncTask.execute(cardInDeck);
    }

    private void onCardFetchedDeckCard(YugiohDeckCard yugiohDeckCard)
    {
        DeleteDeckCardInPlayerDeck deleteDeckCardInPlayerDeck = new DeleteDeckCardInPlayerDeck(yugiohDeckDAO, this::onDeleting, this::onDeleted, this::onDatabaseError);
        deleteDeckCardInPlayerDeck.execute(yugiohDeckCard);
    }

    private void onDeleting()
    {

    }

    private void onDeleted()
    {
        Toast.makeText(this, "Carte supprimée de votre deck", Toast.LENGTH_LONG).show();
        finish();
    }


    private void onFetchingDeckCard()
    {

    }

    private void onCardFetched(YugiohDeckCard yugiohDeckCard)
    {
        if (yugiohDeckCard != null)
        {
            cardInDeck.amountOwned++;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, this::onUpdating, this::onUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
    }

    private void onUpdating()
    {

    }

    private void onUpdated()
    {
        Toast.makeText(this, "Carte ajoutée à votre deck en double", Toast.LENGTH_LONG).show();
        finish();
    }

    private void onLoading()
    {

    }

    private void onInsertingCard()
    {

    }

    private void onInsertedCard(Long aLong)
    {
        Toast.makeText(this, "Carte ajoutée à votre deck", Toast.LENGTH_LONG).show();
        finish();
    }

    private void onDatabaseError()
    {

    }
}
