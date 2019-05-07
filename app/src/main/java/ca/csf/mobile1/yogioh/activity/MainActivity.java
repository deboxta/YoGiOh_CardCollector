package ca.csf.mobile1.yogioh.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import java.util.List;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.queries.card.FetchCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.queries.card.InsertCardsAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView yugiohDeckRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DeckAdapter deckAdapter;

    public static final String CHANNEL_ID = "channel";
    private static final String INSERTIONCARTELOGMESSAGE = "insertionCarte";
    private boolean gift;
    private Dialog myDialog;

    private static final int PLAYER_NUMBER = 1;

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    private List<YugiohCard> currentDeck;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        //TODO getResources().openRawResource(R.raw.yugiohinsertion)
        //TODO Remove this ............
        InsertCardsAsyncTask insertCardsAsyncTask = new InsertCardsAsyncTask(yugiohCardDAO,this::onInsertingCard,this::onCardInserted,this::onDatabaseError);
        insertCardsAsyncTask.execute(new YugiohCard());

        yugiohDeckRecyclerView = findViewById(R.id.myDeck);
        yugiohDeckRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        yugiohDeckRecyclerView.setLayoutManager(layoutManager);
        deckAdapter = new DeckAdapter(this, null);
        yugiohDeckRecyclerView.setAdapter(deckAdapter);

        FetchCardsAsyncTask task = new FetchCardsAsyncTask(yugiohCardDAO, this::onCardsFetching, this::onCardsFetched, this::onDatabaseError);
        task.execute();

        //This is the action to do when a card is selected on the deck to transfer via nfc
        ExchangeActivity.start(this, "15");      //Replace the value by the id of the selected card to transfer via nfc

        //gift = false;

        //myDialog = new Dialog(this);
        //myDialog.setContentView(R.layout.notificationpopup);
        //myDialog.show();

        createNotificationChannel(); //Creer le channel de notif
        startService(new Intent(this, DailyNotificationService.class));
        //SharedPreferences sharedPreferences = this.getSharedPreferences("availableGift", Context.MODE_PRIVATE);
        //gift = sharedPreferences.getBoolean("gift", false);

    }

    private void onCardsFetching()
    {

    }

    private void onCardsFetched(List<YugiohCard> cards)
    {
        currentDeck = cards;
        deckAdapter.setDataSet(currentDeck);
    }

    private void onCardInserted(Long[] longs)
    {
        Log.i("InsertedInDatabase","inserted");
    }

    private void onInsertingCard()
    {

    }

    private void onDatabaseError()
    {

    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager.getNotificationChannel(CHANNEL_ID) == null)
            {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.setDescription("Daily Rewards");
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setLightColor(R.color.colorPrimary);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                manager.createNotificationChannel(channel);
            }
        }
    }

}
