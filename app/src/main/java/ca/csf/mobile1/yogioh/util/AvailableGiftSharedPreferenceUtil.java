package ca.csf.mobile1.yogioh.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AvailableGiftSharedPreferenceUtil
{

    private static final String SHARED_PREFERENCE_ID = "availableGift";

    public static void editAvailabilityOfDailyReward(Context context, boolean trueOrFalse)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHARED_PREFERENCE_ID, trueOrFalse);
        editor.apply();
    }

    //BEN_REVIEW : Par convention, toute méthode retournant un boolean (et qui répond à une question) débute
    //             pas "is" ou "has".
    //
    //             Dans ce cas ci, cela donnerait "isDailyRewardAvailable".
    //
    //             C'est tout de même clair ce que vous avez écrit, mais c'est juste pas idéal.
    public static boolean getAvailabilityOfDailyReward(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SHARED_PREFERENCE_ID, true);
    }
}
