package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InitialInsertionAsynchTask;
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
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.ConvertUtil;
import ca.csf.mobile1.yogioh.util.SnackBarUtil;


public class MainActivity extends AppCompatActivity
{
    private static final String PLAYER_USERNAME = "plucthemachine";
    private static final String PLAYER_NAME = "Pierre-Luc";
    private DeckAdapter deckAdapter;
    private View rootView;
    private ProgressBar progressBar;

    private int numberOfAsyncTasksRunning;

    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohPlayer> playerList;
    private List<YugiohCard> cardsOfPlayerDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.rootViewMain);
        progressBar = findViewById(R.id.loadingProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        cardsOfPlayerDeck = new ArrayList<>();

        initializeDeckRecyclerView();

        Button receiveCardFromTradeButton = findViewById(R.id.receiveCardButton);
        receiveCardFromTradeButton.setText(R.string.receive_button_text);
        receiveCardFromTradeButton.setOnClickListener(this::onReceiveCardFromTradeClicked);

        numberOfAsyncTasksRunning = 0;

        initialBdSetup();

        this.startService(new Intent(this, DailyNotificationService.class));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        fetchAllCards();
    }

    private void initialBdSetup()
    {
        YugiohDatabase yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
    }

    private void fetchAllCards()
    {
        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onLoading, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();
    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        hideProgressBar();

        if (cards.isEmpty())
        {
            createInitialCards();
        }
        else
        {
            fetchPlayers();
        }
    }

    private void createInitialCards()
    {
        InitialInsertionAsynchTask initialInsertionAsynchTask = new InitialInsertionAsynchTask(yugiohCardDAO, this::onInitialCardsInsertionDone);
        initialInsertionAsynchTask.execute(getResources().openRawResource(R.raw.yugiohinsertion));
    }

    private void onInitialCardsInsertionDone()
    {
        fetchPlayers();
    }

    private void fetchPlayers()
    {
        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onLoading, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();
    }

    private void onPlayersFetched(List<YugiohPlayer> players)
    {
        hideProgressBar();

        playerList = players;
        if (playerList.isEmpty())
        {
            createInitialPlayer();
        }
        else
        {
            fetchPlayerDeck();
        }
    }

    private void createInitialPlayer()
    {
        InsertOnePlayerAsyncTask insertOnePlayerAsyncTask = new InsertOnePlayerAsyncTask(yugiohPlayerDAO, this::onLoading, this::onInitialPlayerInserted, this::onDatabaseError);
        playerList.add(new YugiohPlayer(ConstantsUtil.PLAYER_ID, PLAYER_USERNAME, PLAYER_NAME));
        insertOnePlayerAsyncTask.execute(playerList.get(0));
    }

    private void onInitialPlayerInserted(Long id)
    {
        hideProgressBar();

        long newId = ConvertUtil.convertWrapperToPrimitive(id);
        playerList.get(0).id = (int) newId;

        fetchPlayerDeck();
    }

    private void fetchPlayerDeck()
    {
        FetchPlayerDeckAsyncTask fetchPlayerDeckAsyncTask = new FetchPlayerDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onPlayerCardsFetched, this::onDatabaseError);
        fetchPlayerDeckAsyncTask.execute(playerList.get(0));
    }

    private void onPlayerCardsFetched(List<YugiohDeckCard> yugiohDeckCards)
    {
        hideProgressBar();

        checkForRewardAvailability();

        long[] cardsIds = ConvertUtil.convertCardIdsFromIntegerToLongArray(yugiohDeckCards);

        FetchCardsByIdsAsyncTask fetchCardsByIdsAsyncTask = new FetchCardsByIdsAsyncTask(yugiohCardDAO, this::onLoading, this::onSpecificCardsFetched, this::onDatabaseError);
        fetchCardsByIdsAsyncTask.execute(ConvertUtil.convertPrimitiveToWrapper(cardsIds));
    }

    private void onSpecificCardsFetched(List<YugiohCard> cards)
    {
        hideProgressBar();

        cardsOfPlayerDeck = cards;

        deckAdapter.setDataSet(cardsOfPlayerDeck);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        fetchAllCards();
    }

    private void initializeDeckRecyclerView()
    {
        RecyclerView yugiohDeckRecyclerView = findViewById(R.id.playerDeckRecyclerView);
        LinearLayoutManager deckLayoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        yugiohDeckRecyclerView.setLayoutManager(deckLayoutManager);
        deckAdapter = new DeckAdapter(this, cardsOfPlayerDeck);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);
        yugiohDeckRecyclerView.addItemDecoration(new DividerItemDecoration(this, deckLayoutManager.getOrientation()));
    }

    private void checkForRewardAvailability()
    {
        if (AvailableGiftSharedPreferenceUtil.getAvailabilityOfDailyReward(this))
        {
            RewardActivity.start(this);
        }
    }

    private void onReceiveCardFromTradeClicked(View view)
    {
        ExchangeActivity.startReceive(this, false);
    }

    private void onDatabaseError()
    {
        SnackBarUtil.databaseErrorSnackBar(rootView);
    }

    private void onLoading()
    {
        ++numberOfAsyncTasksRunning;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar()
    {
        --numberOfAsyncTasksRunning;
        if (numberOfAsyncTasksRunning == 0) progressBar.setVisibility(View.INVISIBLE);
    }

}
