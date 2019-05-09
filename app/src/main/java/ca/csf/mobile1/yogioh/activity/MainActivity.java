package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsByIdsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InitialInsetionAsynchTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.FetchPlayerDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.InsertMultipleCardsInDeckAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.deck.InsertOneCardInDeckAsyncTask;
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
    private RecyclerView yugiohDeckRecyclerView;
    private DeckAdapter deckAdapter;
    private LinearLayoutManager deckLayoutManager;

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

        beamedCardId = getIntent().getStringExtra(EXTRA_CARD_ID_RETURN);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        //BD setup and fetching
        initialBdSetup();

        currentDeck = new ArrayList<>();

        currentDeck.add(new YugiohDeckCard(playerList.get(1).id, 2, 1));

        //Notification section (Antony)
        gift = false;

//        gift = DailyNotificationService.start(this, gift);
//        RewardActivity.start(this);

    }

    private void initialBdSetup()
    {
        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onCardsFetching, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();

        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onPlayerFetching, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();

        FetchPlayerDeckAsyncTask fetchPlayerDeckAsyncTask = new FetchPlayerDeckAsyncTask(yugiohDeckDAO, this::onPlayerCardsFetching, this::onPlayerCardsFetched, this::onDatabaseError);
        fetchPlayerDeckAsyncTask.execute(playerList.get(1));
    }

    private void onPlayerCardsFetched(List<YugiohDeckCard> yugiohDeckCards)
    {
        FetchCardsByIdsAsyncTask fetchCardsByIdsAsyncTask = new FetchCardsByIdsAsyncTask(yugiohCardDAO, this::onCardInDeckFetching, this::onCardInDeckFetched, this::onDatabaseError);
        fetchCardsByIdsAsyncTask.execute(Long.valueOf(yugiohDeckCards.get(1).cardId));
    }

    private void onPlayerCardsFetching() { }

    private void onCardInDeckFetched(List<YugiohCard> yugiohCards)
    {
        playerCards = yugiohCards;
        if (playerCards.size() == 0)
        {
            createNewDeckCard();
        }
    }

    private void onCardInDeckFetching() { }

    private void createNewDeckCard() {
//        InsertMultipleCardsInDeckAsyncTask insertMultipleCardsInDeckAsyncTask = new InsertMultipleCardsInDeckAsyncTask(yugiohDeckDAO, this::onInsertingOneCard, this::onInsertedOneCard, this::onDatabaseError);
//        insertMultipleCardsInDeckAsyncTask.execute(currentDeck);
    }

    private void onInsertedOneCard(Long[] longs) { }

    private void onInsertingOneCard() { }


    private void onPlayersFetched(List<YugiohPlayer> players)
    {
        playerList = players;
        if(playerList.size() == 0)
        {
            createInitialPlayer();
        }
    }

    private void onPlayerFetching() { }

    private void createInitialPlayer()
    {
        InsertOnePlayerAsyncTask insertOnePlayerAsyncTask = new InsertOnePlayerAsyncTask(yugiohPlayerDAO,this::onPlayerInserting,this::onPlayerInserted, this::onDatabaseError);
        insertOnePlayerAsyncTask.execute(new YugiohPlayer());
    }

    private void onPlayerInserted(Long id) { }

    private void onPlayerInserting() { }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        allCards = cards;
        if (allCards.size() == 0)
        {
            createInitialCards();
        }

        setDeckAdapter();

    }

    private void onCardsFetching() { }

    private void createInitialCards()
    {
        InitialInsetionAsynchTask initialInsetionAsynchTask = new InitialInsetionAsynchTask(yugiohCardDAO);
        initialInsetionAsynchTask.execute(getResources().openRawResource(R.raw.yugiohinsertion));
    }

    private void setDeckAdapter()
    {
        deckLayoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView = findViewById(R.id.myDeck);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        yugiohDeckRecyclerView.setLayoutManager(deckLayoutManager);
        deckAdapter = new DeckAdapter(this, allCards);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);
        yugiohDeckRecyclerView.addItemDecoration(new DividerItemDecoration(this, deckLayoutManager.getOrientation()));
    }

    private void onDatabaseError() { }

}
