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


import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.deck.DeleteDeckCardInPlayerDeck;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.InsertOneCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.UpdateDeckCardAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class ExchangeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback
{
    private static final String CURRENT_ID_CARD = "CURRENT_ID_CARD";
    private static final String TEXT_PLAIN_MIME = "text/plain";
    private String idGivenCard;
    private NdefMessage operationMessage;
    private NfcAdapter nfcAdapter;
    private TextView idView;
    private PendingIntent pendingIntent;
    private ImageView cardView;
    private YugiohDeckDAO yugiohDeckDAO;
    private YugiohDeckCard cardInDeck;
    private int amountOwned;
    private boolean typeOfExchange;

    public static void startTrade(Context context, String cardId, Boolean typeOfExchange) {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, cardId);
        intent.putExtra(ConstantsUtil.EXTRA_TYPE_OF_EXCHANGE, typeOfExchange);

        context.startActivity(intent);
    }

    public static void startReceive(Context context, Boolean typeOfExchange) {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_TYPE_OF_EXCHANGE, typeOfExchange);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        if (savedInstanceState != null)
        {
            idGivenCard = savedInstanceState.getString(CURRENT_ID_CARD);
        }
        else
        {
            typeOfExchange = getIntent().getBooleanExtra(ConstantsUtil.EXTRA_TYPE_OF_EXCHANGE,true);
            idGivenCard = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_ID);
        }

        YugiohDatabase yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        idView = findViewById(R.id.textView);
        cardView = findViewById(R.id.cardView);
        View rootView = findViewById(R.id.rootViewExchange);

        setVariables();

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

    private void setVariables()
    {
        amountOwned = ConstantsUtil.NUMBER_OF_CARDS_TO_ADD;

        if (typeOfExchange == true)
        {
            idView.setText(R.string.text_beam_trade);
        }
        else
        {
            idView.setText(R.string.text_beam_receive);
        }
        if  (idGivenCard != null)
        {
            cardInDeck = new YugiohDeckCard(ConstantsUtil.PLAYER_ID, Integer.valueOf(idGivenCard), amountOwned);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_ID_CARD, idGivenCard);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            try
            {
                processIntentData(getIntent());

            }catch (NumberFormatException e)
            {
                Toast.makeText(this, R.string.error_trade_conflict, Toast.LENGTH_LONG).show();
                finish();
            }
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
        if (typeOfExchange == true)

        {
            idView.setText(R.string.text_beam_trade);
            operationMessage = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( TEXT_PLAIN_MIME, idGivenCard.getBytes())});
            deleteCardFromDeck(idGivenCard);
        }
        return operationMessage;
    }

    void processIntentData(Intent intent)
    {
        Parcelable[] rawOperationMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        operationMessage = (NdefMessage) rawOperationMessages[0];
        idGivenCard = new String(operationMessage.getRecords()[0].getPayload());


        //ici
        
        cardInDeck = new YugiohDeckCard(ConstantsUtil.PLAYER_ID, Integer.valueOf(idGivenCard), ConstantsUtil.NUMBER_OF_CARDS_TO_ADD);
        cardView.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(idGivenCard)));
        cardView.setOnClickListener(this::onClickedCardView);
    }

    private void activateNFC(View view)
    {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
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
    }

    private void onFetchingDeckCard() { }

    private void onCardFetchedDeckCard(YugiohDeckCard yugiohDeckCard)
    {
        if (yugiohDeckCard.amountOwned >1)
        {
            yugiohDeckCard.amountOwned--;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, this::onUpdating, this::onUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(yugiohDeckCard);
        }
        else
        {
            DeleteDeckCardInPlayerDeck deleteDeckCardInPlayerDeck = new DeleteDeckCardInPlayerDeck(yugiohDeckDAO, this::onDeleting, this::onDeleted, this::onDatabaseError);
            deleteDeckCardInPlayerDeck.execute(yugiohDeckCard);
        }
    }

    private void onDeleting() { }

    private void onDeleted()
    {
        Toast.makeText(this,R.string.card_deleted_from_deck_text, Toast.LENGTH_LONG).show();
        finish();
    }

    private void onLoading() { }

    private void onCardFetched(YugiohDeckCard yugiohDeckCard)
    {
        if (yugiohDeckCard != null)
        {
            cardInDeck.amountOwned = cardInDeck.amountOwned + yugiohDeckCard.amountOwned;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, this::onUpdating, this::onUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
        InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, this::onInsertingCard, this::onInsertedCard, this::onDatabaseError);
        insertOneCardInDeckAsyncTask.execute(cardInDeck);
    }

    private void onUpdating() { }

    private void onUpdated()
    {
        Toast.makeText(this, getString(R.string.deck_modified_text), Toast.LENGTH_LONG).show();
        finish();
    }

    private void onInsertingCard() { }

    private void onInsertedCard(Long aLong)
    {
        Toast.makeText(this,R.string.card_added_in_deck_text, Toast.LENGTH_LONG).show();
        finish();
    }

    private void onDatabaseError() { }
}
