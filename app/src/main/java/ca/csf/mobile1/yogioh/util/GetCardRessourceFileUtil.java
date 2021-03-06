package ca.csf.mobile1.yogioh.util;

import android.content.Context;

public class GetCardRessourceFileUtil
{

    private static final String DEF_TYPE_DRAWABLE = "drawable";
    private static final String PACKAGE_PATH = "ca.csf.mobile1.yogioh";

    private static final String CARD_ID = "card";

    public static int getCardRessourceFileId(Context context, int cardID)
    {
        return context.getResources().getIdentifier(CARD_ID + cardID, DEF_TYPE_DRAWABLE, PACKAGE_PATH);
    }
}
