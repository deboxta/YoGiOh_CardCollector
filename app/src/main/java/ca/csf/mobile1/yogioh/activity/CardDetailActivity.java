package ca.csf.mobile1.yogioh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;

public class CardDetailActivity extends AppCompatActivity
{
    private ImageView cardImage;
    private Button exchangeButton;

    private String recievedCardId;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        cardImage = findViewById(R.id.card_details_image);
        exchangeButton = findViewById(R.id.exchange_btn);
        exchangeButton.setOnClickListener(this::onExchangeButtonClicked);

        recievedCardId = getIntent().getStringExtra("EXTRA_ID");

        FetchCardsByIdsAsyncTask fetchCard = new FetchCardsByIdsAsyncTask(yugiohCardDAO, this::onCardFetching, this::onCardFetched, this::onDatabaseError);
        fetchCard.execute(Long.parseLong(recievedCardId));


    }

    private void onExchangeButtonClicked(View view)
    {
        //This is the action to do when a card is selected on the deck to transfer via nfc
        ExchangeActivity.start(this, recievedCardId);
    }

    private void onCardFetching()
    {

    }

    private void onCardFetched(List<YugiohCard> card)
    {

    }

    private void onDatabaseError()
    {

    }
}
