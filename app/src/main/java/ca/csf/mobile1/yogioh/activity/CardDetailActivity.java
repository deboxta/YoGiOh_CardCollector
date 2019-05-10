package ca.csf.mobile1.yogioh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class CardDetailActivity extends AppCompatActivity
{
    private ImageView cardImage;
    private Button exchangeButton;
    private View rootView;

    private String receivedCardId;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
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

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        rootView = findViewById(R.id.rootView);
        cardImage = findViewById(R.id.cardDetailsImage);
        exchangeButton = findViewById(R.id.exchangeButton);

        exchangeButton.setOnClickListener(this::onExchangeButtonClicked);


        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(receivedCardId)));
        FetchCardsByIdsAsyncTask fetchCard = new FetchCardsByIdsAsyncTask(yugiohCardDAO, this::onCardFetching, this::onCardFetched, this::onDatabaseError);
        fetchCard.execute(Long.parseLong(receivedCardId));


    }

    private void onExchangeButtonClicked(View view)
    {
        //This is the action to do when a card is selected on the deck to transfer via nfc
        ExchangeActivity.start(this, receivedCardId);
    }

    private void onCardFetching()
    {

    }

    private void onCardFetched(List<YugiohCard> card)
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, Integer.valueOf(receivedCardId)));
    }

    private void onDatabaseError()
    {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG).show();
    }
}
