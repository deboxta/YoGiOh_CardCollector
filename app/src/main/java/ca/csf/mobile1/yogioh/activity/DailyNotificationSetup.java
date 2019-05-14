package ca.csf.mobile1.yogioh.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class DailyNotificationSetup extends BroadcastReceiver
{
    public static final int NOTIFICATION_ID = 9;
    public static final int NOTIFICATION_PENDING_REQUEST_CODE = 1;
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private PendingIntent notificationPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent reapeatingIntent = new Intent(context, MainActivity.class);
        reapeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_PENDING_REQUEST_CODE, reapeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManagerCompat = NotificationManagerCompat.from(context);
        notification = new NotificationCompat.Builder(context, DailyNotificationService.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.daily_reward_title))
                .setContentText(context.getResources().getString(R.string.daily_reward_description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);

        AvailableGiftSharedPreferenceUtil.editAvailibilityOfDailyReward(context, true);

        context.startService(new Intent(context, DailyNotificationService.class));

    }
}