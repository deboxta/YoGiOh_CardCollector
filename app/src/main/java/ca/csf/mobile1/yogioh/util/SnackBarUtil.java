package ca.csf.mobile1.yogioh.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import ca.csf.mobile1.yogioh.R;

//BEN_REVIEW : Était-ce vraiment nécessaire ?

public class SnackBarUtil
{
    public static void databaseErrorSnackBar(View rootView)
    {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG).show();
    }
}
