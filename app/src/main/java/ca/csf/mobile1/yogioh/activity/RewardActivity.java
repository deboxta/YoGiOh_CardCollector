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
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class RewardActivity extends AppCompatActivity
{

    public static final int MAX_CARD_ID = 13;
    public static final int TIMER_IN_MILLIS_TOTAL_COUNTDOWN = 5000;
    public static final int TIMER_COUNTDOWN_INTERVAL = 1000;
    public static final int LOWEST_CARD_ID = 1;

    private Button closeButton;
    private ImageView cardImage;

    private CountDownTimer countDownTimer;
    private int cardId;
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

        //BEN_REVIEW : Il y a des façons beaucoup plus simples pour faire cela.
        //             Voir "Handler.postDelayed".
        //             https://developer.android.com/reference/android/os/Handler.html#postDelayed(java.lang.Runnable,%20java.lang.Object,%20long)

        //BEN_REVIEW : Aussi, coté expérience utilisateur, je pense pas que ce soit une bonne idée.
        countDownTimer = new CountDownTimer(TIMER_IN_MILLIS_TOTAL_COUNTDOWN, TIMER_COUNTDOWN_INTERVAL)
        {
            public void onTick(long millisUntilFinished)
            {

            }
            public void onFinish()
            {
                finish();
            }
        };

        if(AvailableGiftSharedPreferenceUtil.getAvailabilityOfDailyReward(this))
        {
            closeButton.setEnabled(false);
            giveReward();
        }
    }

    private void closeReward(View view)
    {
        finish();
    }

    private void giveReward()
    {
        Random randomCardId = new Random();
        //BEN_REVIEW : Le "best" aurait été de demander à la BD un "id" random. C'est quelque chose de facile à faire.
        //             Par contre, compte tenu du contexte, on va faire avec.
        cardId = randomCardId.nextInt(MAX_CARD_ID) + LOWEST_CARD_ID;

        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, cardId));

        FetchCardInDeckAsyncTask fetchCardInDeckAsyncTask = new FetchCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onCardFetched, ()->{});
        fetchCardInDeckAsyncTask.execute(cardId, ConstantsUtil.PLAYER_ID);

        AvailableGiftSharedPreferenceUtil.editAvailabilityOfDailyReward(this, false);
    }

    private void initialBdSetup()
    {
        YugiohDatabase yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
    }

    private void onCardAdded(Long cardAdded)
    {
        closeButton.setEnabled(true);
        countDownTimer.start();
    }

    private void onCardFetched(YugiohDeckCard cardInDeck)
    {
        if (cardInDeck == null)
        {
            InsertOneCardInDeckAsyncTask insertOneCardInDeckAsyncTask = new InsertOneCardInDeckAsyncTask(yugiohDeckDAO, ()->{}, this::onCardAdded, ()->{});
            insertOneCardInDeckAsyncTask.execute(new YugiohDeckCard(ConstantsUtil.PLAYER_ID, cardId, ConstantsUtil.NUMBER_OF_CARDS_TO_ADD));
        }
        else
        {
            cardInDeck.amountOwned += ConstantsUtil.NUMBER_OF_CARDS_TO_ADD;
            UpdateDeckCardAsyncTask updateDeckCardAsyncTask = new UpdateDeckCardAsyncTask(yugiohDeckDAO, ()->{}, this::onDeckCardUpdated, ()->{});
            updateDeckCardAsyncTask.execute(cardInDeck);
        }
    }

    public void onDeckCardUpdated()
    {
        closeButton.setEnabled(true);
        countDownTimer.start();
    }
}
