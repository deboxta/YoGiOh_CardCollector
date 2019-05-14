package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InitialInsetionAsynchTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchPlayerDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.player.FetchPlayersAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.player.InsertOnePlayerAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.ConvertUtil;


public class MainActivity extends AppCompatActivity
{
    private static final String EXTRA_CARD_ID_RETURN = "EXTRA_ID_RETURN";
    private RecyclerView yugiohDeckRecyclerView;
    private DeckAdapter deckAdapter;
    private LinearLayoutManager deckLayoutManager;
    private View rootView;
    private ProgressBar progressBar;
    private Button rewardButton;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohPlayer> playerList;
    private List<YugiohCard> allCards;
    private List<YugiohCard> playerCards;
    private List<YugiohDeckCard> currentDeck;
    private String beamedCardId;

    public SharedPreferences sharedPref;

    public static void start(Context context, String cardId) {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(EXTRA_CARD_ID_RETURN, cardId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.rootView);
        progressBar = findViewById(R.id.loadingProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        rewardButton = findViewById(R.id.rewardButton);



        beamedCardId = getIntent().getStringExtra(EXTRA_CARD_ID_RETURN);

        //BD setup and fetching
        initialBdSetup();

        currentDeck = new ArrayList<>();

        //Notification section (Anthony)
        //Start the notification service
        this.startService(new Intent(this, DailyNotificationService.class));

        sharedPref = this.getSharedPreferences("availableGift", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("availableGift", true);
        editor.apply();

        rewardButton.setOnClickListener(this::openRewardActivity);

        setupRewardButton();

    }

    private void setupRewardButton()
    {
        if (sharedPref.getBoolean("availableGift", true))
        {
            rewardButton.setBackgroundColor(getResources().getColor(R.color.clickable_reward_button));
            rewardButton.setText(R.string.daily_reward_button_available);
            rewardButton.setClickable(true);
        }
        else
        {
            rewardButton.setBackgroundColor(getResources().getColor(R.color.disable_reward_button));
            rewardButton.setText(R.string.daily_reward_button_disable);
            rewardButton.setClickable(false);
        }
    }

    private void openRewardActivity(View view)
    {
        startActivity(new Intent(this, RewardActivity.class));
    }

    private void initialBdSetup()
    {
        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onLoading, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();

        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onLoading, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();
    }

    private void onPlayerCardsFetched(List<YugiohDeckCard> yugiohDeckCards)
    {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void onCardInDeckFetched(List<YugiohCard> yugiohCards)
    {
        progressBar.setVisibility(View.INVISIBLE);

        playerCards = yugiohCards;
        if (playerCards.size() == 0)
        {
            createNewDeckCard();
        }
    }

    private void createNewDeckCard() {
//        InsertMultipleCardsInDeckAsyncTask insertMultipleCardsInDeckAsyncTask = new InsertMultipleCardsInDeckAsyncTask(yugiohDeckDAO, this::onInsertingOneCard, this::onInsertedOneCard, this::onDatabaseError);
//        insertMultipleCardsInDeckAsyncTask.execute(currentDeck);
    }

    private void onPlayersFetched(List<YugiohPlayer> players)
    {
        progressBar.setVisibility(View.INVISIBLE);

        playerList = players;
        if(playerList.size() == 0)
        {
            createInitialPlayer();
        }
        else
        {

        }
    }

    private void createInitialPlayer()
    {
        InsertOnePlayerAsyncTask insertOnePlayerAsyncTask = new InsertOnePlayerAsyncTask(yugiohPlayerDAO,this::onLoading,this::onInitialPlayerInserted, this::onDatabaseError);
        playerList.add(new YugiohPlayer());
        insertOnePlayerAsyncTask.execute(playerList.get(0));
    }

    private void onInitialPlayerInserted(Long id)
    {
        progressBar.setVisibility(View.INVISIBLE);
        long newid = ConvertUtil.convertWrapperToPrimitive(id);
        playerList.get(0).id = (int)newid;

        RewardActivity.start(this);
    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        progressBar.setVisibility(View.INVISIBLE);

        allCards = cards;
        if (allCards.size() == 0)
        {
            createInitialCards();

            FetchPlayerDeckAsyncTask fetchPlayerDeckAsyncTask = new FetchPlayerDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onPlayerCardsFetched, this::onDatabaseError);
            fetchPlayerDeckAsyncTask.execute(playerList.get(0));
        }

        initializeDeckRecyclerView();

    }

    private void createInitialCards()
    {
        InitialInsetionAsynchTask initialInsetionAsynchTask = new InitialInsetionAsynchTask(yugiohCardDAO);
        initialInsetionAsynchTask.execute(getResources().openRawResource(R.raw.yugiohinsertion));
    }

    private void initializeDeckRecyclerView()
    {
        deckLayoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView = findViewById(R.id.myDeck);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        yugiohDeckRecyclerView.setLayoutManager(deckLayoutManager);
        deckAdapter = new DeckAdapter(this, allCards);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);
        yugiohDeckRecyclerView.addItemDecoration(new DividerItemDecoration(this, deckLayoutManager.getOrientation()));
    }

    private void onDatabaseError()
    {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG).show();
    }

    private void onLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        setupRewardButton();
    }

}
