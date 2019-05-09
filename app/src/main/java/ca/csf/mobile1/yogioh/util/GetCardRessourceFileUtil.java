package ca.csf.mobile1.yogioh.util;

import android.content.Context;

public class GetCardRessourceFileUtil
{
    public static int getCardRessourceFileId(Context context, String cardID)
    {
        return context.getResources().getIdentifier(cardID,"drawable","ca.csf.mobile1.yogioh");
    }
}
