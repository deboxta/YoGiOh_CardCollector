package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;

public class InsertOneCardAsyncTask extends AsyncTask<YugiohCard, Void, Long>
{
    private boolean isDataBaseError;

    private ListenerInserting onExecute;
    private ListenerInserted onSuccess;
    private final Runnable onDataBaseError;

    private YugiohCardDAO yugiohCardDAO;

    public InsertOneCardAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerInserting onExecute, ListenerInserted onSuccess, Runnable onDataBaseError)
    {
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        this.yugiohCardDAO = yugiohCardDAO;

        isDataBaseError = false;
    }

    @Override
    protected Long doInBackground(YugiohCard... yugiohCard)
    {
        Long id = null;
        try
        {
            id = yugiohCardDAO.insertOne(yugiohCard[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }
        return id;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsInserted();
    }

    @Override
    protected void onPostExecute(Long id)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsInserted(id);
    }

    public interface ListenerInserted
    {
        void onCardsInserted(Long id);
    }

    public interface ListenerInserting
    {
        void onCardsInserted();
    }
}
