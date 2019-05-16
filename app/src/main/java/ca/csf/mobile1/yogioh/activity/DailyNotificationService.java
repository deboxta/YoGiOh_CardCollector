package ca.csf.mobile1.yogioh.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.util.AvailableGiftSharedPreferenceUtil;


public class DailyNotificationService extends Service
{
    public static final int NOTIFICATION_DELAY = 15000;
    public static final int PENDING_REQUEST_CODE = 8;
    public static final String CHANNEL_DESCRIPTION = "Daily Rewards";

    private AlarmManager notificationAlarmManager;
    private PendingIntent pendingNotificationIntent;

    public static final String CHANNEL_ID = "channel";
    public static final String CHANNEL_NAME = "Channel";

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Intent notificationIntent = new Intent(this, DailyNotificationSetup.class);
        pendingNotificationIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + NOTIFICATION_DELAY, pendingNotificationIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );

                notificationChannel.setDescription(CHANNEL_DESCRIPTION);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(false);
                notificationChannel.setLightColor(R.color.colorLight);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(notificationChannel);

                AvailableGiftSharedPreferenceUtil.editAvailibilityOfDailyReward(this, true);
            }
        }

    }
}