package ca.csf.mobile1.yogioh.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.activity.Queries.Card.InsertCardsAsyncTask;
import ca.csf.mobile1.yogioh.activity.Queries.Player.InsertPlayersAsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView myDeck;
    private RecyclerView.LayoutManager layoutManager;

    public static final String CHANNEL_ID = "channel";
    private static final String INSERTIONCARTELOGMESSAGE = "insertionCarte";

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDeckDAO yugiohDeckDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();
        yugiohDeckDAO = yugiohDatabase.yugiohDeckDAO();

        InsertCardsAsyncTask insertCardsAsyncTask = new InsertCardsAsyncTask(yugiohCardDAO,this::onInsertingCard,this::onCardInserted,this::onDatabaseError);
        insertCardsAsyncTask.execute(new YugiohCard());

//        InsertPlayersAsyncTask insertPlayersAsyncTask = new InsertPlayersAsyncTask(yugiohPlayerDAO, this::onInsertingPlayer, this::onPlayerInserted, this::onDatabaseError);
//        insertPlayersAsyncTask.execute(new YugiohPlayer());

        myDeck = findViewById(R.id.myDeck);
        myDeck.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        myDeck.setLayoutManager(layoutManager);
        //deckAdapter = new DeckAdapter(this, db.yugiohDAO().selectAll());
        // myDeck.setAdapter(deckAdapter);

        createNotificationChannel(); //Creer le channel de notif
        startService(new Intent(this, DailyNotificationService.class));
    }

    private void onPlayerInserted(Long[] longs) {

    }

    private void onInsertingPlayer() {

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
