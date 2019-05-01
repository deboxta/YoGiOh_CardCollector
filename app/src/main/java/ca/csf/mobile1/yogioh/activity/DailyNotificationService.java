package ca.csf.mobile1.yogioh.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ca.csf.mobile1.yogioh.R;

public class DailyNotificationService extends Service
{
    public static final String CHANNEL_ID = "channel";
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private PendingIntent notificationPendingIntent;
    private AlarmManager notificationAlarmManager;
    private PendingIntent pendingNotificationIntent;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, DailyNotificationSetup.class);
        notificationIntent.putExtra("NotificationText", "some text");
        pendingNotificationIntent = PendingIntent.getBroadcast(this, 5, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC_WAKEUP, 5500, pendingNotificationIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}