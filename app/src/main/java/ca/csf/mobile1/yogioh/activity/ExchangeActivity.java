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
import ca.csf.mobile1.yogioh.util.SnackBarUtil;

public class ExchangeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback
{
    private static final String CURRENT_ID_CARD = "CURRENT_ID_CARD";
    private static final String TEXT_PLAIN_MIME = "text/plain";

    private NdefMessage operationMessage;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private String idGivenCard;
    private TextView idView;
    private ImageView cardView;
    private View rootView;

    private YugiohDeckDAO yugiohDeckDAO;
    private YugiohDeckCard cardInDeck;

    private boolean typeOfExchange;

    public static void startTrade(Context context, String cardId, Boolean typeOfExchange)
    {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, cardId);
        intent.putExtra(ConstantsUtil.EXTRA_TYPE_OF_EXCHANGE, typeOfExchange);

        context.startActivity(intent);
    }

    public static void startReceive(Context context, Boolean typeOfExchange)
    {
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

        rootView = createView();

        setVariables();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            showToastMessage(R.string.error_text_nfc);
            return;
        }

        if (!nfcAdapter.isEnabled())
        {
            Snackbar.make(rootView, R.string.error_text_nfc_not_activated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.snack_nfc_activation_text, this::activateNFC).show();
        }
        if (idGivenCard != null)
        {
            nfcAdapter.setNdefPushMessage(new NdefMessage(new NdefRecord[] { NdefRecord.createMime( TEXT_PLAIN_MIME, idGivenCard.getBytes())}), this);
        }
        nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private View createView()
    {
        idView = findViewById(R.id.textViewNfc);
        cardView = findViewById(R.id.cardView);
        return findViewById(R.id.rootViewExchange);
    }

    private void setVariables()
    {
        if (typeOfExchange)
        {
            idView.setText(R.string.text_beam_trade);
        }
        else
        {
            idView.setText(R.string.text_beam_receive);
        }
        if  (idGivenCard != null)
        {
            cardInDeck = new YugiohDeckCard(ConstantsUtil.PLAYER_ID, Integer.valueOf(idGivenCard),ConstantsUtil.NUMBER_OF_CARDS_TO_ADD);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_ID_CARD, idGivenCard);
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
        if (typeOfExchange)
        {
            operationMessage = new NdefMessage(new NdefRecord[] { NdefRecord.createMime( TEXT_PLAIN_MIME, idGivenCard.getBytes())});
        }
        return operationMessage;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event)
    {
        deleteCardFromDeck(idGivenCard);
    }

    void processIntentData(Intent intent)
    {
            Parcelable[] rawOperationMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            operationMessage = (NdefMessage) rawOperationMessages[0];
            idGivenCard = new String(operationMessage.getRecords()[0].getPayload());
            cardInDeck = new YugiohDeckCard(ConstantsUtil.PLAYER_ID, Integer.valueOf(idGivenCard), ConstantsUtil.NUMBER_OF_CARDS_TO_ADD);
            cardView.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(idGivenCard)));
            cardView.setOnClickListener(this::onClickedCardView);
    }


    private void activateNFC(View view)
    {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    private void showToastMessage(int message)
    {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
        finish();
    }


    private void onClickedCardView(View view)
    {
        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onCardFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.valueOf(idGivenCard), ConstantsUtil.PLAYER_ID);
    }

    private void onCardFetched(YugiohDeckCard yugiohDeckCard)
    {
        if (yugiohDeckCard != null)
        {
            cardInDeck.amountOwned = cardInDeck.amountOwned + yugiohDeckCard.amountOwned;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, ()->{}, this::onUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
        InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onInsertedCard, this::onDatabaseError);
        insertOneCardInDeckAsyncTask.execute(cardInDeck);
    }

    private void onInsertedCard(Long aLong)
    {
        showToastMessage(R.string.card_added_in_deck_text);
    }

    private void deleteCardFromDeck(String idGivenCard)
    {
        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onCardFetchedDeckCard, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.valueOf(idGivenCard), ConstantsUtil.PLAYER_ID);
    }

    private void onCardFetchedDeckCard(YugiohDeckCard yugiohDeckCard)
    {
        if (yugiohDeckCard.amountOwned >1)
        {
            yugiohDeckCard.amountOwned--;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, ()->{}, this::onUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(yugiohDeckCard);
        }
        else
        {
            DeleteDeckCardInPlayerDeck deleteDeckCardInPlayerDeck = new DeleteDeckCardInPlayerDeck(yugiohDeckDAO, ()->{}, this::onDeleted, this::onDatabaseError);
            deleteDeckCardInPlayerDeck.execute(yugiohDeckCard);
        }
    }

    private void onUpdated()
    {
        showToastMessage( R.string.deck_modified_text);
    }

    private void onDeleted()
    {
        showToastMessage(R.string.card_deleted_from_deck_text);
    }

    private void onDatabaseError()
    {
        SnackBarUtil.databaseErrorSnackBar(rootView);
    }
}
