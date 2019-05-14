package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class RewardActivity extends AppCompatActivity
{

    public static final int MAX_CARD_ID = 9;
    private Button closeButton;
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
        setContentView(R.layout.activity_reward);

        initialBdSetup();

        cardImage = findViewById(R.id.rewardImageView);
        closeButton = findViewById(R.id.buttonClose);

        cardImage.setImageResource(R.drawable.backside_card);

        closeButton.setOnClickListener(this::closeReward);

        cardIdRandomizer = new Random();
        cardId = cardIdRandomizer.nextInt(MAX_CARD_ID) + 1;

        countDownTimer = new CountDownTimer(5000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {

            }
            public void onFinish()
            {
                finish();
            }
        };

        if(AvailableGiftSharedPreferenceUtil.getAvailibilityOfDailyReward(this))
        {
            giveReward();
        }

    }

    private void closeReward(View view)
    {
        finish();
    }

    private void giveReward()
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, cardId));

        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardFetched, this::onDatabaseError);
        fetchCardInDeckAsyncTask.execute(cardId, ConstantsUtil.PLAYER_ID);
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
       AvailableGiftSharedPreferenceUtil.editAvailibilityOfDailyReward(this, false);
       countDownTimer.start();
    }

    private void onDatabaseError()
    {

    }

    private void onCardFetched(YugiohDeckCard cardInDeck)
    {
        if (cardInDeck == null)
        {
            InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onCardAdded, this::onDatabaseError);
            insertOneCardInDeckAsyncTask.execute(new YugiohDeckCard(ConstantsUtil.PLAYER_ID, cardId, ConstantsUtil.NUMBER_OF_CARDS_TO_ADD));
        }
        else
        {
            cardInDeck.amountOwned += ConstantsUtil.NUMBER_OF_CARDS_TO_ADD;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, this::onLoading, this::onDeckCardUpdated, this::onDatabaseError);
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
    }

    public void onDeckCardUpdated()
    {
        AvailableGiftSharedPreferenceUtil.editAvailibilityOfDailyReward(this, false);
        countDownTimer.start();
    }
}
