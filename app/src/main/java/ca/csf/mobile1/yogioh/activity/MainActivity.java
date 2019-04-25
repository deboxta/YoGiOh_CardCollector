package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.nfc.ExchangeActivity;
import ca.csf.mobile1.yogioh.repository.database.Database;
import ca.csf.mobile1.yogioh.repository.database.YugiohCard;
import ca.csf.mobile1.yogioh.nfc.ExchangeActivity;

public class MainActivity extends AppCompatActivity
{
    public static final int NOTIFICATION_TIME_TRIGGER_IN_MILLIS = 24000;
    private RecyclerView myDeck;
    private RecyclerView.Adapter deckAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static final String CHANNEL_ID = "channel";
    private PendingIntent pendingNotificationIntent;
    private AlarmManager notificationAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database db = Room.databaseBuilder(getApplicationContext(), Database.class, "database-name").build();

        myDeck = findViewById(R.id.myDeck);
        myDeck.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        myDeck.setLayoutManager(layoutManager);
        deckAdapter = new DeckAdapter(this);
        myDeck.setAdapter(deckAdapter);

        //This is te action to do when a card is selectionned on the deck to transfer via nfc
        Intent intent = new Intent(this, ExchangeActivity.class);
        intent.putExtra("EXTRA_ID", "15");      //Replace the value by the id of the selected card to transfer via nfc
        startActivity(intent);

        createNotificationChannel();
        createPendingNotificationIntent();
        notificationAlarmSetup();
    }

    private void notificationAlarmSetup()
    {
        notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC_WAKEUP, NOTIFICATION_TIME_TRIGGER_IN_MILLIS, pendingNotificationIntent);
    }

    private void createPendingNotificationIntent()
    {
        Intent notificationIntent = new Intent(this, DailyNotificationSetup.class);
        notificationIntent.putExtra("NotificationText", "some text");
        pendingNotificationIntent = PendingIntent.getBroadcast(this, 5, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
        {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager.getNotificationChannel(CHANNEL_ID) == null)
            {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.setDescription("This is a notification");
                channel.shouldVibrate();

                manager.createNotificationChannel(channel);
            }
        }
    }
}
