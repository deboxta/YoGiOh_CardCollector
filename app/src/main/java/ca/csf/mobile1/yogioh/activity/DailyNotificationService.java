package ca.csf.mobile1.yogioh.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import ca.csf.mobile1.yogioh.R;


public class DailyNotificationService extends Service
{
    public static final int NOTIFICATION_DELAY = 5500;
    public static final int PENDING_REQUEST_CODE = 5;
    private AlarmManager notificationAlarmManager;
    private PendingIntent pendingNotificationIntent;

    public static final String CHANNEL_ID = "channel";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, DailyNotificationSetup.class);
        pendingNotificationIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC_WAKEUP, NOTIFICATION_DELAY, pendingNotificationIntent);

        SharedPreferences sharedPreferences = this.getSharedPreferences("availableGift", Context.MODE_PRIVATE);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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