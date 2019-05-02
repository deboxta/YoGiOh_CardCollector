package ca.csf.mobile1.yogioh.activity.Queries.Deck;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;

public class InsertOneCardInDeckAsyncTask extends AsyncTask<YugiohDeckCard, Void, Long>
{
    private boolean isDataBaseError;

    private ListenerInserting onExecute;
    private ListenerInserted onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public InsertOneCardInDeckAsyncTask(YugiohDeckDAO yugiohDeckDAO, ListenerInserting onExecute, ListenerInserted onSuccess, Runnable onDataBaseError)
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
    protected Long doInBackground(YugiohDeckCard... yugiohDeckCards)
    {
        Long cardId = null;

        try
        {
            cardId = yugiohDeckDAO.insertOne(yugiohDeckCards[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return cardId;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardInserting();
    }

    @Override
    protected void onPostExecute(Long cardAdded)
    {
        onSuccess.onCardInserted(cardAdded);
    }

    public interface ListenerInserted
    {
        void onCardInserted(Long cardAdded);
    }

    public interface ListenerInserting
    {
        void onCardInserting();
    }
}
