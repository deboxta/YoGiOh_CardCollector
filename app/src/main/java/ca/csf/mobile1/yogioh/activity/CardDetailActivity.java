package ca.csf.mobile1.yogioh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;
import ca.csf.mobile1.yogioh.util.SnackBarUtil;

public class CardDetailActivity extends AppCompatActivity
{
    private View rootView;
    private TextView quantityOfCardOwnedTextView;

    //Received from main via intent.
    private String receivedCardId;
    private String receivedCardDescription;

    private YugiohDeckDAO yugiohDeckDAO;

    //BEN_REVIEW : Au lieu d'envoyer un objet décomposé, mieux vaut envoyer l'objet au grand complet. Comme ça,
    //             il aura tout ce dont il a besoin potentiellement.
    public static void start(Context context, String receivedCardId, String receivedCardDescription) {
        Intent intent = new Intent(context, CardDetailActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, receivedCardId);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_DESCRIPTION, receivedCardDescription);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail_layout);

        receivedCardId = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_ID);
        receivedCardDescription = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_DESCRIPTION);

        initiateDatabaseConnection();

        createView();
    }

    private void createView()
    {
        rootView = findViewById(R.id.rootViewCardDetail);
        quantityOfCardOwnedTextView = findViewById(R.id.quantityHeldTextView);
        ImageView cardImage = findViewById(R.id.cardDetailsImage);
        Button tradeButton = findViewById(R.id.tradeButton);
        tradeButton.setOnClickListener(this::onTradeButtonClicked);
        TextView cardDescriptionTextView = findViewById(R.id.cardDescriptionTextView);
        cardDescriptionTextView.setText(receivedCardDescription);
        cardDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());

        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(receivedCardId)));
    }

    private void fetchCardsInDeck() {
        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onCardInDeckFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.parseInt(receivedCardId), ConstantsUtil.PLAYER_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCardsInDeck();
    }

    private void initiateDatabaseConnection()
    {
        YugiohDatabase yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
    }

    private void onTradeButtonClicked(View view)
    {
        ExchangeActivity.startTrade(this, receivedCardId, true);
    }

    private void onCardInDeckFetched(YugiohDeckCard playerCard)
    {
        //If card was traded and the previous quantity held was 1, the user is sent back to the main activity
        // because he does not possess the card anymore
        if (playerCard == null)
        {
            finish();
        }
        try
        {
            quantityOfCardOwnedTextView.setText(getString(R.string.number_of_cards_text, playerCard.amountOwned));
        }
        catch(NullPointerException e)
        {
            quantityOfCardOwnedTextView.setText(R.string.card_quantity_error_text);
        }
    }

    private void onDatabaseError()
    {
        SnackBarUtil.databaseErrorSnackBar(rootView);
    }
}
