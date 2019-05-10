package ca.csf.mobile1.yogioh.util;

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
