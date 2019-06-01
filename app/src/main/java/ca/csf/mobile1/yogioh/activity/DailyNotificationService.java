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
import ca.csf.mobile1.yogioh.util.ConstantsUtil;


public class DailyNotificationService extends Service
{
    //One day
    private static final int NOTIFICATION_DELAY = 86400000;
    private static final int PENDING_REQUEST_CODE = 8;

    private static final String NOT_YET_IMPLEMENTED_EXCEPTION_STRING = "Not yet implemented";

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED_EXCEPTION_STRING);
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
        PendingIntent pendingNotificationIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + NOTIFICATION_DELAY, pendingNotificationIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel()
    {
        //BEN_CORRECTION : Et si c'est pas Android O ? Si c'est plus bas ? Pas de notifications pour ceux qui ont plus
        //                 bas que Android O ? Défaut de logique applicative.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager.getNotificationChannel(ConstantsUtil.CHANNEL_ID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(ConstantsUtil.CHANNEL_ID, ConstantsUtil.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(ConstantsUtil.CHANNEL_DESCRIPTION);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(false);
                notificationChannel.setLightColor(R.color.colorLight);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(notificationChannel);

                AvailableGiftSharedPreferenceUtil.editAvailabilityOfDailyReward(this, true);
            }
        }

    }
}