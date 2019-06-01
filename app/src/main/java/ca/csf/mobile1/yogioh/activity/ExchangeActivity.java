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

    //BEN_CORRECTION : Type enveloppe utilisé au lieu de type de base pour boolean.
    //BEN_CORRECTION : Paramêtre drapeau nommé "typeOfExchange" complètement inutile compte tenu que vous
    //                 avec deux méthodes "start" : une pour "trade" et une pour "receive". Autrement dit,
    //                 c'est redondant. Erreur de logique et bogue potentiel.
    //
    //                 Pourquoi "Bogue Potentiel" ? Imaginez que j'appelle cette fonction comme ceci :
    //
    //                      ExchangeActivity.startTrade(this, "123456", false);
    //
    //                 L'activité démarre alors en mode "receive", même si on lui a pourtant demandé de
    //                 démarer en mode "trade".
    //BEN_CORRECTION : Dans toute votre application, "cardId" est un "int", mais ici, vous le recevez sous
    //                 la forme d'une "String". Par désign, vous auriez du demander un "int". Patch.
    public static void startTrade(Context context, String cardId, Boolean typeOfExchange)
    {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, cardId);
        intent.putExtra(ConstantsUtil.EXTRA_TYPE_OF_EXCHANGE, typeOfExchange);

        context.startActivity(intent);
    }

    //BEN_CORRECTION : Type enveloppe utilisé au lieu de type de base pour boolean.
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
            //BEN_REVIEW : L'intent contient déjà toutes les infos que vous avez besoin.
            //BEN_CORRECTION : Bogue. En mode "trade", il suffit de tourner le téléphone pour passer en
            //                 mode "receive". Ça marche encore, parce que la variable "typeOfExchange" ne sert finalement
            //                 qu'à de l'affichage.
            //
            //                 En passant, bloquer le changement d'orientation est, selon moi, une pratique paresseuse.
            //                 C'est peut être la raison pour laquelle vous avez pas remarqué ça.
            //                 Patch.
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
            //BEN_REVIEW : Toast est considéré comme obsolete, même si ce n'est pas écrit nulle part dans le code.
            //             Préférez "Snackbar" afin de respecter les standards de la plateforme.
            //
            //             Vous le faites pourtant plus bas.
            //
            showToastMessage(R.string.error_text_nfc);
            //BEN_CORRECTION : Je vois aussi ici un "early return". Mauvaise pratique.
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
        pendingIntent = PendingIntent.getActivity(this, ConstantsUtil.VALUE_ZERO, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), ConstantsUtil.VALUE_ZERO);
    }

    private View createView()
    {
        idView = findViewById(R.id.textViewNfc);
        cardView = findViewById(R.id.cardView);
        //BEN_REVIEW : Pourquoi ça retourne la "rootView" ? Pourquoi ne pas avoir fait l'assignation ici aussi, comme
        //             toutes les autres views ?
        return findViewById(R.id.rootViewExchange);
    }

    //BEN_CORRECTION : Vague. setVariables ne veut absolument rien dire.
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

    //BEN_CORRECTION : Inutile, car vous appellez "nfcAdapter.setNdefPushMessage". On s'en était parlé quand j'avais résolu le fameux
    //                 bogue avec vous.
    //
    //                 Code mort.
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

    //BEN_CORRECTION : private manquant +  Identation incorrecte.
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
            //BEN_CORRECTION : Pourquoi n'avez vous pas mis de ProgressBar ici ? C'est pourtant nécessaire.
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
        if (yugiohDeckCard.amountOwned > 1)
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
