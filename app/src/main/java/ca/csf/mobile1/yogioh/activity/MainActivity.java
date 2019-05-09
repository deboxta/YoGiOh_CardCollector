package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InitialInsetionAsynchTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InsertCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.player.FetchPlayersAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.player.InsertOnePlayerAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView yugiohDeckRecyclerView;
    private DeckAdapter deckAdapter;
    private LinearLayoutManager deckLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private static final String INSERTIONCARTELOGMESSAGE = "insertionCarte";
    private boolean gift;
    private Dialog myDialog;

    private static final int PLAYER_NUMBER = 1;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohPlayer> playerList;

    private List<YugiohCard> allCards;
    private List<YugiohCard> currentDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();


        FetchCardsAsyncTask fetchCardsAsyncTask = new FetchCardsAsyncTask(yugiohCardDAO, this::onCardsFetching, this::onCardsFetched, this::onDatabaseError);
        fetchCardsAsyncTask.execute();

        FetchPlayersAsyncTask fetchPlayersAsyncTask = new FetchPlayersAsyncTask(yugiohPlayerDAO, this::onPlayerFetching, this::onPlayersFetched, this::onDatabaseError);
        fetchPlayersAsyncTask.execute();

        currentDeck = new ArrayList<>();

        deckLayoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView = findViewById(R.id.myDeck);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        yugiohDeckRecyclerView.setLayoutManager(deckLayoutManager);
        deckAdapter = new DeckAdapter(this, currentDeck);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);
        yugiohDeckRecyclerView.addItemDecoration(new DividerItemDecoration(this, deckLayoutManager.getOrientation()));

        FetchCardsAsyncTask task = new FetchCardsAsyncTask(yugiohCardDAO, this::onCardsFetching, this::onCardsFetched, this::onDatabaseError);
        task.execute();

        //This is the action to do when a card is selected on the deck to transfer via nfc
        //ExchangeActivity.start(this, "15");      //Replace the value by the id of the selected card to transfer via nfc

        //gift = false;

        //myDialog = new Dialog(this);
        //myDialog.setContentView(R.layout.notificationpopup);
        //myDialog.show();


        //startService(new Intent(this, DailyNotificationService.class));
        //SharedPreferences sharedPreferences = this.getSharedPreferences("availableGift", Context.MODE_PRIVATE);
        //gift = sharedPreferences.getBoolean("gift", false);

        //Intent rewardPopup = new Intent(this, RewardActivity.class);
        //startActivity(rewardPopup);

    }

    private void onPlayerFetching()
    {

    }

    private void onPlayersFetched(List<YugiohPlayer> players)
    {
        playerList = players;
        if(playerList.size() == 0)
        {
            createInitialPlayer();
        }
    }

    private void createInitialPlayer()
    {
        InsertOnePlayerAsyncTask insertOnePlayerAsyncTask = new InsertOnePlayerAsyncTask(yugiohPlayerDAO,this::onPlayerInserting,this::onPlayerInserted, this::onDatabaseError);
        insertOnePlayerAsyncTask.execute(new YugiohPlayer());
    }

    private void onPlayerInserted(Long id)
    {

    }

    private void onPlayerInserting()
    {

    }

    private void onCardsFetching()
    {

    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        allCards = cards;
        if (allCards.size() == 0)
        {
            createInitialCards();
        }
    }

    private void createInitialCards()
    {
        InitialInsetionAsynchTask initialInsetionAsynchTask = new InitialInsetionAsynchTask(yugiohCardDAO);
        initialInsetionAsynchTask.execute(getResources().openRawResource(R.raw.yugiohinsertion));
    }

    private void onCardInserted(Long[] longs)
    {
        Log.i("InsertedInDatabase", "inserted");
    }

    private void onInsertingCard()
    {

    }

    private void onDatabaseError()
    {

    }

}
