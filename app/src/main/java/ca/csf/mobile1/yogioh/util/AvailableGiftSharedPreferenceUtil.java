package ca.csf.mobile1.yogioh.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AvailableGiftSharedPreferenceUtil
{

    public static final String SHARED_PREFERENCE_ID = "availableGift";

    public static void editAvailibilityOfDailyReward(Context context, boolean trueOrFalse)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHARED_PREFERENCE_ID, trueOrFalse);
        editor.apply();
    }

    public static boolean getAvailibilityOfDailyReward(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SHARED_PREFERENCE_ID, true);
    }
}
