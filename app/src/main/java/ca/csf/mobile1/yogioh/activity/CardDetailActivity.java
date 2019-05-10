package ca.csf.mobile1.yogioh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class CardDetailActivity extends AppCompatActivity
{
    private View rootView;
    private TextView quantityHeldTextView;
    private ImageView cardImage;
    private Button exchangeButton;

    private String receivedCardId;

    private YugiohDatabase yugiohDatabase;
    private YugiohDeckDAO yugiohDeckDAO;

    public static void start(Context context, String cardId) {
        Intent intent = new Intent(context, CardDetailActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID, cardId);

        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail_layout);

        receivedCardId = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_ID);

        initiateDatabaseConnection();

        createView();

    }

    private void createView()
    {
        rootView = findViewById(R.id.rootView);
        quantityHeldTextView = findViewById(R.id.quantityHeldTextView);
        cardImage = findViewById(R.id.cardDetailsImage);
        exchangeButton = findViewById(R.id.exchangeButton);
        exchangeButton.setOnClickListener(this::onExchangeButtonClicked);

        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(receivedCardId)));

        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardInDeckFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(Integer.parseInt(receivedCardId));
    }

    private void initiateDatabaseConnection()
    {
        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
    }

    private void onExchangeButtonClicked(View view)
    {
        ExchangeActivity.start(this, receivedCardId);
    }

    private void onLoading()
    {

    }

    private void onCardInDeckFetched(YugiohDeckCard playerCard)
    {
        quantityHeldTextView.setText(getString(R.string.number_of_cards_text, playerCard.amountOwned));
    }

    private void onDatabaseError()
    {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG).show();
    }
}
