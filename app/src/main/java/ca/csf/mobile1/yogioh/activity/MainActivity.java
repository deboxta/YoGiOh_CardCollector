package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Calendar;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView myDeck;
    private RecyclerView.Adapter deckAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static final String CHANNEL_ID = "channel";

    private YugiohDatabase yugiohDatabase;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yugiohDatabase = Room.databaseBuilder(getApplicationContext(), YugiohDatabase.class, "yugiohDatabase").build();
        yugiohCardDAO = yugiohDatabase.yugiohCardDao();
        yugiohPlayerDAO = yugiohDatabase.yugiohPlayerDAO();

        myDeck = findViewById(R.id.myDeck);
        myDeck.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        myDeck.setLayoutManager(layoutManager);
        //deckAdapter = new DeckAdapter(this, db.yugiohDAO().selectAll());
       // myDeck.setAdapter(deckAdapter);

        //This is the action to do when a card is selected on the deck to transfer via nfc
        Intent intent = new Intent(this, ExchangeActivity.class);
        intent.putExtra("EXTRA_ID", "15");      //Replace the value by the id of the selected card to transfer via nfc
        startActivity(intent);

        //createNotificationChannel(); //Creer le channel de notif
        //startService(new Intent(this, DailyNotificationService.class));
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
