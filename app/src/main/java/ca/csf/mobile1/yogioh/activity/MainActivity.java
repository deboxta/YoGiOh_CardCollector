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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
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
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;
import ca.csf.mobile1.yogioh.util.ConvertUtil;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView yugiohDeckRecyclerView;
    private DeckAdapter deckAdapter;
    private LinearLayoutManager deckLayoutManager;
    private View rootView;
    private ProgressBar progressBar;

    private int numberOfAsyncTasksRunning;
    private String beamedCardId;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohPlayer> playerList;
    private List<YugiohCard> allCards;
    private List<YugiohDeckCard> playerDeck;
    private List<YugiohCard> cardsOfPlayerDeck;

    public static void start(Context context, String cardId) {
        Intent intent = new Intent(context, ExchangeActivity.class);
        intent.putExtra(ConstantsUtil.EXTRA_CARD_ID_RETURN, cardId);

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

        beamedCardId = getIntent().getStringExtra(ConstantsUtil.EXTRA_CARD_ID_RETURN);

        numberOfAsyncTasksRunning = 0;

        //BD setup and fetching
        initialBdSetup();

        playerDeck = new ArrayList<>();

        this.startService(new Intent(this, DailyNotificationService.class));
    }

    private void initialBdSetup()
    {
        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, ConstantsUtil.YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onLoading, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();
    }

    private void onPlayerCardsFetched(List<YugiohDeckCard> yugiohDeckCards)
    {
        hideProgressBar();
        playerDeck = yugiohDeckCards;
        if (playerDeck == null)
        {
            RewardActivity.start(this);
        }
        else
        {
            long[] cardsIds = ConvertUtil.convertCardIdsFromIntegerToLongArray(yugiohDeckCards);

            FetchCardsByIdsAsyncTask fetchCardsByIdsAsyncTask = new FetchCardsByIdsAsyncTask(yugiohCardDAO, this::onLoading, this::onSpecificCardsFetched, this::onDatabaseError);
            fetchCardsByIdsAsyncTask.execute(ConvertUtil.convertPrimitiveToWrapper(cardsIds));
        }
    }

    private void onPlayersFetched(List<YugiohPlayer> players)
    {
        hideProgressBar();

        playerList = players;
        if(playerList.size() == 0)
        {
            createInitialPlayer();
        }
        else
        {
            FetchPlayerDeckAsyncTask fetchPlayerDeckAsyncTask = new FetchPlayerDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onPlayerCardsFetched, this::onDatabaseError);
            fetchPlayerDeckAsyncTask.execute(playerList.get(0));
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
        hideProgressBar();

        long newid = ConvertUtil.convertWrapperToPrimitive(id);
        playerList.get(0).id = (int)newid;
        //TODO: What happens next?
    }

    private void onSpecificCardsFetched(List<YugiohCard> cards)
    {
        hideProgressBar();

        cardsOfPlayerDeck = cards;
        initializeDeckRecyclerView();
    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        hideProgressBar();

        allCards = cards;
        if (allCards.size() == 0)
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
        InitialInsetionAsynchTask initialInsetionAsynchTask = new InitialInsetionAsynchTask(yugiohCardDAO, this::onInitialCardsInsertionDone);
        initialInsetionAsynchTask.execute(getResources().openRawResource(R.raw.yugiohinsertion));
    }

    private void onInitialCardsInsertionDone()
    {
        fetchPlayers();
    }

    private void initializeDeckRecyclerView()
    {
        //TODO: changer allCards par cardsOfPlayerDeck
        deckLayoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView = findViewById(R.id.myDeck);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        yugiohDeckRecyclerView.setLayoutManager(deckLayoutManager);
        deckAdapter = new DeckAdapter(this, cardsOfPlayerDeck);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);
        yugiohDeckRecyclerView.addItemDecoration(new DividerItemDecoration(this, deckLayoutManager.getOrientation()));

        if (AvailableGiftSharedPreferenceUtil.getAvailibilityOfDailyReward(this))
        {
            RewardActivity.start(this);
        }
    }

    private void fetchPlayers()
    {
        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onLoading, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();
    }

    private void onDatabaseError()
    {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG).show();
    }

    private void onLoading()
    {
        numberOfAsyncTasksRunning++;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar()
    {
        numberOfAsyncTasksRunning--;
        if (numberOfAsyncTasksRunning == 0) progressBar.setVisibility(View.INVISIBLE);
    }

}
