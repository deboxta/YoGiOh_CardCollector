package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.InsertOneCardInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.UpdateDeckCardAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class RewardActivity extends AppCompatActivity
{

    public static final int MAX_CARD_ID = 10;
    private Button closeButton;
    private Button openButton;
    private ImageView cardImage;
    CountDownTimer countDownTimer;

    private int cardId;
    private Random cardIdRandomizer;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    public static void start(Context context)
    {
        Intent rewardPopup = new Intent(context, RewardActivity.class);

        context.startActivity(rewardPopup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_popup);

        initialBdSetup();

        cardImage = findViewById(R.id.rewardImageView);
        openButton = findViewById(R.id.buttonOpen);
        closeButton = findViewById(R.id.buttonClose);

        cardImage.setImageResource(R.drawable.backside_card);

        openButton.setOnClickListener(this::openReward);
        closeButton.setOnClickListener(this::closeReward);

        cardIdRandomizer = new Random();
        cardId = cardIdRandomizer.nextInt(MAX_CARD_ID);

         countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish();
            }
        };

    }

    private void closeReward(View view)
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, 5));
        finish();
    }

    private void openReward(View view)
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, cardId));
        countDownTimer.start();

        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(cardId);
    }

    private void initialBdSetup()
    {
        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
    }

    private void onLoading()
    {

    }

    private void onCardAdded(Long cardAdded)
    {

    }

    private void onDatabaseError()
    {

    }

    private void onCardFetched(YugiohDeckCard cardInDeck)
    {
        if (cardInDeck == null)
        {
            //TODO: envoyé la bonne carte
            /*InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardAdded, this::onDatabaseError);
        insertOneCardInDeckAsyncTask.execute();*/
        }
        else
        {
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, this::onLoading, this::onDeckCardUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
    }

    public void onDeckCardUpdated()
    {
        //TODO: timer 5 second et puis finish quand il est terminé
    }
}
