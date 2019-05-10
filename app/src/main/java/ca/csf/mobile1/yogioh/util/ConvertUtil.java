package ca.csf.mobile1.yogioh.util;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;

public class ConvertUtil
{
    public static long[] convertWrapperToPrimitive(Long[] wrapperIds)
    {
        long[] ids = new long[wrapperIds.length];
        for (int i = 0; i < wrapperIds.length; i++)
        {
            ids[i] = wrapperIds[i];
        }
        return ids;
    }

    public static long[] convertCardIdsFromIntegerToLongArray(List<YugiohDeckCard> cards)
    {
        long[] cardsIds = new long[cards.size()];
        for (int i = 0; i < cards.size(); i++)
        {
            cardsIds[i] = cards.get(i).cardId;
        }
        return cardsIds;
    }

    public static long convertWrapperToPrimitive(Long wrapperId)
    {
        long id;
        id = wrapperId;
        return id;
    }

    public static Long[] convertPrimitiveToWrapper(long[] primitiveIds)
    {
        Long[] ids = new Long[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++)
        {
            ids[i] = primitiveIds[i];
        }
        return ids;
    }
}
