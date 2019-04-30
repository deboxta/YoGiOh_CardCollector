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
    public static final int NOTIFICATION_TIME_TRIGGER_IN_MILLIS = 24000;
    private RecyclerView myDeck;
    private RecyclerView.Adapter deckAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static final String CHANNEL_ID = "channel";
    private PendingIntent pendingNotificationIntent;
    private AlarmManager notificationAlarmManager;
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private Calendar calendar;
    private AlarmManager notificationAlarmManagerREAPEAT;

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

        //TO A.B : j'doit demander à BEN si les daily sont font spécifiquement (on peut genre tricher en se moment si on change la date)
        //Tout est en commentaire pour pas faire chier le projet.
        //setupCalendar(); //Setup le calendrier pour les rapelles
        //createNotificationChannel(); //Creer le channel de notif
        //createNotification(); //Cree une notification
        //createPendingNotificationIntent(); //Cree une notif pendante
        //notificationAlarmSetup(); //Setup un "alarm"
        //repeatNotification(); //Setup un alarm repetif

    }

    private void repeatNotification()
    {
        Intent reapeatingNotif = new Intent(this, DailyNotificationSetup.class);

        PendingIntent repeatIntentPending = PendingIntent.getBroadcast(this, 0, reapeatingNotif, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationAlarmManagerREAPEAT = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManagerREAPEAT.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, repeatIntentPending);
    }

    private void setupCalendar()
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
    }

    private void createNotification()
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Daily Reward Available")
                .setContentText("Come and get ur reward!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notificationPendingIntent)
                .build();

        notificationManagerCompat.notify(0, notification);
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
