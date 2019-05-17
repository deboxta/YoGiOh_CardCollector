package ca.csf.mobile1.yogioh.util;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;

public class ConvertUtil
{
    public static long[] convertWrapperToPrimitive(Long[] wrapperIds)
    {
        long[] primitiveIds = new long[wrapperIds.length];
        for (int i = 0; i < wrapperIds.length; i++)
        {
            primitiveIds[i] = wrapperIds[i];
        }
        return primitiveIds;
    }

    public static long[] convertCardIdsFromIntegerToLongArray(List<YugiohDeckCard> yugiohCards)
    {
        long[] convertedCardsIds = new long[yugiohCards.size()];
        for (int i = 0; i < yugiohCards.size(); i++)
        {
            convertedCardsIds[i] = yugiohCards.get(i).cardId;
        }
        return convertedCardsIds;
    }

    public static long convertWrapperToPrimitive(Long wrapperId)
    {
        long id;
        id = wrapperId;
        return id;
    }

    public static Long[] convertPrimitiveToWrapper(long[] primitiveIds)
    {
        Long[] wrapperIds = new Long[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++)
        {
            wrapperIds[i] = primitiveIds[i];
        }
        return wrapperIds;
    }
}
