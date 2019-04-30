package ca.csf.mobile1.yogioh.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ca.csf.mobile1.yogioh.R;

public class DailyNotificationSetup extends BroadcastReceiver
{

    public static final String CHANNEL_ID = "channel";
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private PendingIntent notificationPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        /*notificationManagerCompat = NotificationManagerCompat.from(context);
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Une notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(8, notification);*/

        notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent reapeatingIntent = new Intent(context, MainActivity.class);
        reapeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notificationPendingIntent = PendingIntent.getActivity(context, 1, reapeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManagerCompat = NotificationManagerCompat.from(context);
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Daily Reward Available")
                .setContentText("Come and get ur reward!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(8, notification);
    }
}