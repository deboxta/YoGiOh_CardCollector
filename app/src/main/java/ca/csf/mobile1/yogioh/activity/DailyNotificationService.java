package ca.csf.mobile1.yogioh.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;


public class DailyNotificationService extends Service
{
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
        notificationIntent.putExtra("NotificationText", "value");
        pendingNotificationIntent = PendingIntent.getBroadcast(this, 5, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        notificationAlarmManager.set(AlarmManager.RTC_WAKEUP, 5500, pendingNotificationIntent);

        SharedPreferences sharedPreferences = this.getSharedPreferences("availableGift", Context.MODE_PRIVATE);
        boolean gift = sharedPreferences.getBoolean("gift", false);
        int x = 5;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}