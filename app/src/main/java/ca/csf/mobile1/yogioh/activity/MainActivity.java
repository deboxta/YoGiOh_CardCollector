package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import ca.csf.mobile1.yogioh.DeckAdapter;
import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.repository.database.Database;
import ca.csf.mobile1.yogioh.repository.database.YugiohCard;
import ca.csf.mobile1.yogioh.nfc.BeamActivity;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView myDeck;
    private RecyclerView.Adapter deckAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static final String CHANNEL_ID = "channel";
    private NotificationManagerCompat notificationManagerCompat;
    Notification notification;

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

        Intent intent = new Intent(this, BeamActivity.class);
        intent.putExtra("EXTRA_ID", "15");
        startActivity(intent);
    }

    private void notificationBuild()
    {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Une notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
    }

    private void sendNotification(View view)
    {
        notificationManagerCompat.notify(2, notification);
    }

    private void createNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("This is a notification");
            channel.shouldVibrate();

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
