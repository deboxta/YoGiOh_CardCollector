package ca.csf.mobile1.yogioh.activity.queries.deck;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;

public class InsertMultipleCardsInDeckAsyncTask extends AsyncTask<YugiohDeckCard, Void, Long[]>
{
    private boolean isDataBaseError;

    private ListenerInserting onExecute;
    private ListenerInserted onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public InsertMultipleCardsInDeckAsyncTask(YugiohDeckDAO yugiohDeckDAO, ListenerInserting onExecute, ListenerInserted onSuccess, Runnable onDataBaseError)
    {
        if (yugiohDeckDAO == null) throw new IllegalArgumentException("yugiohDeckDAO cannot be null");
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;
        this.yugiohDeckDAO = yugiohDeckDAO;

        isDataBaseError = false;
    }

    @Override
    protected Long[] doInBackground(YugiohDeckCard... yugiohDeckCards)
    {
        Long[] cardsIds = null;

        try
        {
            cardsIds = convertPrimitiveToWrapper(yugiohDeckDAO.insertAll(yugiohDeckCards));
        }
        catch(Exception e)
        {
            isDataBaseError = true;
        }
        return cardsIds;
    }

    private Long[] convertPrimitiveToWrapper(long[] primitiveIds) {
        Long[] ids = new Long[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++) {
            ids[i] = primitiveIds[i];
        }
        return ids;
    }

    @Override
    protected void onPostExecute(Long[] cardsAdded)
    {
        onSuccess.onCardInserted(cardsAdded);
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardInserting();
    }

    public interface ListenerInserted
    {
        void onCardInserted(Long[] cardsAdded);
    }

    public interface ListenerInserting
    {
        void onCardInserting();
    }
}
