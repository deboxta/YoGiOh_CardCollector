package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


public class MainActivity extends AppCompatActivity
{
    private static final String EXTRA_CARD_ID_RETURN = "EXTRA_ID_RETURN";
    public static final String YUGIOH_DATABASE_NAME = "yugiohDatabase";
    private RecyclerView yugiohDeckRecyclerView;
    private DeckAdapter deckAdapter;
    private LinearLayoutManager deckLayoutManager;
    private View rootView;
    private ProgressBar progressBar;

    private boolean gift;


    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohPlayer> playerList;
    private List<YugiohCard> allCards;
    private List<YugiohCard> playerCards;
    private List<YugiohDeckCard> currentDeck;
    private String beamedCardId;

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

        beamedCardId = getIntent().getStringExtra(EXTRA_CARD_ID_RETURN);

        //BD setup and fetching
        initialBdSetup();

        currentDeck = new ArrayList<>();

        //Notification section (Anthony)
        gift = false;

        //gift = DailyNotificationService.start(this, gift);
        //RewardActivity.start(this);

    }

    private void initialBdSetup()
    {
        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, YUGIOH_DATABASE_NAME).build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();
        
        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onLoading, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();

        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onLoading, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();
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
            FetchPlayerDeckAsyncTask fetchPlayerDeckAsyncTask = new FetchPlayerDeckAsyncTask(yugiohDeckDAO, this::onLoading, this::onPlayerCardsFetched, this::onDatabaseError);
            fetchPlayerDeckAsyncTask.execute(players.get(0));
        }
    }

    private void createInitialPlayer()
    {
        InsertOnePlayerAsyncTask insertOnePlayerAsyncTask = new InsertOnePlayerAsyncTask(yugiohPlayerDAO,this::onLoading,this::onInitialPlayerInserted, this::onDatabaseError);
        insertOnePlayerAsyncTask.execute(new YugiohPlayer());
    }

    private void onInitialPlayerInserted(Long id)
    {
        progressBar.setVisibility(View.INVISIBLE);

        //TODO: Lancer le daily reward
    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        progressBar.setVisibility(View.INVISIBLE);

        allCards = cards;
        if (allCards.size() == 0)
        {
            createInitialCards();
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
}
