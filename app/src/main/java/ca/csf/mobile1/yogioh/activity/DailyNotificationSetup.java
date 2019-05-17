package ca.csf.mobile1.yogioh.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;
import ca.csf.mobile1.yogioh.util.ConstantsUtil;

public class DailyNotificationSetup extends BroadcastReceiver
{

    private static final int NOTIFICATION_ID = 11;
    private static final int NOTIFICATION_PENDING_REQUEST_CODE = 6;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_PENDING_REQUEST_CODE, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, ConstantsUtil.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.daily_reward_title))
                .setContentText(context.getResources().getString(R.string.daily_reward_description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setVibrate(null)
                .build();

        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        AvailableGiftSharedPreferenceUtil.editAvailabilityOfDailyReward(context, true);
        context.startService(new Intent(context, DailyNotificationService.class));
    }
}